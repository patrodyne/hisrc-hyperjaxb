#!/bin/sh

M2_REPO="${HOME}/.m2/repository"
JBT_VERSION="0.12.1-SNAPSHOT"
CP="${M2_REPO}/org/patrodyne/jvnet/hisrc-basicjaxb-tools/${JBT_VERSION}/hisrc-basicjaxb-tools-${JBT_VERSION}.jar"
EXEC="java -cp ${CP} org.jvnet.jaxb2_commons.util.CreateToplevelXJBindings --nested"

cd "src/main/resources"

for XSD in *.xsd
	do
		${EXEC} "${XSD}"
	done

exit 0
