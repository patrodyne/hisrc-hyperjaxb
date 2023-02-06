package org.jvnet.hyperjaxb.ejb.tests.nokis;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "de.baw.nokis";
	}

	public String getPersistenceUnitName()
	{
		return "de.baw.nokis";
	}
}
