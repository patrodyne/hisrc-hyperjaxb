package org.jvnet.hyperjaxb3.ejb.strategy.mapping;

import org.jvnet.hyperjaxb3.ejb.strategy.customizing.Customizing;
import org.jvnet.hyperjaxb3.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb3.ejb.strategy.model.GetTypes;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.Naming;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.ElementCollection;
import ee.jakarta.xml.ns.persistence.orm.Embeddable;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import ee.jakarta.xml.ns.persistence.orm.Embedded;
import ee.jakarta.xml.ns.persistence.orm.EmbeddedId;
import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.Id;
import ee.jakarta.xml.ns.persistence.orm.ManyToMany;
import ee.jakarta.xml.ns.persistence.orm.ManyToOne;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;
import ee.jakarta.xml.ns.persistence.orm.OneToMany;
import ee.jakarta.xml.ns.persistence.orm.OneToOne;
import ee.jakarta.xml.ns.persistence.orm.Transient;
import ee.jakarta.xml.ns.persistence.orm.Version;

/**
 * Interface for a context of injected and instantiated mappings for strategic processing.
 */
public interface MappingContext extends Cloneable
{
	public GetTypes<Mapping> getGetTypes();
	public Customizing getCustomizing();
	public Naming getNaming();
	public Ignoring getIgnoring();
	public ClassOutlineMapping<EmbeddableAttributes> getEmbeddableAttributesMapping();

	public ClassOutlineMapping<Object> getEntityOrMappedSuperclassOrEmbeddableMapping();
	public ClassOutlineMapping<Entity> getEntityMapping();
	public ClassOutlineMapping<MappedSuperclass> getMappedSuperclassMapping();
	public ClassOutlineMapping<Embeddable> getEmbeddableMapping();
	public ClassOutlineMapping<Attributes> getAttributesMapping();
	public FieldOutlineMapping<Id> getIdMapping();
	public FieldOutlineMapping<EmbeddedId> getEmbeddedIdMapping();
	public FieldOutlineMapping<Basic> getBasicMapping();
	public FieldOutlineMapping<Version> getVersionMapping();
	public FieldOutlineMapping<Embedded> getEmbeddedMapping();
	public FieldOutlineMapping<?> getToOneMapping();
	public FieldOutlineMapping<ElementCollection> getElementCollectionMapping();
	public FieldOutlineMapping<ManyToOne> getManyToOneMapping();
	public FieldOutlineMapping<OneToOne> getOneToOneMapping();
	public FieldOutlineMapping<?> getToManyMapping();
	public FieldOutlineMapping<OneToMany> getOneToManyMapping();
	public FieldOutlineMapping<ManyToMany> getManyToManyMapping();
	public FieldOutlineMapping<Transient> getTransientMapping();
	public AssociationMapping getAssociationMapping();
	public AttributeMapping getAttributeMapping();
	public Mapping createEmbeddedMapping(Mapping context, FieldOutline fieldOutline);

}
