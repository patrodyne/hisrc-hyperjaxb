package org.jvnet.hyperjaxb.ejb.tests.ims_lip;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.imsglobal.xsd.imslip_v1p0";
	}

	public String getPersistenceUnitName()
	{
		return "org.imsglobal.xsd.imslip_v1p0";
	}
}
