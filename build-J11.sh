#!/bin/sh
#
# Usage: build-J11.sh [option(s)] [goal(s)]
# Example: build-J11.sh clean install
#
# Profile Id: none - default, install common jars to local repository.
# Profile Id: samples - package sample plus default projects.
# Profile Id: tests - package test plus default projects.
# Profile Id: tests,tests-0 - package test set #0 plus default projects.
# Profile Id: tests,tests-1 - package test set #1 plus default projects.
# Profile Id: tests,tests-2 - package test set #2 plus default projects.
# Profile Id: tests,tests-all - package all test sets plus default projects.
# Profile Id: all - package the above plus templates and tutorials.
# Profile Id: sonatype-oss-release - upload default artifacts to central repository.
#
# How to build and test:
#
#   1) build-J11.sh -DskipTests=true clean install
#   2) build-J11.sh -DskipTests=true -Pall clean package
#   3) build-J11.sh -DskipTests=false -Pall test
#   Notes:
#     Step #1 installs the shared libraries to your local maven repository.
#     Step #2 packages the shared, test and sample projects.
#     Step #3 unit test the shared, test and sample projects.
#
JAVA11_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
  if [ ! -d "${JAVA11_HOME}" ]; then
	echo "Please configure JDK 11 home path."
	exit 1
  fi
  export JAVA_HOME="${JAVA11_HOME}"
# DEBUG_OPTS="-X -Dorg.slf4j.simpleLogger.showLogName=true"
# BUILD_OPTS="--fail-at-end -Dorg.jvnet.hyperjaxb3.todoLogLevel=DEBUG -DskipTests=true $@"
  BUILD_OPTS="--fail-at-end -Dorg.jvnet.hyperjaxb3.todoLogLevel=DEBUG -Dorg.jboss.logging.provider=slf4j $@"
  mvn ${BUILD_OPTS}
# mvn ${BUILD_OPTS} install
# mvn ${BUILD_OPTS} -Psamples package
# mvn ${BUILD_OPTS} -Ptests package
# mvn ${BUILD_OPTS} -Ptests,tests-all package
# mvn ${BUILD_OPTS} -Psamples,tests package
# mvn ${BUILD_OPTS} -Pall package
