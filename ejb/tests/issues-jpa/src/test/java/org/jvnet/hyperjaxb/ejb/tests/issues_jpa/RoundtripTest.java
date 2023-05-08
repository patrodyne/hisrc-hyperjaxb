package org.jvnet.hyperjaxb.ejb.tests.issues_jpa;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.issuesjpa";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.issuesjpa";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
