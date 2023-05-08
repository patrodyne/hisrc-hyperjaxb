package org.jvnet.hyperjaxb.ejb.tests.dc;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "generated:org.purl.dc.elements._1:org.purl.dc.terms";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "generated:org.purl.dc.elements._1:org.purl.dc.terms";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
