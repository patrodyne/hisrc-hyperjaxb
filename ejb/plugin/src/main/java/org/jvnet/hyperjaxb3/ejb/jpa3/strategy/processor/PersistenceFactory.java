package org.jvnet.hyperjaxb3.ejb.jpa3.strategy.processor;

import static org.jvnet.hyperjaxb3.ejb.Constants.JPA_JPA3_VERSION;

import jakarta.enterprise.inject.Vetoed;

@Vetoed
public class PersistenceFactory extends org.jvnet.hyperjaxb3.ejb.strategy.processor.PersistenceFactory
{
	@Override
	protected String getVersion()
	{
		return JPA_JPA3_VERSION;
	}
}
