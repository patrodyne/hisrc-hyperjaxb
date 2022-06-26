#!/bin/sh
#
# Starts the H2 Console (web-) server, as well as the TCP and PG server.
#
# Usage: java org.h2.tools.Console <options>
#
# Options are case sensitive. Supported options are:
#
# [-help] or [-?]   Print the list of options
# [-url]            Start a browser and connect to this URL
# [-driver]         Used together with -url: the driver
# [-user]           Used together with -url: the user name
# [-password]       Used together with -url: the password
# [-web]            Start the web server with the H2 Console
# [-tool]           Start the icon or window that allows to start a browser
# [-browser]        Start a browser connecting to the web server
# [-tcp]            Start the TCP server
# [-pg]             Start the PG server
#
# When running without options, -tcp, -web, -browser and -pg are started.
#
# For each Server, additional options are available; for details, see the Server tool.
# If a service can not be started, the program terminates with an exit code of 1.
# 
# See also https://h2database.com/javadoc/org/h2/tools/Console.html
#
H2JAR="${M2_REPO}/com/h2database/h2/2.1.210/h2-2.1.210.jar"
JDBC_URL="jdbc:h2:file:./target/test-database/podb"
JDBC_OPT="MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1"
if [ -r "${H2JAR}" ]; then
	echo "Starting server and web console. Press CTRL-C to stop server."
	java -cp "${H2JAR}" org.h2.tools.Console \
		-url "${JDBC_URL};${JDBC_OPT}" \
		-user "tester" \
		-password "123456"
else
	echo "Please configure H2 location in this script."
fi
