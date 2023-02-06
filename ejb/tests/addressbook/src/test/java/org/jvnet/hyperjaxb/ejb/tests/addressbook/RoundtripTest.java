package org.jvnet.hyperjaxb.ejb.tests.addressbook;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "com.pps.schema";
	}

	public String getPersistenceUnitName()
	{
		return "com.pps.schema";
	}
}
