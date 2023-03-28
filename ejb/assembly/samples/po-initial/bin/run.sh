#!/bin/bash

BASEDIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
source ${BASEDIR}/build-cfg.sh
source ${BASEDIR}/build-inc.sh
export MAVEN_OPTS="${MAVEN_OPTS} ${JVM_SYS_PROPS}"

mvn "${MAVEN_ARGS}" clean compile exec:java
