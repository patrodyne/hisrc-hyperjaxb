package org.jvnet.hyperjaxb.ejb.tests.embeddable;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.embeddable";
	}

	public String getPersistenceUnitName()
	{
		return "foo";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}