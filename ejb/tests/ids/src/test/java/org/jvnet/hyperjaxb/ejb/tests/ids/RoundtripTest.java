package org.jvnet.hyperjaxb.ejb.tests.ids;

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
		return "org.jvnet.hyperjaxb.ejb.tests.ids.tests";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "foo";
	}
}
