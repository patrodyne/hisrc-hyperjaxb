package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.Column;

public class BasicMapping implements FieldOutlineMapping<Basic> {

	@Override
	public Basic process(Mapping context, FieldOutline fieldOutline) {

		final Basic basic = context.getCustomizing().getBasic(fieldOutline);

		createBasic$Name(context, fieldOutline, basic);

		createBasic$Column(context, fieldOutline, basic);

		if (basic.getLob() == null && basic.getTemporal() == null
				&& basic.getEnumerated() == null) {
			if (context.getAttributeMapping().isTemporal(context, fieldOutline)) {
				basic.setTemporal(context.getAttributeMapping().createTemporalType(
						context, fieldOutline));
			} else if (context.getAttributeMapping().isEnumerated(context, fieldOutline)) {
				basic.setEnumerated(context.getAttributeMapping()
						.createEnumerated(context, fieldOutline));
			} else if (context.getAttributeMapping().isLob(context, fieldOutline)) {
				basic.setLob(context.getAttributeMapping().createLob(context, fieldOutline));
			}

		}

		return basic;
	}

	public void createBasic$Name(Mapping context, FieldOutline fieldOutline,
			final Basic basic) {
		basic.setName(context.getNaming()
				.getPropertyName(context, fieldOutline));
	}

	public void createBasic$Column(Mapping context, FieldOutline fieldOutline,
			final Basic basic) {
		if (basic.getColumn() == null) {
			basic.setColumn(new Column());
		}

		basic.setColumn(context.getAttributeMapping().createColumn(context, fieldOutline, basic.getColumn()));
	}

}
