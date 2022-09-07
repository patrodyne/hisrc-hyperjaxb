package org.jvnet.hyperjaxb3.ejb.tests.issues;

import jakarta.persistence.Column;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import org.jvnet.annox.reflect.AnnotatedElementFactory;
import org.jvnet.annox.reflect.DualAnnotatedElementFactory;
import org.jvnet.annox.reflect.ParameterizedAnnotatedElement;

public class IssueHJIII28Test {
	
	@Test
	public void testLengthAnnotation() throws Exception {
		
		final AnnotatedElementFactory aef = new DualAnnotatedElementFactory();
		
		final ParameterizedAnnotatedElement annotatedElement = aef.getAnnotatedElement(IssueHJIII28ComplexType.class.getMethod("getIssueHJIII28"));
		
		final Column annotation = annotatedElement.getAnnotation(Column.class);
		
		assertEquals(1024, annotation.length());
		
		

		
	}

}
