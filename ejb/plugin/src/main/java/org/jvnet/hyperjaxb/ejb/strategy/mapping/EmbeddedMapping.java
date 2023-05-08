package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Embedded;

public class EmbeddedMapping implements FieldOutlineMapping<Embedded>
{
	@Override
	public Embedded process(Mapping context, FieldOutline fieldOutline)
	{
		final Embedded embedded = context.getCustomizing().getEmbedded(fieldOutline);

		createEmbedded$Name(context, fieldOutline, embedded);

		context.getAttributeMapping()
			.createAttributeOverride(context, fieldOutline, embedded.getAttributeOverride());

		context.getAssociationMapping()
			.createAssociationOverride(context,	fieldOutline, embedded.getAssociationOverride());

		return embedded;
	}

	public void createEmbedded$Name(Mapping context, FieldOutline fieldOutline, final Embedded embedded)
	{
		embedded.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}
}
