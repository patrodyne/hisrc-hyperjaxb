package org.jvnet.hyperjaxb.ejb.tests.dl;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "uk.ac.clrc.escience.schemas.scientific";
	}

	public String getPersistenceUnitName()
	{
		return "uk.ac.clrc.escience.schemas.scientific";
	}
}