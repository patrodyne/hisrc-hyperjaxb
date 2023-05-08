package org.jvnet.hyperjaxb.ejb.tests.ows;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "net.opengis.ows.v_1_1_0";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "net.opengis.ows.v_1_1_0";
	}
}
