package org.jvnet.hyperjaxb.ejb.tests.service;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "my";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "my";
	}
}
