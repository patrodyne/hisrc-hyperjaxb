package org.jvnet.hyperjaxb.ejb.strategy.annotate;

import java.util.Collection;

import org.jvnet.basicjaxb_annox.model.XAnnotation;

import ee.jakarta.xml.ns.persistence.orm.Embeddable;
import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;

public interface CreateXAnnotations
{
	public Collection<XAnnotation<?>> createEntityAnnotations(Entity entity);

	public Collection<XAnnotation<?>> createMappedSuperclassAnnotations(MappedSuperclass entity);

	public Collection<XAnnotation<?>> createEmbeddableAnnotations(Embeddable embeddable);

	public Collection<XAnnotation<?>> createAttributeAnnotations(Object attribute);
}
