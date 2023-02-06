package org.jvnet.hyperjaxb.ejb.tests.polyform;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.polyform";
	}

	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.polyform";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
