package org.jvnet.hyperjaxb.annotation.util.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import org.jvnet.basicjaxb_annox.model.XAnnotation;
import org.jvnet.basicjaxb_annox.model.annotation.field.XAnnotationField;
import org.jvnet.hyperjaxb.annotation.util.AnnotationUtils;

public class AnnotationUtilsTest {

	@Test
	public void testA() throws Exception
	{
		final Collection<XAnnotation<?>> a = new LinkedList<XAnnotation<?>>();
		a.add(new XAnnotation<Override>(Override.class));
		a.add(new XAnnotation<Override>(Override.class));
		XAnnotationField<Annotation[]> xa = AnnotationUtils.create("test", a.toArray(new XAnnotation[a.size()]), Override.class);
		assertNotNull(xa, "XAnnotationField should be created.");
	}

	@Test
	public void testB() throws Exception
	{
		final Collection<XAnnotation<?>> a = new LinkedList<XAnnotation<?>>();
		XAnnotationField<Annotation[]> xa = AnnotationUtils.create("test", a.toArray(new XAnnotation[a.size()]), Override.class);
		assertNotNull(xa, "XAnnotationField should be created.");
	}
}
