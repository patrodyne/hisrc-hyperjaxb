package org.jvnet.hyperjaxb.ejb.tests.customizations;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.customizations.test";
	}

	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.customizations.test";
	}
}