#!/bin/sh
#
# Interactive command line tool to access a database using JDBC.
#
# Usage: java org.h2.tools.Shell <options>
# Options are case sensitive. Supported options are:
# [-help] or [-?]        Print the list of options
# [-url "<url>"]         The database URL (jdbc:h2:...)
# [-user <user>]         The user name
# [-password <pwd>]      The password
# [-driver <class>]      The JDBC driver class to use (not required in most cases)
# [-sql "<statements>"]  Execute the SQL statements and exit
# [-properties "<dir>"]  Load the server properties from this directory
#
# If special characters don't work as expected, you may need to use
#  -Dfile.encoding=UTF-8 (Mac OS X) or CP850 (Windows).
# See also https://h2database.com/javadoc/org/h2/tools/Shell.html
#
H2JAR="${M2_REPO}/com/h2database/h2/2.1.210/h2-2.1.210.jar"
JDBC_URL="jdbc:h2:file:./target/test-database/podb"
JDBC_OPT="MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1"
JAVA_CMD="rlwrap java"
if ! command -v rlwrap >/dev/null; then
	JAVA_CMD="java"
    echo ""
    echo "Hints: rlwrap"
	echo "  Hint: Install rlwrap to provide readline editing, up-arrow history and completion."
	echo "  Home: https://github.com/hanslub42/rlwrap"
	echo "  Debian: sudo apt update; sudo apt install rlwrap"
fi
echo ""
echo "Hints: sql"
echo "  show schemas;"
echo "  show tables;"
echo "  show columns from TABLENAME;"
if [ -r "${H2JAR}" ]; then
	${JAVA_CMD} -cp "${H2JAR}" org.h2.tools.Shell \
		-url "${JDBC_URL};${JDBC_OPT}" \
		-user "tester" \
		-password "123456"
else
	echo "Please configure H2 location in this script."
fi
