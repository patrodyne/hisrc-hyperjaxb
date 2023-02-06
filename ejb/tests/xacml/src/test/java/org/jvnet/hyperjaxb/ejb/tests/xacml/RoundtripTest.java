package org.jvnet.hyperjaxb.ejb.tests.xacml;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "oasis.names.tc.xacml._2_0.policy.schema.os";
	}

	public String getPersistenceUnitName()
	{
		return "oasis.names.tc.xacml._2_0.policy.schema.os";
	}
}
