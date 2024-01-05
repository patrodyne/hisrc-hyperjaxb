package org.jvnet.hyperjaxb.ejb.tests.issues;

import jakarta.persistence.Id;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(3)
public class IssueHJIII41Test {

	@Test
	public void testEntityAnnotation() throws Exception {

		assertNotNull(IssueHJIII41ParentType.class.getMethod("getId",
				new Class[0]).getAnnotation(Id.class));

		try {
			IssueHJIII41ChildType.class.getMethod("getHjid", new Class[0]);
			fail("Expected exception.");
		} catch (NoSuchMethodException nsmex) {
			assertTrue(true);
		}
	}

}
