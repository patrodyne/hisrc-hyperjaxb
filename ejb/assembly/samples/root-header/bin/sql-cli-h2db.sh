#!/bin/sh
#
# sql-cli-h2db.sh: SQL command line tool using H2 Shell.
#
#	Usage: ./sql-cli-h2db.sh [h2|pg]
#	h2	H2 local database (default)
#	pg	PostgreSQL network database
#
# H2 Shell: Interactive command line tool to access a database using JDBC.
#
# Usage: java org.h2.tools.Shell <options>
# Options are case sensitive. Supported options are:
# [-help] or [-?]		 Print the list of options
# [-url "<url>"]		 The database URL (jdbc:h2:...)
# [-user <user>]		 The user name
# [-password <pwd>]		 The password
# [-driver <class>]		 The JDBC driver class to use (not required in most cases)
# [-sql "<statements>"]  Execute the SQL statements and exit
# [-properties "<dir>"]  Load the server properties from this directory
#
# If special characters don't work as expected, you may need to use
#  -Dfile.encoding=UTF-8 (Mac OS X) or CP850 (Windows).
# See also https://h2database.com/javadoc/org/h2/tools/Shell.html
#
JAVA_CMD="rlwrap java"
H2JAR="${M2_REPO}/com/h2database/h2/2.2.224/h2-2.2.224.jar"
PGJAR="${M2_REPO}/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar"
if [ -r "${H2JAR}" ]; then
	echo ""
else
	echo "Please configure H2JAR location in this script."
	exit 1
fi
JDBC_TYPE=${1:-h2}
case "${JDBC_TYPE}" in
	h2)
		JDBC_URL="jdbc:h2:file:./target/test-database/h2db"
		JDBC_OPT=";MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1"
		JDBC_USER="tester"
		JDBC_PASS="123456"
		JDBC_LIBS="${H2JAR}"
		echo "Hints: sql>"
		echo "	show schemas;"
		echo "	show tables from SCHEMANAME;"
		echo "	set schema SCHEMANAME;"
		echo "	show columns from TABLENAME;"
		echo "	select table_name, row_count_estimate from information_schema.tables where table_schema = 'SCHEMANAME' and row_count_estimate > 0 order by row_count_estimate desc;"
		;;
	pg)
		JDBC_URL="jdbc:postgresql://nas02/hyperjaxb"
		JDBC_OPT=""
		JDBC_USER="hyperjaxb"
		JDBC_PASS="ChangeMe!"
		JDBC_LIBS="${H2JAR}:${PGJAR}"
		if [ -r "${PGJAR}" ]; then
			echo "Hints: sql>"
			echo "	select schema_name from information_schema.schemata;"
			echo "	select table_schema, table_name, table_type from information_schema.tables where table_schema = 'SCHEMANAME';"
			echo "	select column_name, udt_name, character_maximum_length max, numeric_precision n_prec, numeric_scale n_scale, datetime_precision d_prec, is_nullable nullable from information_schema.columns where table_name = 'TABLENAME';"
			echo "	show all;"
		else
			echo "Please configure PGJAR location in this script."
			exit 2
		fi
		;;
esac
if ! command -v rlwrap >/dev/null; then
	JAVA_CMD="java"
	echo ""
	echo "Hints: rlwrap"
	echo "	Hint: Install rlwrap to provide readline editing, up-arrow history and completion."
	echo "	Home: https://github.com/hanslub42/rlwrap"
	echo "	Debian: sudo apt update; sudo apt install rlwrap"
fi
# Execute
${JAVA_CMD} -cp "${JDBC_LIBS}" org.h2.tools.Shell \
	-url "${JDBC_URL}${JDBC_OPT}" \
	-user "${JDBC_USER}" \
	-password "${JDBC_PASS}"
echo "DONE"
