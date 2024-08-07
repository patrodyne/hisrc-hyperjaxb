package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.EmbeddedId;

public class EmbeddedIdMapping implements FieldOutlineMapping<EmbeddedId> {

	@Override
	public EmbeddedId process(Mapping context, FieldOutline fieldOutline) {

		final EmbeddedId embeddedId = context.getCustomizing().getEmbeddedId(
				fieldOutline);

		createEmbeddedId$Name(context, fieldOutline, embeddedId);
		context.getAttributeMapping().createAttributeOverride(context,
				fieldOutline, embeddedId.getAttributeOverride());

		return embeddedId;
	}

	public void createEmbeddedId$Name(Mapping context,
			FieldOutline fieldOutline, final EmbeddedId embeddedId) {
		embeddedId.setName(context.getNaming().getPropertyName(context,
				fieldOutline));
	}
}
