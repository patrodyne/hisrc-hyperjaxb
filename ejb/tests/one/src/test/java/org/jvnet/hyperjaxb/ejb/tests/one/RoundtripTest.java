
package org.jvnet.hyperjaxb.ejb.tests.one;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.one";
	}

	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.one";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
