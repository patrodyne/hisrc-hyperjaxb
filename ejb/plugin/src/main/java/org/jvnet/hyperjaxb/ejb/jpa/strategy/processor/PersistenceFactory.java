package org.jvnet.hyperjaxb.ejb.jpa.strategy.processor;

import static org.jvnet.hyperjaxb.ejb.Constants.JPA_JPA_VERSION;

import jakarta.enterprise.inject.Vetoed;

@Vetoed
public class PersistenceFactory extends org.jvnet.hyperjaxb.ejb.strategy.processor.PersistenceFactory
{
	@Override
	protected String getVersion()
	{
		return JPA_JPA_VERSION;
	}
}
