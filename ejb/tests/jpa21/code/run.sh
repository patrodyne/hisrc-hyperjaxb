#!/bin/sh
mvn clean compile exec:java -Dexec.args="insert1 summaryH2" -Dexec.classpathScope=test
