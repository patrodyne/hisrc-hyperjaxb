package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import org.jvnet.basicjaxb.util.OutlineUtils;

import com.sun.tools.xjc.outline.ClassOutline;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;

public class MappedSuperclassMapping implements
		ClassOutlineMapping<MappedSuperclass> {

	@Override
	public MappedSuperclass process(Mapping context, ClassOutline classOutline) {
		final MappedSuperclass entity = context.getCustomizing()
				.getMappedSuperclass(classOutline);
		createMappedSuperclass(context, classOutline, entity);
		return entity;
	}

	public void createMappedSuperclass(Mapping context,
			ClassOutline classOutline, final MappedSuperclass mappedSuperclass) {
		createMappedSuperclass$Class(context, classOutline, mappedSuperclass);
		/*
		 * createEntity$Inheritance(context, classOutline, mappedSuperclass);
		 * 
		 * createEntity$Table(context, classOutline, mappedSuperclass);
		 */
		createMappedSuperclass$Attributes(context, classOutline,
				mappedSuperclass);
	}

	public void createMappedSuperclass$Class(Mapping context,
			ClassOutline classOutline, final MappedSuperclass mappedSuperclass) {
		if (mappedSuperclass.getClazz() == null
				|| "##default".equals(mappedSuperclass.getClazz())) {
			mappedSuperclass.setClazz(OutlineUtils.getPackagedClassName(classOutline));
		}
	}

	public void createMappedSuperclass$Attributes(Mapping context,
			ClassOutline classOutline, final MappedSuperclass mappedSuperclass) {
		final Attributes attributes =
			context.getAttributesMapping().process(context, classOutline);
		mappedSuperclass.setAttributes(attributes);
	}

}
