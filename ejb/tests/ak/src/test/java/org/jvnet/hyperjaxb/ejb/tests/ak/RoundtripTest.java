package org.jvnet.hyperjaxb.ejb.tests.ak;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.tests.ak";
	}

	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.tests.ak";
	}
}