package org.jvnet.hyperjaxb.mojo.ejb.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class HyperMojoTest extends AbstractHyperMojoTest
{
	@Test
	public void testExecute() throws Exception
	{
		assertEquals("jpa", getVariant(), "default variant is jpa");
		assertNull(getRoundtripTestClassName(), "default roundtrip class name is null");
	}
}
