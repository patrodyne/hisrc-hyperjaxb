package org.jvnet.hyperjaxb.ejb.tests.web;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "com.sun.java.xml.ns.j2ee";
	}

	public String getPersistenceUnitName()
	{
		return "com.sun.java.xml.ns.j2ee";
	}
}
