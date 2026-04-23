package org.example.jpa21;

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
		return "org.example.jpa21.model";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.example.jpa21.model";
	}
}
