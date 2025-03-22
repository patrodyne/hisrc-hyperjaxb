package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import org.jvnet.hyperjaxb.ejb.strategy.customizing.Customizing;

import com.sun.tools.xjc.outline.ClassOutline;

import ee.jakarta.xml.ns.persistence.orm.Embeddable;
import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;

public class EntityOrMappedSuperclassOrEmbeddableMapping implements
		ClassOutlineMapping<Object> {

	@Override
	public Object process(Mapping context, ClassOutline classOutline) {
		final Customizing customizing = context.getCustomizing();
		final Object entityOrMappedSuperclass =
			customizing.getEntityOrMappedSuperclassOrEmbeddable(classOutline);

		if (entityOrMappedSuperclass instanceof Entity) {
			return context.getEntityMapping().process(context, classOutline);
		} else if (entityOrMappedSuperclass instanceof MappedSuperclass) {
			return context.getMappedSuperclassMapping().process(context, classOutline);
		} else if (entityOrMappedSuperclass instanceof Embeddable) {
			return context.getEmbeddableMapping().process(context, classOutline);
		} else {
			throw new AssertionError(
					"Either one-to-many or many-to-many mappings are expected.");
		}
	}

}
