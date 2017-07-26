# This package defines names for EMS entities

# suresh@nlm.nih.gov 3/2005

package EMSNames;
BEGIN
{
unshift @INC, "$ENV{ENV_HOME}/bin";
require "env.pl";
unshift @INC, "$ENV{EMS_HOME}/lib";
unshift @INC, "$ENV{EMS_HOME}/bin";
}
$PREFIX = "EMS3";
$TMPTABLEPREFIX = $PREFIX . "TMP";
$TMPFILEPREFIX = $PREFIX . "TMP";

# tables
$MAXTABTABLE = $PREFIX . "_". "MAXTAB";
$MEBINSTABLE = $PREFIX . "_" . "MEBINS";
$QABINSTABLE = $PREFIX . "_" . "QABINS";
$AHBINSTABLE = $PREFIX . "_" . "AHBINS";
$MECONFIGTABLE = $PREFIX . "_" . "MECONFIG";
$QACONFIGTABLE = $PREFIX . "_" . "QACONFIG";
$AHCONFIGTABLE = $PREFIX . "_" . "AHCONFIG";
$BININFOTABLE = $PREFIX . "_" . "BININFO";
$BINLOCKTABLE = $PREFIX . "_" . "BINLOCK";
$BEINGEDITEDTABLE = $PREFIX . "_" . "BEINGEDITED";
$AHCANONICALNAMETABLE = $PREFIX . "_AHCANONICALNAME";
$AHHISTORYTABLE = $PREFIX . "_AHHISTORY";
# preserve old names for the following tables since MEME uses them
$WORKLISTINFOTABLE = "WMS_WORKLIST_INFO";
$CHECKLISTINFOTABLE = "EMS_CHECKLIST_INFO";

$EDITINGEPOCHTABLE = $PREFIX . "_" . "EDITING_EPOCH";
$CHEMCONCEPTSTABLE = $PREFIX . "_" . "CHEMCONCEPTS";
$CLINICALCONCEPTSTABLE = $PREFIX . "_" . "CLINICALCONCEPTS";
$OTHERCONCEPTSTABLE = $PREFIX . "_" . "OTHERCONCEPTS";
$DAILYSNAPSHOTTABLE = $PREFIX . "_" . "DAILY_SNAPSHOT";
$DAILYACTIONCOUNTTABLE = $PREFIX . "_" . "DAILY_ACTIONCOUNT";
$SOURCESTATSTABLE = $PREFIX . "_" . "SOURCESTATS";
$STYCOOCTABLE = $PREFIX . "_" . "STYCOOC";

# Max tab keys

# ME data tracking
$MEPARTITIONINGLOCKKEY = "MEPARTITIONINGLOCK";
$MEWORKLISTLOCKKEY = "MEWORKLISTLOCK";
$MEPARTITIONDATEKEY = "MEPARTITIONDATE";
$MEPARTITIONTIMEKEY = "MEPARTITIONTIME";
$MEPARTITIONUSERKEY = "MEPARTITIONUSER";

# access control (beyond that in the config file)
$EMSACCESSKEY = "EMSACCESSKEY";

$TMPTABLENUMKEY = "TMPTABLENUM";
$EMSSTATEKEY = "EMSSTATE"; # readonly, readwrite, closed
$NEXTREPORTIDKEY = "NEXTREPORTID";
# when where the config files loaded into the DB?
$MECONFIGLOADTIMEKEY = "MECONFIGLOADTIME";
$QACONFIGLOADTIMEKEY = "QACONFIGLOADTIME";
$AHCONFIGLOADTIMEKEY = "AHCONFIGLOADTIME";

# cutoff for batch EMS processes
$EMSBATCHCUTOFFKEY = "EMSBATCHCUTOFF";

# directories, files
$TMPDIR = (-d $ENV{'EMS_HOME'} . "/tmp" ? $ENV{'EMS_HOME'} . "/tmp" : "/tmp");

#----------------------------------------
1;