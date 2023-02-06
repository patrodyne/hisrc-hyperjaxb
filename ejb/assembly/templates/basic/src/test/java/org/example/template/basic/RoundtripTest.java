package org.example.template.basic;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.example.template.basic";
    }

    public String getPersistenceUnitName()
	{
        return "org.example.template.basic";
    }
}
