package org.jvnet.hyperjaxb.ejb.tests.episodes.a;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    @Override
    public String getContextPath()
	{
        return "org.jvnet.hyperjaxb.ejb.tests.episodes.a";
    }

    @Override
    public String getPersistenceUnitName()
	{
        return "org.jvnet.hyperjaxb.ejb.tests.episodes.a";
    }
    
    @Override
    public Boolean isValidateXml()
    {
        return false;
    }
}
