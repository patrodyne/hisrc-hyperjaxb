#!/bin/bash
#
# sql-web-h2db.sh: SQL command line tool using H2 Shell.
#
#	Usage: ./sql-web-h2db.sh [h2|pg]
#	       ../sql-web-h2db.sh [h2|pg]
#	       ../../sql-web-h2db.sh [h2|pg]
#	       ../../../sql-web-h2db.sh [h2|pg]
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
H2JAR="${M2_REPO}/com/h2database/h2/2.2.220/h2-2.2.220.jar"
PGJAR="${M2_REPO}/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar"
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
		;;
	pg)
		JDBC_LIBS="${H2JAR}:${PGJAR}"
		if [ ! -r "${PGJAR}" ]; then
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
echo "Starting server and web console. Press CTRL-C to stop server."
java -cp "${JDBC_LIBS}" org.h2.tools.Console \
	-url "${jakarta_persistence_jdbc_url}" \
	-user "${jakarta_persistence_jdbc_user}" \
	-password "${jakarta_persistence_jdbc_password}"
echo "DONE"
