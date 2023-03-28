package org.example.po;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.example.po";
    }

    public String getPersistenceUnitName()
	{
        return "org.example.po";
    }
}
