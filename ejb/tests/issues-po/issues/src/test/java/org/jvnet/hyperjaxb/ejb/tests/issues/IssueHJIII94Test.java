package org.jvnet.hyperjaxb.ejb.tests.issues;

import jakarta.persistence.Id;
import jakarta.persistence.Version;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(3)
public class IssueHJIII94Test {

	@Test
	public void testEntityAnnotation() throws Exception {

		assertNotNull(IssueHJIII94Type.class.getMethod("getHjid",
				new Class[0]).getAnnotation(Id.class));

		assertNotNull(IssueHJIII94Type.class.getMethod("getHjversion",
				new Class[0]).getAnnotation(Version.class));

		try {
			IssueHJIII94SubType.class.getDeclaredMethod("getHjid", new Class[0]);
			fail("Expected exception.");
		} catch (NoSuchMethodException nsmex) {
			assertTrue(true);
		}
		try {
			IssueHJIII94SubType.class.getDeclaredMethod("getHjversion", new Class[0]);
			fail("Expected exception.");
		} catch (NoSuchMethodException nsmex) {
			assertTrue(true);
		}
	}

}
