#!/bin/bash
#
# Run a Maven goal to execute the Explorer class.
#
# Notes:
# Fix long lines in JTextArea not rendering
#	-Dsun.java2d.xrender=false \
#	-Dsun.java2d.opengl=true \
# See https://bugs.openjdk.java.net/browse/JDK-8262010
#
# Gnome scaling
# export GDK_SCALE=2

BASEDIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
source ${BASEDIR}/build-inc.sh

# Execute Explorer using 'org.codehaus.mojo:exec-maven-plugin'
mvn test-compile exec:java ${JVM_SYS_PROPS} \
	-Dexec.classpathScope="test" \
	-Dexec.mainClass="org.patrodyne.jvnet.hyperjaxb.ex001.Explorer"
