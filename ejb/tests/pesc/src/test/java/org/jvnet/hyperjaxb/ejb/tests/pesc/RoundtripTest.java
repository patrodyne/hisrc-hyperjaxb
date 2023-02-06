package org.jvnet.hyperjaxb.ejb.tests.pesc;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.pesc.core.coremain.v1_2";
	}

	public String getPersistenceUnitName()
	{
		return "org.pesc.core.coremain.v1_2";
	}
}
