#!/bin/sh
#
# pg-recreate-schema.sh - Drop / Create PostgreSQL HyperJAXB Table Schemas
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
# "src/main/resources" or "src/test/resources" in each sub-project.
#
# This script is ONLY needed for the PG option.
#
# For the PostgreSQL option, you must run this script to initially create the
# database schemas where the test tables are generated during the Maven test
# phase.
#
# See also: src/test/resources/persistence-pg-create-database.sql
# See also: src/test/resources/persistence-pg-recreate-schema.sql
# See also: pg-recreate-schema.sh
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
# Comment out 'exit' to recreate schema!
#
exit 0
${SQLCMD} "${SQLFILE}" | grep "GRANT ALL"
