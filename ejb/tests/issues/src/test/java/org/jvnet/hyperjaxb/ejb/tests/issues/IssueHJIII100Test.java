package org.jvnet.hyperjaxb.ejb.tests.issues;

import jakarta.persistence.NamedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class IssueHJIII100Test {

	@Test
	public void testEntityAnnotation() throws Exception {

		assertNotNull(IssueHJIII100Type.class
				.getAnnotation(NamedQuery.class));

		assertEquals(
				1,
				IssueHJIII100Type.class.getAnnotation(NamedQuery.class).hints().length);
	}

}
