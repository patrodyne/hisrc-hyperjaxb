package org.jvnet.hyperjaxb.ejb.tutorials.po.step_three;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tutorials.po.stepthree";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tutorials.po.stepthree";
	}
}
