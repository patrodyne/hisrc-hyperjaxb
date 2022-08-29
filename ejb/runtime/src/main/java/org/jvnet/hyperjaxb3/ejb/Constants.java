package org.jvnet.hyperjaxb3.ejb;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class Constants {

	public static final String TODO_LOG_LEVEL="org.jvnet.hyperjaxb3.todoLogLevel";
	
	public static final String PERSISTENCE_PROPERTIES_BASE_FILE="org.jvnet.hyperjaxb3.persistencePropertiesBaseFile";
	public static final String PERSISTENCE_PROPERTIES_MORE_FILE="org.jvnet.hyperjaxb3.persistencePropertiesMoreFile";
	
	public static final String JPA_EJB_VERSION = "3.0";
	public static final String ORM_EJB_VERSION = "3.0";
	public static final String JPA_JPA3_VERSION = "3.0";
	public static final String ORM_JPA3_VERSION = "3.0";

	public static final String NAMESPACE = "http://hyperjaxb3.jvnet.org";

	public static final Locator EMPTY_LOCATOR;

	static {
		LocatorImpl l = new LocatorImpl();
		l.setColumnNumber(-1);
		l.setLineNumber(-1);
		EMPTY_LOCATOR = l;
	}

}
