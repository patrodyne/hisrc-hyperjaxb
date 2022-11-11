package org.jvnet.hyperjaxb.ejb.tests.issues;

import jakarta.persistence.Entity;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import org.jvnet.hyperjaxb.ejb.tests.issuesignored.IssueHJIII32ComplexType;

public class IssueHJIII32Test {
	
	@Test
	public void testEntityAnnotation() throws Exception {
		
		assertNull(IssueHJIII32ComplexType.class.getAnnotation(Entity.class));
		
	}

}
