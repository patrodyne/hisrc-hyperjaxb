package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Column;
import ee.jakarta.xml.ns.persistence.orm.Id;

public class IdMapping implements FieldOutlineMapping<Id> {

	@Override
	public Id process(Mapping context, FieldOutline fieldOutline) {

		final Id id = context.getCustomizing().getId(fieldOutline);

		createId$Name(context, fieldOutline, id);
		createId$Column(context, fieldOutline, id);
		createId$Temporal(context, fieldOutline, id);
		return id;
	}

	public void createId$Name(Mapping context, FieldOutline fieldOutline,
			final Id id) {
		id.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

	public void createId$Column(Mapping context, FieldOutline fieldOutline,
			final Id id) {
		if (id.getColumn() == null) {
			id.setColumn(new Column());
		}

		id.setColumn(context.getAttributeMapping().createColumn(context, fieldOutline, id.getColumn()));
	}

	public void createId$Temporal(Mapping context, FieldOutline fieldOutline,
			Id id) {
		if (id.getTemporal() == null && context.getAttributeMapping().isTemporal(context, fieldOutline)) {
			id.setTemporal(context.getAttributeMapping().createTemporalType(context, fieldOutline));
		}
	}

}
