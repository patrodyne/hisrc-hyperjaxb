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

# Release to Maven Central via Sonatype Nexus Repository Manager
# 0) Pre-requisite: hisrc-hyperjaxb-annox
# 1) Set MVN_ARGS to "-T 1" and commit/push
# 2) Exit mc, use the same TTY to reuse gpg signing daemon.
# 3) To "prime" the GnuPG agent...............: echo "test" | gpg --clearsign
# 4) To delete a TAG..........................: git tag -d N.N.N; git push origin --delete N.N.N
# 5) To throw away the last N local commits...: git reset --hard HEAD~N 
# 6) To ruthlessly force local repo to remote.: git push --force origin master
# 7) git status; git remote show origin
# Note: Child modules are SKIPPED (normally) for some goals.
# ./build-JXX.sh -DskipTests=true clean install
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -Pnexus-deploy clean deploy
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -DdryRun=false release:clean
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -DdryRun=false release:prepare
# ./build-JXX.sh -DskipTests=true -Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG -DdryRun=false release:perform
#
# NEW: https://central.sonatype.org/publish/publish-portal-guide/
