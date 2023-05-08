package org.jvnet.hyperjaxb.ejb.tests.equalsbuilder;

import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldEqualsStrategy extends JAXBEqualsStrategy
{
	private Logger logger = LoggerFactory.getLogger(HelloWorldEqualsStrategy.class);
	@Override
	public Logger getLogger() { return logger; }
	
	@Override
	public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, Object lhs, Object rhs)
	{
		getLogger().info("Hello world!");
		return super.equals(leftLocator, rightLocator, lhs, rhs);
	}
}
