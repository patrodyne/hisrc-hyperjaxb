#!/bin/sh
#
# recreate-pg-schema.sh - Drop / Create PostgreSQL HyperJAXB Table Schemas
#
# The HyperJAXB project creates test tables on either a local H2 database
# or a remote PostgreSQL database server. The choice is configured using
# these Java system properties in build.sh, etc.:
#
#   -Dorg.jvnet.hyperjaxb.persistencePropertiesBaseFile=persistence.properties
#   -Dorg.jvnet.hyperjaxb.persistencePropertiesMoreFile=persistence-h2.properties
#   -Dorg.jvnet.hyperjaxb.persistencePropertiesMoreFile=persistence-pg.properties
#
# Choose only one of the 'PropertiesMore' configurations to specify H2 or PostgreSQL.
# The local H2 databases are auto-created during the Maven build in each target sub-
# directory; but, you must provide the PostgreSQL remote server as an external resource.
#
# The build looks for the property files, as a resource, on the classpath at
# "src/test/resources" in each sub-project.
#
# This script is not needed for the (default) H2 option. For the PostgreSQL option,
# you must run this script to initially create the database schemas where the test
# tables are generated during the Maven test phase.
#
# See also: src/test/resources/pg-create-hyperjaxb-database.sql
#
# Note: Change 'nas02' below to be your PostgreSQL host!
#
if ! command -v psql; then
	echo "Please install psql!"
	exit 1
fi
SQLCMD="psql -h nas02 -d hyperjaxb -U hyperjaxb -a -f"
SQLFILE="src/test/resources/persistence-pg-recreate-schema.sql"
#
# Comment out 'exit' to recreate all schemas!
#
exit 0
${SQLCMD} "ejb/assembly/explore/Ex001-JustProduct/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/assembly/samples/po-customized-eclipselink/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/assembly/samples/po-higherjaxb-maven-plugin/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/assembly/samples/po-initial/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/assembly/samples/root-header/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/assembly/samples/uniprot/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/assembly/templates/basic/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/roundtrip/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/addressbook/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ak/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/annox/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/any/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/any-element/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/bibtexml/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ccr/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/cda/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/choice/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/component/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/cu-one/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/custom-naming/schema/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/customType/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/customizations/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/dc/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/derby/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/device/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/dl/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/dom/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/dy/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ebxmlrr/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/edxl/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ek/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/embeddable/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/embeddable-jpa/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/embeddable-jpa-batch/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/eminq/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/enum/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/episodes/a/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/episodes/b/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/equals-builder/schema/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/floating/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/fpml-pretrade/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ids/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/idSymbolSpace/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ims-eportfolio/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ims-lip/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ioda/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/issues/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/issues-el/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/issues-jpa/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/nml/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/nokis/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/one/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/onix30/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ota/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/ows/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/pesc/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/plmxml/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/po/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/po-customized/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/po-el-customized/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/po-jaxb/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/polyform/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/po-mysql/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/publication/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/punit/core/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/punit/ext/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/px/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/regrep/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/rim/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/sbml/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/sepa/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/service/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/simple/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/sml/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/st/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/star/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/tp/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/transient-ids/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/uniprot/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/verylong/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/web/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/xacml/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tests/xmldsig/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tutorials/po/step-one/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tutorials/po/step-two/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "ejb/tutorials/po/step-three/${SQLFILE}" | grep "GRANT ALL"
${SQLCMD} "misc/dynamic/${SQLFILE}" | grep "GRANT ALL"
