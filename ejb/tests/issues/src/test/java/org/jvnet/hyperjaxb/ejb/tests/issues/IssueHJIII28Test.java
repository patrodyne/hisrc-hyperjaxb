package org.jvnet.hyperjaxb.ejb.tests.issues;

import jakarta.persistence.Column;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.jvnet.basicjaxb_annox.reflect.AnnotatedElementFactory;
import org.jvnet.basicjaxb_annox.reflect.DualAnnotatedElementFactory;
import org.jvnet.basicjaxb_annox.reflect.ParameterizedAnnotatedElement;

public class IssueHJIII28Test {
	
	@Test
	public void testLengthAnnotation() throws Exception {
		
		final AnnotatedElementFactory aef = new DualAnnotatedElementFactory();
		
		final ParameterizedAnnotatedElement annotatedElement = aef.getAnnotatedElement(IssueHJIII28ComplexType.class.getMethod("getIssueHJIII28"));
		
		final Column annotation = annotatedElement.getAnnotation(Column.class);
		
		assertEquals(1024, annotation.length());
		
		

		
	}

}
