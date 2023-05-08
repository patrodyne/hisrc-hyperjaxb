package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import org.jvnet.basicjaxb.util.OutlineUtils;

import com.sun.tools.xjc.outline.ClassOutline;

import ee.jakarta.xml.ns.persistence.orm.Embeddable;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;

public class EmbeddableMapping implements ClassOutlineMapping<Embeddable> {

	// private static Log logger = LogFactory.getLog(EntityMapping.class);

	@Override
	public Embeddable process(Mapping context, ClassOutline classOutline) {
		final Embeddable entity = context.getCustomizing().getEmbeddable(
				classOutline);
		createEmbeddable(context, classOutline, entity);
		return entity;
	}

	public void createEmbeddable(Mapping context, ClassOutline classOutline,
			final Embeddable entity) {
		createEmbeddable$Class(context, classOutline, entity);

		createEmbeddable$Attributes(context, classOutline, entity);
	}

	public void createEmbeddable$Class(Mapping context,
			ClassOutline classOutline, final Embeddable entity) {
		if (entity.getClazz() == null || "##default".equals(entity.getClazz())) {
			entity.setClazz(OutlineUtils.getClassName(classOutline));
		}
	}

	public void createEmbeddable$Attributes(Mapping context,
			ClassOutline classOutline, final Embeddable entity) {
		final EmbeddableAttributes attributes = context
				.getEmbeddableAttributesMapping().process(context, classOutline);
		entity.setAttributes(attributes);
	}

}
