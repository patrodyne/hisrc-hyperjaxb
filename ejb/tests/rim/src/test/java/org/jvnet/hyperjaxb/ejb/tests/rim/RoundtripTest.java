package org.jvnet.hyperjaxb.ejb.tests.rim;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.freebxml.omar.jaxb.bindings.lcm._4_0:org.freebxml.omar.jaxb.bindings.query._4_0:org.freebxml.omar.jaxb.bindings.rim._4_0:org.freebxml.omar.jaxb.bindings.rs._4_0:org.freebxml.omar.jaxb.bindings.spi._4_0:org.w3._2005._08.addressing";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.freebxml.omar.jaxb.bindings.lcm._4_0:org.freebxml.omar.jaxb.bindings.query._4_0:org.freebxml.omar.jaxb.bindings.rim._4_0:org.freebxml.omar.jaxb.bindings.rs._4_0:org.freebxml.omar.jaxb.bindings.spi._4_0:org.w3._2005._08.addressing";
	}
}
