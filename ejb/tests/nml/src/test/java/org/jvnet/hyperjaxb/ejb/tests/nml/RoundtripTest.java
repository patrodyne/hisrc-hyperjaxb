package org.jvnet.hyperjaxb.ejb.tests.nml;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "newsml.entities";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "newsml.entities";
	}
}
