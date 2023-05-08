package org.jvnet.hyperjaxb.ejb.tests.regrep;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.freebxml.omar.jaxb.bindings.cms:org.freebxml.omar.jaxb.bindings.lcm:org.freebxml.omar.jaxb.bindings.mtomtransport:org.freebxml.omar.jaxb.bindings.query:org.freebxml.omar.jaxb.bindings.rim:org.freebxml.omar.jaxb.bindings.rs";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.freebxml.omar.jaxb.bindings.cms:org.freebxml.omar.jaxb.bindings.lcm:org.freebxml.omar.jaxb.bindings.mtomtransport:org.freebxml.omar.jaxb.bindings.query:org.freebxml.omar.jaxb.bindings.rim:org.freebxml.omar.jaxb.bindings.rs";
	}
}
