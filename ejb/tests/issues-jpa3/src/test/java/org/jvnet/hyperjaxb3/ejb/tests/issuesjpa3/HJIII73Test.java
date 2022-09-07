package org.jvnet.hyperjaxb3.ejb.tests.issuesjpa3;

import jakarta.persistence.OrderColumn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.jvnet.annox.reflect.AnnotatedElementFactory;
import org.jvnet.annox.reflect.DualAnnotatedElementFactory;
import org.jvnet.annox.reflect.ParameterizedAnnotatedElement;

public class HJIII73Test {

	@Test
	public void testLengthAnnotation() throws Exception {

		final AnnotatedElementFactory aef = new DualAnnotatedElementFactory();

		final ParameterizedAnnotatedElement o2m = aef
				.getAnnotatedElement(HJIII73Parent.class
						.getMethod("getHJIII73ChildOneToMany"));
		final ParameterizedAnnotatedElement m2m = aef
				.getAnnotatedElement(HJIII73Parent.class
						.getMethod("getHJIII73ChildManyToMany"));

		assertNotNull(o2m.getAnnotation(OrderColumn.class));
		assertTrue(o2m.getAnnotation(OrderColumn.class).name().length() > 0);
		assertNotNull(m2m.getAnnotation(OrderColumn.class));
		assertEquals("ORDNUNG", m2m.getAnnotation(OrderColumn.class).name());
	}

}
