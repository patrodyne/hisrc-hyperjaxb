package org.jvnet.hyperjaxb.ejb.tests.web;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "com.sun.java.xml.ns.j2ee";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "com.sun.java.xml.ns.j2ee";
	}
}
