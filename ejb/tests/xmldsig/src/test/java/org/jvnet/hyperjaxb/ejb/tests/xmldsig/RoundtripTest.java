package org.jvnet.hyperjaxb.ejb.tests.xmldsig;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.w3._2000._09.xmldsig_";
	}

	public String getPersistenceUnitName()
	{
		return "org.w3._2000._09.xmldsig_";
	}
}
