package org.jvnet.hyperjaxb.ejb.tests.choice;

import org.junit.jupiter.api.Order;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

@Order(2)
public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.choice";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.choice";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
