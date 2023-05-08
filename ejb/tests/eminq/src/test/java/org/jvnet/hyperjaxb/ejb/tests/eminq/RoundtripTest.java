package org.jvnet.hyperjaxb.ejb.tests.eminq;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "com.innoq.model.impl";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "com.innoq.model.impl";
	}
}
