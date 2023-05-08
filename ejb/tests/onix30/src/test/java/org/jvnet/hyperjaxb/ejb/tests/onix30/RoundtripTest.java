package org.jvnet.hyperjaxb.ejb.tests.onix30;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.editeur.onix._3_0.reference";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.editeur.onix._3_0.reference";
	}
}
