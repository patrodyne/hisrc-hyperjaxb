#!/bin/sh
#
# sql-cli-h2db.sh: SQL command line tool using H2 Shell.
#
#	Usage: ./sql-web-h2db.sh [h2|pg]
#	h2	H2 local database (default)
#	pg	PostgreSQL network database
#
# Starts the H2 Console (web-) server, as well as the TCP and PG server.
#
# Usage: java org.h2.tools.Console <options>
#
# Options are case sensitive. Supported options are:
#
# [-help] or [-?]	Print the list of options
# [-url]			Start a browser and connect to this URL
# [-driver]			Used together with -url: the driver
# [-user]			Used together with -url: the user name
# [-password]		Used together with -url: the password
# [-web]			Start the web server with the H2 Console
# [-tool]			Start the icon or window that allows to start a browser
# [-browser]		Start a browser connecting to the web server
# [-tcp]			Start the TCP server
# [-pg]				Start the PG server
#
# When running without options, -tcp, -web, -browser and -pg are started.
#
# For each Server, additional options are available; for details, see the Server tool.
# If a service can not be started, the program terminates with an exit code of 1.
# 
# See also https://h2database.com/javadoc/org/h2/tools/Console.html
#
H2JAR="${M2_REPO}/com/h2database/h2/2.2.224/h2-2.2.224.jar"
PGJAR="${M2_REPO}/org/postgresql/postgresql/42.7.1/postgresql-42.7.1.jar"
if [ ! -r "${H2JAR}" ]; then
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
		;;
	pg)
		JDBC_URL="jdbc:postgresql://nas02/hyperjaxb"
		JDBC_OPT=""
		JDBC_USER="hyperjaxb"
		JDBC_PASS="ChangeMe!"
		JDBC_LIBS="${H2JAR}:${PGJAR}"
		if [ ! -r "${PGJAR}" ]; then
			echo "Please configure PGJAR location in this script."
			exit 2
		fi
		;;
esac
echo "Starting server and web console. Press CTRL-C to stop server."
java -cp "${JDBC_LIBS}" org.h2.tools.Console \
	-url "${JDBC_URL}${JDBC_OPT}" \
	-user "${JDBC_USER}" \
	-password "${JDBC_PASS}"
echo "DONE"
