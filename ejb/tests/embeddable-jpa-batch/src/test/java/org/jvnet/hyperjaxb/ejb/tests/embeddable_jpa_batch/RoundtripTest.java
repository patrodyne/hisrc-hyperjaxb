package org.jvnet.hyperjaxb.ejb.tests.embeddable_jpa_batch;

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
		return "com.stackoverflow.embeddable_jpa_batch";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "com.stackoverflow.embeddable_jpa_batch";
	}
}
