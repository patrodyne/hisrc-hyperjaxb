package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import java.util.Collection;
import java.util.List;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.AssociationOverride;
import ee.jakarta.xml.ns.persistence.orm.JoinColumn;
import ee.jakarta.xml.ns.persistence.orm.JoinTable;
import ee.jakarta.xml.ns.persistence.orm.OrderColumn;
import ee.jakarta.xml.ns.persistence.orm.PrimaryKeyJoinColumn;

public interface AssociationMapping {

	public Collection<FieldOutline> getSourceIdFieldsOutline(Mapping context,
			FieldOutline fieldOutline);

	public Collection<FieldOutline> getTargetIdFieldsOutline(Mapping context,
			FieldOutline fieldOutline);

	// * 1:1
	public void createPrimaryKeyJoinColumns(Mapping context,
			FieldOutline fieldOutline,
			Collection<FieldOutline> idFieldOutlines,
			List<PrimaryKeyJoinColumn> primaryKeyJoinColumns);

	// * M:1
	// * 1:M
	// * 1:1
	public void createJoinColumns(Mapping context, FieldOutline fieldOutline,
			Collection<FieldOutline> idFieldOutlines,
			List<JoinColumn> joinColumns);

	// 1:1
	// M:1
	// 1:M
	// M:M
	public void createJoinTable(Mapping context, FieldOutline fieldOutline,
			Collection<FieldOutline> sourceIdFieldOutlines,
			Collection<FieldOutline> targetIdFieldOutlines, JoinTable joinTable);

	public void createOrderColumn(Mapping context, FieldOutline fieldOutline,
			final OrderColumn orderColumn);

	public void createElementCollection$OrderColumn(Mapping context,
			FieldOutline fieldOutline, final OrderColumn orderColumn);

	public void createElementCollection$CollectionTable$JoinColumns(
			Mapping context, FieldOutline fieldOutline,
			Collection<FieldOutline> idFieldOutlines,
			List<JoinColumn> joinColumns);

	public AssociationMapping createEmbeddedAssociationMapping(
			FieldOutline fieldOutline);

	public void createAssociationOverride(Mapping context,
			FieldOutline fieldOutline,
			final List<AssociationOverride> associationOverrides);

}