package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import java.util.List;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.AttributeOverride;
import ee.jakarta.xml.ns.persistence.orm.Column;
import ee.jakarta.xml.ns.persistence.orm.Lob;

public interface AttributeMapping
{
	public Column createColumn(Mapping context, FieldOutline fieldOutline, Column column);

	public boolean isTemporal(Mapping context, FieldOutline fieldOutline);

	public String createTemporalType(Mapping context, FieldOutline fieldOutline);

	public boolean isLob(Mapping context, FieldOutline fieldOutline);

	public Lob createLob(Mapping context, FieldOutline fieldOutline);

	public boolean isEnumerated(Mapping context, FieldOutline fieldOutline);

	public String createEnumerated(Mapping context, FieldOutline fieldOutline);

	public void createAttributeOverride(Mapping context, FieldOutline fieldOutline,
		final List<AttributeOverride> attributeOverrides);
}