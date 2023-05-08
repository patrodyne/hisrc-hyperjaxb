package org.jvnet.hyperjaxb.ejb.tests.device;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "com.sun.crae.generatedcomponents";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "com.sun.crae.generatedcomponents";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
