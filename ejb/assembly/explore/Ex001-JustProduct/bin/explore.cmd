@echo off
@rem Run a Maven goal to execute the Explorer class.
@mvn test-compile exec:java ^
	-Dorg.jboss.logging.provider=slf4j ^
	-Dorg.jvnet.hyperjaxb.todoLogLevel=DEBUG ^
	-Dorg.jvnet.hyperjaxb.persistencePropertiesBaseFile=persistence.properties ^
	-Dorg.jvnet.hyperjaxb.persistencePropertiesMoreFile=persistence-h2.properties ^
	-Dcom.zaxxer.hikari.housekeeping.periodMs=30000 ^
	-Dexec.classpathScope=test ^
	-Dexec.mainClass=org.patrodyne.jvnet.hyperjaxb.ex001.Explorer


