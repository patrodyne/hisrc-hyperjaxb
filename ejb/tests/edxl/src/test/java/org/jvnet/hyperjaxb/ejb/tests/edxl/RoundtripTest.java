package org.jvnet.hyperjaxb.ejb.tests.edxl;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "net.opengis.gml:oasis.names.tc.ciq.xal._3:oasis.names.tc.ciq.xnl._3:oasis.names.tc.ciq.xpil._3:oasis.names.tc.emergency.edxl.have._1:oasis.names.tc.emergency.edxl.have._1_0.geo_oasis:oasis.names.tc.ciq.ct._3";
	}

	public String getPersistenceUnitName()
	{
		return "oasis.names.tc.ciq.xal._3:oasis.names.tc.ciq.xnl._3:oasis.names.tc.ciq.xpil._3:oasis.names.tc.emergency.edxl.have._1:oasis.names.tc.emergency.edxl.have._1_0.geo_oasis:oasis.names.tc.ciq.ct._3";
	}
}
