package org.jvnet.hyperjaxb.ejb.tests.embeddable;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.embeddable";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "foo";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
