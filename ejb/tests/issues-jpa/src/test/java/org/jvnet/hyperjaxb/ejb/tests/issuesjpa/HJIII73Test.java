package org.jvnet.hyperjaxb.ejb.tests.issuesjpa;

import jakarta.persistence.OrderColumn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.jvnet.basicjaxb_annox.reflect.AnnotatedElementFactory;
import org.jvnet.basicjaxb_annox.reflect.DualAnnotatedElementFactory;
import org.jvnet.basicjaxb_annox.reflect.ParameterizedAnnotatedElement;

@Order(3)
public class HJIII73Test
{
	@Test
	public void testLengthAnnotation() throws Exception
	{

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
