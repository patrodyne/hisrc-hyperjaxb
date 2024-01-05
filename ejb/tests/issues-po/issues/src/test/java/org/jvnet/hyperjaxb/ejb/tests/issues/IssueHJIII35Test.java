package org.jvnet.hyperjaxb.ejb.tests.issues;

import jakarta.persistence.Version;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(3)
public class IssueHJIII35Test {

	@Test
	public void testEntityAnnotation() throws Exception {

		assertNotNull(IssueHJIII35Type.class.getMethod("getHjversion",
				new Class[0]).getAnnotation(Version.class));
	}

}
