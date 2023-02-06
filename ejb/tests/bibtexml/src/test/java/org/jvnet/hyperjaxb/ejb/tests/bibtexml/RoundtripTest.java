package org.jvnet.hyperjaxb.ejb.tests.bibtexml;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "net.sf.bibtexml";
	}

	public String getPersistenceUnitName()
	{
		return "net.sf.bibtexml";
	}
}
