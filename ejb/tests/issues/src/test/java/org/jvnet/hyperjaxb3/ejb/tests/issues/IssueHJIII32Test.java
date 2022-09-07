package org.jvnet.hyperjaxb3.ejb.tests.issues;

import jakarta.persistence.Entity;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import org.jvnet.hyperjaxb3.ejb.tests.issuesignored.IssueHJIII32ComplexType;

public class IssueHJIII32Test {
	
	@Test
	public void testEntityAnnotation() throws Exception {
		
		assertNull(IssueHJIII32ComplexType.class.getAnnotation(Entity.class));
		
		

		
	}

}
