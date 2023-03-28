#!/bin/sh
#
# pg-create-database.sh - Create PostgreSQL HyperJAXB Database
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
# Locally, you must install the 'psql' client with username/password access to the
# server.
#
# For the PostgreSQL option, you must have a PostgreSQL server available and
# you must have privilege(s) to create a database on the server and to create
# schema(s) in the database. For example, 'postgres' is the default super-user in
# a typical PostgreSQL installation. As that user, you can create a 'hyperjaxb'
# database.
#
# Then you must run this script to initially create the database where the test
# schema(s) are created.
#
# Hint(s):
#
#   1) https://www.postgresql.org/
#   2) Accessing a remote PG server:
#   2.1) On the server, locate or find the 'hba.conf' file
#   2.1.1) locate 'pg_hba.conf'
#   2.1.2) find / -name 'pg_hba.conf'
#   2.2) Edit the 'pg_hba.conf'
#   2.2.1) Add configuration to allow access by username/password on your LAN:
#
# TYPE  DATABASE        USER            ADDRESS                 METHOD
# host  all             all             192.168.1.0/24          md5
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
SQLCMD="psql -h nas02 -d postgres -U postgres -a -f"
SQLFILE="src/test/resources/persistence-pg-create-database.sql"
#
# Comment out 'exit' to create a database!
#
exit 0
${SQLCMD} "${SQLFILE}"
