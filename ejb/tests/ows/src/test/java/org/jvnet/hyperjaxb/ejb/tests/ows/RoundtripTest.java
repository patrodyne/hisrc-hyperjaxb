package org.jvnet.hyperjaxb.ejb.tests.ows;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "net.opengis.ows.v_1_1_0";
	}

	public String getPersistenceUnitName()
	{
		return "net.opengis.ows.v_1_1_0";
	}
}
