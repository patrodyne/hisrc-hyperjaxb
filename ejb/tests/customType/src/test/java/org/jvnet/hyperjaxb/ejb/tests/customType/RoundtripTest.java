package org.jvnet.hyperjaxb.ejb.tests.customType;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "com.typetest";
	}

	public String getPersistenceUnitName()
	{
		return "com.typetest";
	}
}
