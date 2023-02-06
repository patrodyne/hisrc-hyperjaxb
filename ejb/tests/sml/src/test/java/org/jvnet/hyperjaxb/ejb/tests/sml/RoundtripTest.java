package org.jvnet.hyperjaxb.ejb.tests.sml;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.iptc.std.nitf._2006_10_18:org.iptc.std.sportsml._2006_10_18";
    }

    public String getPersistenceUnitName()
	{
        return "org.iptc.std.nitf._2006_10_18:org.iptc.std.sportsml._2006_10_18";
    }
}
