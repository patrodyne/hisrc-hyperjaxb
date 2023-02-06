package org.jvnet.hyperjaxb.ejb.tests.ota;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.opentravel.ota._2003._05";
    }

    public String getPersistenceUnitName()
	{
        return "org.opentravel.ota._2003._05";
    }
}
