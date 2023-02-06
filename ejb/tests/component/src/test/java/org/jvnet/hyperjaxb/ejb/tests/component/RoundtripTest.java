package org.jvnet.hyperjaxb.ejb.tests.component;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.jvnet.hyperjaxb.tests.component";
    }

    public String getPersistenceUnitName()
	{
        return "org.jvnet.hyperjaxb.tests.component";
    }
}
