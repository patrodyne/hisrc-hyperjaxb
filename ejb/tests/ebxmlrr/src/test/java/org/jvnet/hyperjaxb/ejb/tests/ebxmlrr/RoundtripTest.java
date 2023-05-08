package org.jvnet.hyperjaxb.ejb.tests.ebxmlrr;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.oasis.ebxml.registry.bindings.cms:org.oasis.ebxml.registry.bindings.lcm:org.oasis.ebxml.registry.bindings.query:org.oasis.ebxml.registry.bindings.rim:org.oasis.ebxml.registry.bindings.rs:org.w3._2000._09.xmldsig_";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.oasis.ebxml.registry.bindings.cms:org.oasis.ebxml.registry.bindings.lcm:org.oasis.ebxml.registry.bindings.query:org.oasis.ebxml.registry.bindings.rim:org.oasis.ebxml.registry.bindings.rs:org.w3._2000._09.xmldsig_";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
}
