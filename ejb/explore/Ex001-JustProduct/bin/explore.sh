#!/bin/sh
#
# Fix long lines in JTextArea not rendering
#   -Dsun.java2d.xrender=false \
#   -Dsun.java2d.opengl=true \
# See https://bugs.openjdk.java.net/browse/JDK-8262010
#
# Gnome scaling
# export GDK_SCALE=2
mvn test-compile exec:java \
	-Dorg.jboss.logging.provider=slf4j \
	-Dorg.jvnet.hyperjaxb3.todoLogLevel=DEBUG \
	-Dexec.classpathScope="test" \
	-Dexec.mainClass="org.patrodyne.jvnet.hyperjaxb.ex001.Explorer"
