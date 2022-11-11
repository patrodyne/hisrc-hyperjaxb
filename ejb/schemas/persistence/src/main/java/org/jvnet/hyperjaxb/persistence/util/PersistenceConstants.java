package org.jvnet.hyperjaxb.persistence.util;

import ee.jakarta.xml.ns.persistence.Persistence;
import ee.jakarta.xml.ns.persistence.orm.Entity;

public class PersistenceConstants
{
	private PersistenceConstants() { }

	public static final String CONTEXT_PATH =
		Persistence.class.getPackage().getName() + ":" + Entity.class.getPackage().getName();

	public static final String JPA_NAMESPACE_URI = "https://jakarta.ee/xml/ns/persistence";
	public static final String ORM_NAMESPACE_URI = "https://jakarta.ee/xml/ns/persistence/orm";

}
