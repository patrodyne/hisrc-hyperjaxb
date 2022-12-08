#!/bin/bash
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
if ! command -v rlwrap >/dev/null; then
	JAVA_CMD="java"
	echo ""
	echo "Hints: rlwrap"
	echo "	Hint: Install rlwrap to provide readline editing, up-arrow history and completion."
	echo "	Home: https://github.com/hanslub42/rlwrap"
	echo "	Debian: sudo apt update; sudo apt install rlwrap"
fi
H2JAR="${M2_REPO}/com/h2database/h2/2.1.210/h2-2.1.210.jar"
PGJAR="${M2_REPO}/org/postgresql/postgresql/42.4.0/postgresql-42.4.0.jar"
if [ ! -r "${H2JAR}" ]; then
	echo "ERROR: Please configure H2JAR location in this script."
	exit 1
fi
JDBC_TYPE=${1:-h2}
JDBC_CONF="src/test/resources/persistence-${JDBC_TYPE}.properties"
if [ ! -r "${JDBC_CONF}" ]; then
	echo "ERROR: Configureation file '${JDBC_CONF}' not found!"
	exit 2
fi
case "${JDBC_TYPE}" in
	h2)
		JDBC_LIBS="${H2JAR}"
		echo "Hints: sql>"
		echo "	show schemas;"
		echo "	show tables;"
		echo "	show columns from TABLENAME;"
		echo "	select table_name, row_count_estimate from information_schema.tables where lower(table_schema) = 'public' and row_count_estimate > 0 order by row_count_estimate desc;"
		;;
	pg)
		JDBC_LIBS="${H2JAR}:${PGJAR}"
		if [ -r "${PGJAR}" ]; then
			echo "Hints: sql>"
			echo "	select schema_name from information_schema.schemata;"
			echo "	select table_schema, table_name, table_type from information_schema.tables where table_schema = 'SCHEMANAME';"
			echo "	select column_name, udt_name, character_maximum_length max, numeric_precision n_prec, numeric_scale n_scale, datetime_precision d_prec, is_nullable nullable from information_schema.columns where table_name = 'TABLENAME';"
			echo "	show all;"
		else
			echo "ERROR: Please configure PGJAR location in this script."
			exit 3
		fi
		;;
esac
while IFS='=' read -r key value
do
	if [[ ! "$key" =~ ^[[:space:]]*# ]]; then
		key=$(echo $key | tr '.' '_')
		eval ${key}=\${value}
	fi
done < "$JDBC_CONF"
# Execute
${JAVA_CMD} -cp "${JDBC_LIBS}" org.h2.tools.Shell \
	-url "${jakarta_persistence_jdbc_url}" \
	-user "${jakarta_persistence_jdbc_user}" \
	-password "${jakarta_persistence_jdbc_password}"
echo "DONE"
