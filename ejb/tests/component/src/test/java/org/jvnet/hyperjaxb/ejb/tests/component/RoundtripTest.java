package org.jvnet.hyperjaxb.ejb.tests.component;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    @Override
	public String getContextPath()
	{
        return "org.jvnet.hyperjaxb.tests.component";
    }

    @Override
	public String getPersistenceUnitName()
	{
        return "org.jvnet.hyperjaxb.tests.component";
    }
}
