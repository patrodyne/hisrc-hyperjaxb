package org.jvnet.hyperjaxb3.ejb.strategy.processor;

import static org.jvnet.hyperjaxb3.ejb.Constants.JPA_EJB_VERSION;

import ee.jakarta.xml.ns.persistence.Persistence;

public class PersistenceFactory {

	public Persistence createPersistence() {
		final Persistence persistence = new Persistence();
		persistence.setVersion(getVersion());
		return persistence;
	}

	protected String getVersion() {
		return JPA_EJB_VERSION;
	}

}
