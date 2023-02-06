package org.jvnet.hyperjaxb.ejb.tests.issues;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
	extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
	implements ContextPathAware
{
//	protected ClassLoader getContextClassLoader()
//	{
//		Class<ObjectFactory> poObjectFactory = org.jvnet.hyperjaxb.ejb.tests.po.ObjectFactory.class;
//		return poObjectFactory.getClassLoader();
//	}
	
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.issuesignored:org.jvnet.hyperjaxb.ejb.tests.issues:org.jvnet.hyperjaxb.ejb.tests.po";
	}

	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.issues";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
