package org.jvnet.hyperjaxb3.ejb.strategy.naming.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.jvnet.jaxb2_commons.config.LocatorInputFactory.PROTOCOL_CLASSPATH;
import static org.jvnet.jaxb2_commons.config.LocatorInputFactory.PROTOCOL_FILE;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.ReservedNames;

public class DefaultReservedNamesTest
{
	private final String RESOURCE_ROOT = System.getProperty("user.dir");
	private final String RESOURCE_BASE = "src/main/resources/";
	private final String RESOURCE_PATH = "org/jvnet/hyperjaxb3/ejb/strategy/naming/impl/";
	private final String RESOURCE_NAME = "ReservedNames.properties";

	@Test
	void testClasspathAbsoluteLoad() throws IOException
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = PROTOCOL_CLASSPATH + "/" + RESOURCE_PATH + RESOURCE_NAME;
		reservedNames.load(locator);
		assertFalse(reservedNames.isEmpty(), "Reserved names are expected.");
	}
	
	@Test
	void testClasspathRelativeLoad() throws IOException
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = PROTOCOL_CLASSPATH + RESOURCE_NAME;
		reservedNames.load(locator);
		assertFalse(reservedNames.isEmpty(), "Reserved names are expected.");
	}

	@Test
	void testRelativeFileProtocolLoad() throws IOException
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = PROTOCOL_FILE + RESOURCE_BASE + RESOURCE_PATH + RESOURCE_NAME;
		reservedNames.load(locator);
		assertFalse(reservedNames.isEmpty(), "Reserved names are expected.");
	}


	@Test
	void testAbsoluteFileProtocolLoad() throws IOException
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = PROTOCOL_FILE + "//" + RESOURCE_ROOT + "/" +RESOURCE_BASE + RESOURCE_PATH + RESOURCE_NAME;
		reservedNames.load(locator);
		assertFalse(reservedNames.isEmpty(), "Reserved names are expected.");
	}

	@Test
	void testRelativeFileLoad() throws IOException
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = RESOURCE_BASE + RESOURCE_PATH + RESOURCE_NAME;
		reservedNames.load(locator);
		assertFalse(reservedNames.isEmpty(), "Reserved names are expected.");
	}


	@Test
	void testAbsoluteFileLoad() throws IOException
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = RESOURCE_ROOT + "/" +RESOURCE_BASE + RESOURCE_PATH + RESOURCE_NAME;
		reservedNames.load(locator);
		assertFalse(reservedNames.isEmpty(), "Reserved names are expected.");
	}
}
