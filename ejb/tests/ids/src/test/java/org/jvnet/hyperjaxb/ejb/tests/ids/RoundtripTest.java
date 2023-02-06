package org.jvnet.hyperjaxb.ejb.tests.ids;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.ids.tests";
	}

	public String getPersistenceUnitName()
	{
		return "foo";
	}
}
