package org.jvnet.hyperjaxb.ejb.strategy.processor;

import static org.jvnet.hyperjaxb.ejb.Constants.JPA_EJB_VERSION;

import ee.jakarta.xml.ns.persistence.Persistence;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersistenceFactory
{
	public Persistence createPersistence()
	{
		final Persistence persistence = new Persistence();
		persistence.setVersion(getVersion());
		return persistence;
	}

	protected String getVersion()
	{
		return JPA_EJB_VERSION;
	}
}
