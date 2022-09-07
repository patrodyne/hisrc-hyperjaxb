package org.jvnet.hyperjaxb3.ejb.tests.issues;

import jakarta.persistence.Id;
import jakarta.persistence.Version;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

public class IssueHJIII94Test {

	@Test
	public void testEntityAnnotation() throws Exception {

		assertNotNull(IssueHJIII94Type.class.getMethod("getHjid",
				new Class[0]).getAnnotation(Id.class));

		assertNotNull(IssueHJIII94Type.class.getMethod("getHjversion",
				new Class[0]).getAnnotation(Version.class));

		try {
			IssueHJIII94SubType.class.getDeclaredMethod("getHjid", new Class[0]);
			Assert.fail("Expected exception.");
		} catch (NoSuchMethodException nsmex) {
			assertTrue(true);
		}
		try {
			IssueHJIII94SubType.class.getDeclaredMethod("getHjversion", new Class[0]);
			Assert.fail("Expected exception.");
		} catch (NoSuchMethodException nsmex) {
			assertTrue(true);
		}
	}

}
