package org.jvnet.hyperjaxb3.ejb.tests.issues;

import jakarta.persistence.NamedQuery;

import junit.framework.Assert;
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
