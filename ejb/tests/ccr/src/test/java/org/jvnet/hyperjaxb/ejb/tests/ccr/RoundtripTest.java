package org.jvnet.hyperjaxb.ejb.tests.ccr;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.astm.ccr";
	}

	public String getPersistenceUnitName()
	{
		return "org.astm.ccr";
	}
}
