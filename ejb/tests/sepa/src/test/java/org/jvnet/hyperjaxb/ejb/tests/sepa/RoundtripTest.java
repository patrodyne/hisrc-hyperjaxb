package org.jvnet.hyperjaxb.ejb.tests.sepa;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "generated";
	}

	public String getPersistenceUnitName()
	{
		return "generated";
	}
}