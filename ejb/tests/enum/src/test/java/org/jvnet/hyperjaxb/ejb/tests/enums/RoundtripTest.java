package org.jvnet.hyperjaxb.ejb.tests.enums;

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
		return "test";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "test";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
