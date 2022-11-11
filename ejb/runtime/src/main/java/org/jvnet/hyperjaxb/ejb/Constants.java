package org.jvnet.hyperjaxb.ejb;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class Constants {

	public static final String TODO_LOG_LEVEL="org.jvnet.hyperjaxb.todoLogLevel";
	
	public static final String PERSISTENCE_PROPERTIES_BASE_FILE="org.jvnet.hyperjaxb.persistencePropertiesBaseFile";
	public static final String PERSISTENCE_PROPERTIES_MORE_FILE="org.jvnet.hyperjaxb.persistencePropertiesMoreFile";
	
	public static final String JPA_EJB_VERSION = "3.0";
	public static final String ORM_EJB_VERSION = "3.0";
	public static final String JPA_JPA_VERSION = "3.0";
	public static final String ORM_JPA_VERSION = "3.0";

	public static final String NAMESPACE = "http://hyperjaxb.jvnet.org";

	public static final Locator EMPTY_LOCATOR;

	static {
		LocatorImpl l = new LocatorImpl();
		l.setColumnNumber(-1);
		l.setLineNumber(-1);
		EMPTY_LOCATOR = l;
	}

}
