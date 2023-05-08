package org.jvnet.hyperjaxb.ejb.tests.simple;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "tests";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "tests";
	}
}
