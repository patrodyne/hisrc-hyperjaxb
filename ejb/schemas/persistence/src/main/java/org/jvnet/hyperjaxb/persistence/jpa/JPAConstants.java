package org.jvnet.hyperjaxb.persistence.jpa;

import org.jvnet.hyperjaxb.persistence.util.PersistenceConstants;

public class JPAConstants
{
	private JPAConstants() { }

	public static final String SCHEMA_LOCATION = 
		PersistenceConstants.JPA_NAMESPACE_URI + " https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd " +
		PersistenceConstants.ORM_NAMESPACE_URI + " https://jakarta.ee/xml/ns/persistence/orm/orm_3_0.xsd";
}
