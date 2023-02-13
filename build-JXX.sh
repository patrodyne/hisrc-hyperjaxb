#!/bin/bash
#
# Usage: build-JXX.sh [option(s)] [goal(s)]
# Example: build-JXX.sh clean install
#
# Profile Id: none - default, install common jars to local repository.
# Profile Id: tests - package test plus default projects.
# Profile Id: tests,tests-0 - successful tests plus packagable, failing tests.
# Profile Id: tests,tests-1 - successful tests plus longer packagable, failing tests.
# Profile Id: tests,tests-2 - successful tests plus unpackagable, failing tests.
# Profile Id: assembly - integration-test assemblies plus default projects.
# Profile Id: all - package the above plus templates and tutorials.
# Profile Id: sonatype-oss-release - upload default artifacts to central repository.
#
# How to build and test:
#
#   1) build-JXX.sh -DskipTests=true clean install
#   2) build-JXX.sh -DskipTests=true -Pall clean package
#   3) build-JXX.sh -DskipTests=false -Pall test
#   Notes:
#     Step #1 installs the shared libraries to your local maven repository.
#     Step #2 packages the shared, test and assembly projects.
#     Step #3 unit test the shared, test and assembly projects.
#

if [ ! -d "${JAVA_HOME}" ]; then
	echo "Please configure Java home path."
	exit 1
fi

BASEDIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
source ${BASEDIR}/build-INC.sh

if [ $# -eq 0 ]; then
  ${BASEDIR}/build.sh
else
  source ${BASEDIR}/build-CFG.sh
  mvn ${JVM_SYS_PROPS} "$@"
fi

# ./build-JXX.sh -DskipTests=true clean install
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -Pnexus-deploy clean deploy
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -DdryRun=false release:clean
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -DdryRun=false release:prepare
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -DdryRun=false release:perform
