/*****************************************************************************
 *
 * Package: gov.nih.nlm.meme.qa.ic
 * Object:  MGV_ETest
 * 
 * 04/07/2006 RBE (1-AV8WP): File created
 *  
 *****************************************************************************/

package gov.nih.nlm.meme.integrity;

import gov.nih.nlm.meme.common.Atom;
import gov.nih.nlm.meme.common.Concept;
import gov.nih.nlm.meme.common.Identifier;
import gov.nih.nlm.meme.common.NativeIdentifier;
import gov.nih.nlm.meme.common.Relationship;
import gov.nih.nlm.meme.common.Source;
import gov.nih.nlm.meme.common.Termgroup;
import junit.framework.TestCase;

public class MGV_ETest extends TestCase {

	private Concept source = null;
	private Concept target = null;
	private MGV_E ic = null;

	/* Code to set up a fresh scaffold for each test */
	protected void setUp() throws Exception {
		super.setUp();
		source = null;
		target = null;
		ic = new MGV_E();
		UnaryCheckData[] ucds = new UnaryCheckData[] {
				new UnaryCheckData("MGV_E", "SOURCE", "MMM", false) };
		ic.setCheckData(ucds);
		ic.setIsActive(true);
		ic.setIsFatal(true);
		ic.setShortDescription("Short description of MGV_E");
		ic.setDescription("Description of MGV_E.");

	}

	/* Code to destroy the scaffold after each test */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//
	// Test and assert success
	//

	
	public void testViolationNotFoundEmptyRels() {
		System.out.println("MGV_ETest#testViolationNotFoundEmptyRels()");
		createSourceConcept();
		createTargetConcept();
		assertTrue("No violation expected.", !ic.validate(source, target));
	}

	public void testViolationFound() {
		System.out.println("MGV_ETest#testViolationFound()");
		createSourceConceptSetSAB();
		createTargetConceptSetSAB();
		assertTrue("Violation expected.", ic.validate(source, target));
	}
	
	public void testViolationNotFoundSetTBR() {
		System.out.println("MGV_ETest#testViolationNotFoundSetTBR()");
		createSourceConceptSetTBR();
		createTargetConceptSetTBR();
		assertTrue("No violation expected.", !ic.validate(source, target));
	}

	public void testViolationNotFoundSetName() {
		System.out.println("MGV_ETest#testViolationNotFoundSetName()");
		createSourceConceptSetName();
		createTargetConceptSetName();
		assertTrue("No violation expected.", !ic.validate(source, target));
	}

	public void testViolationNotFoundSetStatus() {
		System.out.println("MGV_ETest#testViolationNotFoundSetStatus()");
		createSourceConceptSetStatus();
		createTargetConceptSetStatus();
		assertTrue("No violation expected.", !ic.validate(source, target));
	}

	public void testViolationNotFoundSetRelatedConcept() {
		System.out.println("MGV_ETest#testViolationNotFoundSetRelatedConcept()");
		createSourceConceptSetRelatedConcept();
		createTargetConceptSetRelatedConcept();
		assertTrue("No violation expected.", !ic.validate(source, target));
	}

	//
	// Test Cases
	//

	private void createSourceConcept() {
		source = new Concept.Default();
		Atom atom = createAtom();
		atom.setConcept(source);
		source.addAtom(atom);
	}

	private void createTargetConcept() {
		target = new Concept.Default();
		Atom atom = createAtom();
		atom.setConcept(target);
		target.addAtom(atom);
	}

	private void createSourceConceptSetSAB() {
		source = new Concept.Default();
		Atom atom = createAtom();
        source.addRelationship(createRelationship());    
		atom.setConcept(source);
		source.addAtom(atom);
	}

	private void createTargetConceptSetSAB() {
		target = new Concept.Default();
		Atom atom = createAtom();
        target.addRelationship(createRelationship());    
		atom.setConcept(target);
		target.addAtom(atom);
	}

	private void createSourceConceptSetTBR() {
		source = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setTobereleased('N');
        source.addRelationship(rel);    
		atom.setConcept(source);
		source.addAtom(atom);
	}

	private void createTargetConceptSetTBR() {
		target = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setTobereleased('N');
        target.addRelationship(rel);    
		atom.setConcept(target);
		target.addAtom(atom);
	}

	private void createSourceConceptSetName() {
		source = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setName("RT?");
        source.addRelationship(rel);    
		atom.setConcept(source);
		source.addAtom(atom);
	}

	private void createTargetConceptSetName() {
		target = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setName("RT?");
        target.addRelationship(rel);    
		atom.setConcept(target);
		target.addAtom(atom);
	}

	private void createSourceConceptSetStatus() {
		source = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setStatus('N');
        source.addRelationship(rel);    
		atom.setConcept(source);
		source.addAtom(atom);
	}

	private void createTargetConceptSetStatus() {
		target = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setStatus('N');
        target.addRelationship(rel);    
		atom.setConcept(target);
		target.addAtom(atom);
	}

	private void createSourceConceptSetRelatedConcept() {
	    Concept concept = new Concept.Default();
	    concept.setIdentifier(new Identifier.Default(56789));
		source = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setRelatedConcept(concept);
        source.addRelationship(rel);    
		atom.setConcept(source);
		source.addAtom(atom);
	}

	private void createTargetConceptSetRelatedConcept() {
	    Concept concept = new Concept.Default();
	    concept.setIdentifier(new Identifier.Default(56789));
		target = new Concept.Default();
		Atom atom = createAtom();
	    Relationship rel = createRelationship();
	    rel.setRelatedConcept(concept);
        target.addRelationship(rel);    
		atom.setConcept(target);
		target.addAtom(atom);
	}

	//
	// Helper method(s)
	//
	private Atom createAtom() {
		Atom atom = new Atom.Default();
		atom.setString("TEST ATOM");
		atom.setTermgroup(new Termgroup.Default("MTH/PN"));		
		atom.setSource(createSource());
		atom.setStatus('R');
		atom.setGenerated(true);
		atom.setReleased('N');
		atom.setSuppressible("N");
		atom.setTobereleased('Y');
		atom.setConcept(source);
		return atom;
	}
	
	private Source createSource() {
		Source src = new Source.Default("MTH");
		src.setStrippedSourceAbbreviation("MTH");
		src.setIsCurrent(true);
		return src;
	}

	private Relationship createRelationship() {
	    Relationship rel = new Relationship.Default();
	    rel.setAtom(createAtom());
	    rel.setConcept(source);
	    target = new Concept.Default();
	    target.setIdentifier(new Identifier.Default(12345));
	    rel.setRelatedConcept(target);
	    rel.setName("RT");
	    rel.setAttribute("mapped_to");
	    rel.setSource(createSource());
	    rel.setSourceOfLabel(createSource());
	    rel.setStatus('R');
	    rel.setGenerated(false);
	    rel.setLevel('S');
	    rel.setReleased('A');
	    rel.setTobereleased('Y');
	    rel.setSuppressible("N");
	    rel.setNativeIdentifier(new NativeIdentifier("", "", "", "", ""));
	    rel.setRelatedNativeIdentifier(new NativeIdentifier("", "", "", "", ""));	      
	    return rel;
	}
	
}