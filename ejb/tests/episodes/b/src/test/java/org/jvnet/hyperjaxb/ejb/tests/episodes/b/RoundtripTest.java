package org.jvnet.hyperjaxb.ejb.tests.episodes.b;

import org.junit.jupiter.api.Order;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

@Order(2)
public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.jvnet.hyperjaxb.ejb.tests.episodes.b";
    }

    public String getPersistenceUnitName()
	{
        return "org.jvnet.hyperjaxb.ejb.tests.episodes.b";
    }
}
