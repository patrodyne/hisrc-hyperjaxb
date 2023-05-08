package org.jvnet.hyperjaxb.ejb.tests.ccr;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.astm.ccr";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.astm.ccr";
	}
}
