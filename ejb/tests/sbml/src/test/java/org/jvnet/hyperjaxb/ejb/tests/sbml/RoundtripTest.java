package org.jvnet.hyperjaxb.ejb.tests.sbml;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.sbml.sbml.level2:org.w3._1998.math.mathml";
	}

	public String getPersistenceUnitName()
	{
		return "org.sbml.sbml.level2:org.w3._1998.math.mathml";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
