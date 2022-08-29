package org.jvnet.hyperjaxb3.persistence.jpa3;

import org.jvnet.hyperjaxb3.persistence.util.PersistenceConstants;

public class JPA3Constants
{
	private JPA3Constants() { }

	public static final String SCHEMA_LOCATION = 
		PersistenceConstants.JPA_NAMESPACE_URI + " https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd " +
		PersistenceConstants.ORM_NAMESPACE_URI + " https://jakarta.ee/xml/ns/persistence/orm/orm_3_0.xsd";
}
