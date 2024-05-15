package org.example.root_header;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.example.root_header";
    }

    public String getPersistenceUnitName()
	{
        return "org.example.root_header";
    }
}
