package org.jvnet.hyperjaxb.ejb.util;

import jakarta.persistence.EntityManager;

/**
 * Utility to detect JPS providers, etc.
 */
public class ProviderDetector
{
	public static Provider getProvider(EntityManager em)
	{
		String factoryClass = em.getEntityManagerFactory().getClass().getName();
		if ( factoryClass.contains("hibernate") )
			return Provider.HIBERNATE;
		else if ( factoryClass.contains("eclipse") || factoryClass.contains("persistence") )
			return Provider.ECLIPSELINK;
		return Provider.UNKNOWN;
	}
}
