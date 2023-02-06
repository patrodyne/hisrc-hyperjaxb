package org.jvnet.hyperjaxb.ejb.tests.device;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "com.sun.crae.generatedcomponents";
	}

	public String getPersistenceUnitName()
	{
		return "com.sun.crae.generatedcomponents";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
