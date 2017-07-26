/*****************************************************************************
 *
 * Package:    com.lexical.meme.recipe.steps
 * Object:     UpdateReleasabilityStep.java
 *
 * Changes
 * 02/24/2009 BAC (1-GCLNT): Parallelize operation
 *   11/15/2006 BAC (1-CTLEE): mail.pl call fixed to correctly send mail
 *****************************************************************************/
package gov.nih.nlm.recipe.steps;

import gov.nih.nlm.recipe.RxConstants;
import gov.nih.nlm.recipe.RxStep;
import gov.nih.nlm.recipe.RxToolkit;
import gov.nih.nlm.swing.SuperJList;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

/**
 * This step is used to cause a sources default semantic types to either "win"
 * or to "lose"
 * 
 * @author Brian Carlsen
 * @version 1.0
 */

public class UpdateReleasabilityStep extends RxStep {

	//
	// Fields
	//
	protected String[] sources;
	protected String tbr;
	protected String comments;

	/**
	 * UpdateReleasabilityStep default constructor
	 */
	public UpdateReleasabilityStep() {
		super();
		resetValues();
	};

	/**
	 * This method resets the internal values to defaults
	 */
	public void resetValues() {
		sources = new String[] {};
		tbr = "n";
		comments = "";
	};

	/**
	 * Create & return an instance of inner class View
	 * 
	 * @return RxStep.View
	 */
	public RxStep.View constructView() {
		return new UpdateReleasabilityStep.View();
	};

	/**
	 * This method generates some kind of online help and then returns. Most
	 * simply it will produce a dialog box.
	 */
	public void getHelp() {
		RxToolkit.notifyUser("Help is not currently available.");
	};

	/**
	 * This method will be overridden by the subclasses' method. It returns the
	 * HTML representation of the step.
	 * @return String
	 */
	public String toHTML() {
		StringBuffer step_text = new StringBuffer();

		step_text
				.append("            <table ALIGN=CENTER WIDTH=90% BORDER=1 CELLSPACING=1 CELLPADDING=2>\n");
		step_text.append("              <tr><td><b>Sources:</b></td><td>\n");
		for (int i = 0; i < sources.length; i++) {
			step_text.append("                   ");
			step_text.append(sources[i]);
			step_text.append("\n");
		}
		step_text.append("              </tr>\n");
		step_text.append("              <tr><td><b>TBR:</b></td><td>\n");
		step_text.append("                 ");
		step_text.append(tbr);
		step_text.append("</td></tr>\n");

		if (!comments.equals("")) {
			step_text.append("              <tr>\n");
			step_text.append("                <td><b>Comments:</b></td><td>");
			step_text.append(comments);
			step_text.append("</td></tr>\n");
		}
		step_text.append("            </table>\n");

		return step_text.toString();
	};

	/**
	 * This method generates code for a shell script to perform the post-insert
	 * merge operation
	 */
	public String toShellScript() {
		StringBuffer body = new StringBuffer(500);
		body.append("#\n# Update Releasability\n");
		if (comments != null && !comments.equals("")) {
			body.append("# ").append(comments).append("\n");
		}

		body.append("#\n");
		body
				.append("# Map obsolete rels, update releasability, and insert bequeathal rels\n");
		body.append("#\n");
		body
				.append("foreach value (`$MEME_HOME/bin/dump_table.pl -u $user -d $db -q \"select a.high_source,a.low_source,b.stripped_source from source_source_rank a, source_rank b where a.low_source=b.source and a.stripped_source=b.stripped_source\"`)\n"
						+ "    set ns=`echo $value | perl -ne 'split /\\|/; print \"$_[0]\\n\";'`\n"
						+ "    set os=`echo $value | perl -ne 'split /\\|/; print \"$_[1]\\n\";'`\n"
						+ "    set ss=`echo $value | perl -ne 'chop;split /\\|/; print \"$_[2]\\n\";'`\n"
						+ "    echo \"    Map Obsolete Relationships ($ss) ...`/bin/date`\"\n"
						+ "\n"
						+ "    $ORACLE_HOME/bin/sqlplus -s $user@$db <<EOF >&! /tmp/t.$$.log\n"
						+ "        WHENEVER SQLERROR EXIT -2\n"
						+ "        set serveroutput on size 100000\n"
						+ "        set feedback off\n"
						+ "        ALTER SESSION SET sort_area_size=200000000;\n"
						+ "        ALTER SESSION SET hash_area_size=200000000;\n"
						+ "        exec MEME_SOURCE_PROCESSING.map_obsolete_rels (-\n"
						+ "            stripped_source => '$ss', -\n"
						+ "            authority => '$authority', -\n"
						+ "            work_id => $work_id );\n"
						+ "EOF\n"
						+ "    if ($status != 0) then\n"
						+ "        echo \"Error mapping obsolete rels\"\n"
						+ "        cat /tmp/t.$$.log\n"
						+ "    if ($?to == 1)  eval $MEME_HOME/bin/mail.pl $subject_flag $to_from_flags '-message=\"Error mapping obsolete rels\"' \n"
						+ "        exit 1\n"
						+ "    endif\n"
						+ "\n"
						+ "    echo \"    Update Releasability ($os)...`/bin/date`\"\n"
						+ "    $ORACLE_HOME/bin/sqlplus -s $user@$db <<EOF  >&! /tmp/t.ur.$ss.$$.log &\n"
						+ "        WHENEVER SQLERROR EXIT -2\n"
						+ "        set serveroutput on size 100000\n"
						+ "        set feedback off\n"
						+ "        ALTER SESSION SET sort_area_size=200000000;\n"
						+ "        ALTER SESSION SET hash_area_size=200000000;\n"
						+ "        exec MEME_SOURCE_PROCESSING.update_releasability (-\n"
						+ "            old_source => '$os', -\n"
						+ "            new_source => '$ns', -\n"
						+ "            authority => '$authority', -\n"
						+ "            new_value => 'n', -\n"
						+ "            work_id => $work_id );\n"
						+ "EOF\n"
						+ "\n"
						+ "end\n\n");

		/**
		 * body.append("#\n"); body.append(
		 * "echo \"    Map Obsolete Relationships ...`/bin/date`\"\n\n");
		 * body.append("set stripped_source=`$MEME_HOME/bin/dump_table.pl -u $user -d $db -q \"select source from source_version where current_name='$new_source'\"`\n"
		 * ); body.append(
		 * "$ORACLE_HOME/bin/sqlplus -s $user@$db <<EOF >&! /tmp/t.$$.log\n" +
		 * "    WHENEVER SQLERROR EXIT -2\n" +
		 * "    set serveroutput on size 100000\n" + "    set feedback off\n" +
		 * "    ALTER SESSION SET sort_area_size=200000000;\n" +
		 * "    ALTER SESSION SET hash_area_size=200000000;\n" +
		 * "    exec MEME_SOURCE_PROCESSING.map_obsolete_rels (-\n" +
		 * "	stripped_source => '$stripped_source', -\n" +
		 * "	authority => '$authority', -\n" + "	work_id => $work_id );\n" + "EOF\n"
		 * + "if ($status != 0) then\n" +
		 * "    echo \"Error mapping obsolete rels\"\n" + "    cat /tmp/t.$$.log\n"
		 * + "    exit 1\n" + "endif\n");
		 * 
		 * body.append("echo \"    Update Releasability ...`/bin/date`\"\n\n"); for
		 * (int i = 0; i < sources.length; i++) { body.append(
		 * "$ORACLE_HOME/bin/sqlplus -s $user@$db <<EOF >&! /tmp/t.$$.log\n" +
		 * "    WHENEVER SQLERROR EXIT -2\n" +
		 * "    set serveroutput on size 100000\n" + "    set feedback off\n" +
		 * "    ALTER SESSION SET sort_area_size=200000000;\n" +
		 * "    ALTER SESSION SET hash_area_size=200000000;\n" +
		 * "    exec MEME_SOURCE_PROCESSING.update_releasability (-\n" +
		 * "	old_source => '"); body.append(sources[i]).append("', -\n" +
		 * "	new_source => '$new_source', -\n" + "	authority => '$authority', -\n" +
		 * "	new_value => 'n', -\n" + "	work_id => $work_id );\n" + "EOF\n" +
		 * "if ($status != 0) then\n" +
		 * "    echo \"Error updating releasability\"\n" + "    cat /tmp/t.$$.log\n"
		 * + "    exit 1\n" + "endif\n\n"); }
		 ***/
		body.append("#\n# Bequeath old versioned SRC concepts\n#\n"
				+ "set old_source_table=`echo $ss | sed 's/-/_/; s/\\.//'`\n"
				+ "$ORACLE_HOME/bin/sqlplus -s $user@$db <<EOF >&! /tmp/t.$$.log\n"
				+ "    WHENEVER SQLERROR EXIT -2\n"
				+ "    set serveroutput on size 100000\n" + "    set feedback off\n"
				+ "\n"
				+ "    exec MEME_UTILITY.drop_it('table','t_$old_source_table');    \n"
				+ "    CREATE TABLE t_$old_source_table (\n" + "	row_id NUMBER );\n");
		for (int i = 0; i < sources.length; i++) {
			body.append("    -- Remove ").append(sources[i]).append(
					"\n" + "    INSERT INTO t_$old_source_table\n"
							+ "    SELECT atom_id FROM classes\n"
							+ "    WHERE source = 'SRC'\n" + "      AND concept_id IN\n"
							+ "      (SELECT concept_id FROM classes a, atoms b\n"
							+ "       WHERE source='SRC'\n"
							+ " 	 AND termgroup = 'SRC/VAB'\n"
							+ "	 AND a.atom_id = b.atom_id\n" + "	 AND atom_name = '")
					.append(sources[i]).append("' );\n\n");
		}
		body
				.append("    exec MEME_UTILITY.drop_it('table','t_rel_$old_source_table');\n"
						+ "    CREATE TABLE t_rel_$old_source_table AS\n"
						+ "    SELECT  concept_id_1,concept_id_2,atom_id_1,atom_id_2,\n"
						+ "                   relationship_name,relationship_attribute,\n"
						+ "                   source, source_of_label,status,generated_status,\n"
						+ "                   relationship_level,released,tobereleased,\n"
						+ "                   relationship_id, suppressible,\n"
						+ "                   sg_id_1, sg_type_1, sg_qualifier_1,\n"
						+ "                   sg_id_2, sg_type_2, sg_qualifier_2\n"
						+ "    FROM relationships WHERE 1=0;\n"
						+ "\n"
						+ "    INSERT INTO t_rel_$old_source_table\n"
						+ "    SELECT concept_id_1,concept_id_2,0,0,\n"
						+ "                   'BRT','',\n"
						+ "                   'MTH', 'MTH','R','Y',\n"
						+ "                   'C', 'N', 'Y', 0, 'N','','','','','',''\n"
						+ "    FROM relationships \n"
						+ "    WHERE atom_id_1 IN (SELECT * FROM t_$old_source_table) \n"
						+ "      AND relationship_attribute = 'has_version'\n"
						+ "      AND relationship_level = 'S'\n"
						+ "    UNION\n"
						+ "    SELECT concept_id_2,concept_id_1,0,0,\n"
						+ "                   'BRT','',\n"
						+ "                   'MTH', 'MTH','R','Y',\n"
						+ "                   'C', 'N', 'Y', 0, 'N','','','','','',''\n"
						+ "    FROM relationships \n"
						+ "    WHERE atom_id_2 IN (SELECT * FROM t_$old_source_table) \n"
						+ "      AND relationship_attribute = 'version_of'\n"
						+ "      AND relationship_level = 'S';\n"
						+ "\n"
						+ "EOF\n"
						+ "if ($status != 0) then\n"
						+ "    echo 'Error preparing bequeathal rels'\n"
						+ "    cat /tmp/t.$$.log\n"
						+ "    if ($?to == 1)  eval $MEME_HOME/bin/mail.pl $subject_flag $to_from_flags '-message=\"EError preparing bequeathal rels\"' \n"
						+ "    exit 1\n"
						+ "endif\n"
						+ "\n"
						+ "$MEME_HOME/bin/insert.pl -w $work_id -host=$host -port=$port -rels t_rel_$old_source_table $db $new_source >&! insert.bequeathal.log \n"
						+ "\n"
						+ "#\n"
						+ "# Clean up temp tables\n"
						+ "#\n"
						+ "$ORACLE_HOME/bin/sqlplus -s $user@$db <<EOF >&! /tmp/t.$$.log\n"
						+ "    set serveroutput on size 100000\n"
						+ "    set feedback off\n"
						+ "    exec MEME_UTILITY.drop_it('table','t_$old_source_table');    \n"
						+ "    exec MEME_UTILITY.drop_it('table','t_rel_$old_source_table');    \n"
						+ "EOF\n");
		return body.toString();

	}

	/**
	 * This method returns an HTML representation of the step type for use in
	 * rendering the section header for a recipe step
	 * @return String
	 */
	public String typeToHTML() {
		return "<h3>" + typeToString() + "</h3>";
	};

	/**
	 * This method returns an string representation of the step type for use in
	 * rendering the step in JLists and JTables.
	 * @return String
	 */
	public String toString() {
		return typeToString();
	};

	/**
	 * This method returns a descriptive name for the type of step.
	 * @return String
	 */
	public static String typeToString() {
		RxToolkit.trace("UpdateReleasabilityStep::typeToString()");
		return "Update Source Releasability";
	};

	/**
	 * Inner class returned by getView();
	 */
	public class View extends RxStep.View {

		//
		// View Fields
		//
		private SuperJList jsources = new SuperJList(RxToolkit.getSources());
		private SuperJList jtbr =
				new SuperJList(RxToolkit.DBToolkit.getTobereleased());
		private JTextArea jcomments = new JTextArea();

		/**
		 * Constructor
		 */
		public View() {
			super();
			RxToolkit.trace("UpdateReleasabilityStep.View::View()");
			initialize();
		};

		/**
		 * This sets up the JPanel
		 */
		private void initialize() {
			RxToolkit.trace("UpdateReleasabilityStep.View::initialize()");

			setLayout(new BorderLayout());
			JPanel data_panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = GridBagConstraints.RELATIVE;
			c.insets = RxConstants.GRID_INSETS;

			// Create an RxStep.DataChangeListener.
			DataChangeListener dcl = new DataChangeListener();

			// Add source list
			jsources.setVisibleRowCount(5);
			jsources.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			jsources.addListSelectionListener(dcl);
			JScrollPane jscroll = new JScrollPane();
			jscroll.setViewportView(jsources);

			c.gridy = 0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			data_panel.add(new JLabel(typeToString()), c);

			c.gridy++;
			c.gridwidth = 1;
			data_panel.add(new JLabel("Sources:"), c);
			data_panel.add(jscroll, c);

			// add tbr
			c.gridy++;
			data_panel.add(new JLabel("TBR:"), c);
			jtbr.resizeList(1, 5);
			jtbr.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jtbr.addListSelectionListener(dcl);
			data_panel.add(jtbr, c);

			c.gridy++;
			data_panel.add(new JLabel("Comments:"), c);
			c.gridy++;
			c.gridwidth = GridBagConstraints.REMAINDER;
			jcomments.setRows(3);
			jcomments.getDocument().addDocumentListener(dcl);
			JScrollPane comment_scroll = new JScrollPane();
			comment_scroll.setViewportView(jcomments);
			data_panel.add(comment_scroll, c);

			add(data_panel);

		};

		//
		// Implementation of RxStep.View methods
		//

		/**
		 * Set the focus
		 */
		public void setFocus() {
			jsources.requestFocus();
		}

		/**
		 * This takes values from the step and displays them.
		 */
		public void getValues() {
			RxToolkit.trace("UpdateReleasabilityStep.View::getValues()");
			String old_source_name;
			if (sources.length == 0) {
				old_source_name =
						(String) UpdateReleasabilityStep.this.parent.getParent()
								.getAttribute("old_source_name");
				sources = new String[] {
					old_source_name
				};
				jsources.setSelectedValues(sources, true);
			}
			jsources.setSelectedValues(sources, true);
			jtbr.setSelectedValue(tbr, true);
			jcomments.setText(comments);
			has_data_changed = false;
		}

		/**
		 * This method is overridden by subclasses. It takes a step and puts the
		 * values from the GUI.
		 */
		public void setValues() {
			RxToolkit.trace("UpdateReleasabilityStep.View::setValues()");
			sources = RxToolkit.toStringArray(jsources.getSelectedValues());
			tbr = (String) jtbr.getSelectedValue();
			comments = jcomments.getText();
			has_data_changed = false;
		};

		/**
		 * This method is overridden by subclasses It validates the input with
		 * respect to the underlying step
		 */
		public boolean checkUserEntry() {
			RxToolkit.trace("UpdateReleasabilityStep.View::checkUserEntry()");
			// Must select a source
			if (jsources.getSelectedValue() == null) {
				RxToolkit.reportError("You must fill out the 'Sources:' field");
				return false;
			}
			// Must select a tbr value
			if (jtbr.getSelectedValue() == null) {
				RxToolkit.reportError("You must fill out the 'TBR:' field");
				return false;
			}
			return true;
		};

	}

}
