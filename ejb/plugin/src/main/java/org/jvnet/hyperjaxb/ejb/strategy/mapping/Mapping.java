package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import org.jvnet.hyperjaxb.ejb.strategy.MojoConfigured;
import org.jvnet.hyperjaxb.ejb.strategy.customizing.Customizing;
import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.model.GetTypes;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelBase;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;

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
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

/**
 * A context of injected and instantiated mappings for strategic processing.
 * 
 * Injected: GetTypes, ClassOutlineMapping, Customizing, Naming, Ignoring
 * Instantiated: ClassOutlineMapping, FieldOutlineMapping, AssociationMapping, AttributeMapping 
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class Mapping implements MappingContext
{
	@Inject @ModelBase
	private GetTypes<Mapping> getTypes;
	@Override
	public GetTypes<Mapping> getGetTypes()
	{
		return getTypes;
	}
	public void setGetTypes(GetTypes<Mapping> getTypes)
	{
		this.getTypes = getTypes;
	}

	@Inject
	private Customizing customizing;
	@Override
	public Customizing getCustomizing()
	{
		return customizing;
	}
	public void setCustomizing(Customizing modelCustomizations)
	{
		this.customizing = modelCustomizations;
	}

	@Inject
	private Naming naming;
	@Override
	public Naming getNaming()
	{
		return naming;
	}
	public void setNaming(Naming naming)
	{
		this.naming = naming;
	}

	@Inject
	private Ignoring ignoring;
	@Override
	public Ignoring getIgnoring()
	{
		return ignoring;
	}
	public void setIgnoring(Ignoring ignoring)
	{
		this.ignoring = ignoring;
	}
	
	@Inject @MojoConfigured
	private ClassOutlineMapping<EmbeddableAttributes> embeddableAttributesMapping;
	@Override
	public ClassOutlineMapping<EmbeddableAttributes> getEmbeddableAttributesMapping()
	{
		return embeddableAttributesMapping;
	}
	public void setEmbeddableAttributesMapping(ClassOutlineMapping<EmbeddableAttributes> embeddableAttributesMapping)
	{
		this.embeddableAttributesMapping = embeddableAttributesMapping;
	}

	private ClassOutlineMapping<Object> entityOrMappedSuperclassOrEmbeddableMapping = new EntityOrMappedSuperclassOrEmbeddableMapping();
	@Override
	public ClassOutlineMapping<Object> getEntityOrMappedSuperclassOrEmbeddableMapping()
	{
		return entityOrMappedSuperclassOrEmbeddableMapping;
	}
	public void setEntityOrMappedSuperclassOrEmbeddableMapping(ClassOutlineMapping<Object> entityOrMappedSuperclassMapping)
	{
		this.entityOrMappedSuperclassOrEmbeddableMapping = entityOrMappedSuperclassMapping;
	}

	private ClassOutlineMapping<Entity> entityMapping = new EntityMapping();
	@Override
	public ClassOutlineMapping<Entity> getEntityMapping()
	{
		return entityMapping;
	}
	public void setEntityMapping(ClassOutlineMapping<Entity> entityMapping)
	{
		this.entityMapping = entityMapping;
	}

	private ClassOutlineMapping<MappedSuperclass> mappedSuperclassMapping = new MappedSuperclassMapping();
	@Override
	public ClassOutlineMapping<MappedSuperclass> getMappedSuperclassMapping()
	{
		return mappedSuperclassMapping;
	}
	public void setMappedSuperclassMapping(ClassOutlineMapping<MappedSuperclass> mappedSuperclassMapping)
	{
		this.mappedSuperclassMapping = mappedSuperclassMapping;
	}

	private ClassOutlineMapping<Embeddable> embeddableMapping = new EmbeddableMapping();
	@Override
	public ClassOutlineMapping<Embeddable> getEmbeddableMapping()
	{
		return embeddableMapping;
	}
	public void setEmbeddableMapping(ClassOutlineMapping<Embeddable> embeddableMapping)
	{
		this.embeddableMapping = embeddableMapping;
	}

	private ClassOutlineMapping<Attributes> attributesMapping = new AttributesMapping();
	@Override
	public ClassOutlineMapping<Attributes> getAttributesMapping()
	{
		return attributesMapping;
	}
	public void setAttributesMapping(ClassOutlineMapping<Attributes> attributesMapping)
	{
		this.attributesMapping = attributesMapping;
	}

	private FieldOutlineMapping<Id> idMapping = new IdMapping();
	@Override
	public FieldOutlineMapping<Id> getIdMapping()
	{
		return idMapping;
	}
	public void setIdMapping(FieldOutlineMapping<Id> idMapping)
	{
		this.idMapping = idMapping;
	}

	private FieldOutlineMapping<EmbeddedId> embeddedIdMapping = new EmbeddedIdMapping();
	@Override
	public FieldOutlineMapping<EmbeddedId> getEmbeddedIdMapping()
	{
		return embeddedIdMapping;
	}
	public void setEmbeddedIdMapping(FieldOutlineMapping<EmbeddedId> embeddedIdMapping)
	{
		this.embeddedIdMapping = embeddedIdMapping;
	}

	private FieldOutlineMapping<Basic> basicMapping = new BasicMapping();
	@Override
	public FieldOutlineMapping<Basic> getBasicMapping()
	{
		return basicMapping;
	}
	public void setBasicMapping(FieldOutlineMapping<Basic> basicMapping)
	{
		this.basicMapping = basicMapping;
	}

	private FieldOutlineMapping<Version> versionMapping = new VersionMapping();
	@Override
	public FieldOutlineMapping<Version> getVersionMapping()
	{
		return versionMapping;
	}

	public void setVersionMapping(FieldOutlineMapping<Version> versionMapping)
	{
		this.versionMapping = versionMapping;
	}

	private FieldOutlineMapping<Embedded> embeddedMapping = new EmbeddedMapping();
	@Override
	public FieldOutlineMapping<Embedded> getEmbeddedMapping()
	{
		return embeddedMapping;
	}
	public void setEmbeddedMapping(FieldOutlineMapping<Embedded> embeddedMapping)
	{
		this.embeddedMapping = embeddedMapping;
	}

	private FieldOutlineMapping<?> toOneMapping = new ToOneMapping();
	@Override
	public FieldOutlineMapping<?> getToOneMapping()
	{
		return toOneMapping;
	}
	public void setToOneMapping(FieldOutlineMapping<?> toOneMapping)
	{
		this.toOneMapping = toOneMapping;
	}

	private FieldOutlineMapping<ElementCollection> elementCollectionMapping = new ElementCollectionMapping();
	@Override
	public FieldOutlineMapping<ElementCollection> getElementCollectionMapping()
	{
		return elementCollectionMapping;
	}
	public void setElementCollectionMapping(FieldOutlineMapping<ElementCollection> elementCollectionMapping)
	{
		this.elementCollectionMapping = elementCollectionMapping;
	}

	private FieldOutlineMapping<ManyToOne> manyToOneMapping = new ManyToOneMapping();
	@Override
	public FieldOutlineMapping<ManyToOne> getManyToOneMapping()
	{
		return manyToOneMapping;
	}
	public void setManyToOneMapping(FieldOutlineMapping<ManyToOne> manyToOneMapping)
	{
		this.manyToOneMapping = manyToOneMapping;
	}

	private FieldOutlineMapping<OneToOne> oneToOneMapping = new OneToOneMapping();
	@Override
	public FieldOutlineMapping<OneToOne> getOneToOneMapping()
	{
		return oneToOneMapping;
	}
	public void setOneToOneMapping(FieldOutlineMapping<OneToOne> oneToOneMapping)
	{
		this.oneToOneMapping = oneToOneMapping;
	}

	private FieldOutlineMapping<?> toManyMapping = new ToManyMapping();
	@Override
	public FieldOutlineMapping<?> getToManyMapping()
	{
		return toManyMapping;
	}
	public void setToManyMapping(FieldOutlineMapping<?> toManyMapping)
	{
		this.toManyMapping = toManyMapping;
	}

	private FieldOutlineMapping<OneToMany> oneToManyMapping = new OneToManyMapping();
	@Override
	public FieldOutlineMapping<OneToMany> getOneToManyMapping()
	{
		return oneToManyMapping;
	}
	public void setOneToManyMapping(FieldOutlineMapping<OneToMany> oneToManyMapping)
	{
		this.oneToManyMapping = oneToManyMapping;
	}

	private FieldOutlineMapping<ManyToMany> manyToManyMapping = new ManyToManyMapping();
	@Override
	public FieldOutlineMapping<ManyToMany> getManyToManyMapping()
	{
		return manyToManyMapping;
	}

	public void setManyToManyMapping(FieldOutlineMapping<ManyToMany> manyToManyMapping)
	{
		this.manyToManyMapping = manyToManyMapping;
	}

	private FieldOutlineMapping<Transient> transientMapping = new TransientMapping();
	@Override
	public FieldOutlineMapping<Transient> getTransientMapping()
	{
		return transientMapping;
	}
	public void setTransientMapping(FieldOutlineMapping<Transient> transientMapping)
	{
		this.transientMapping = transientMapping;
	}

	private AssociationMapping associationMapping = new DefaultAssociationMapping();
	@Override
	public AssociationMapping getAssociationMapping()
	{
		return associationMapping;
	}
	public void setAssociationMapping(AssociationMapping associationMapping)
	{
		this.associationMapping = associationMapping;
	}

	private AttributeMapping defaultAttributeMapping = new DefaultAttributeMapping();
	@Override
	public AttributeMapping getAttributeMapping()
	{
		return defaultAttributeMapping;
	}
	public void setAttributeMapping(AttributeMapping defaultAttributeMapping)
	{
		this.defaultAttributeMapping = defaultAttributeMapping;
	}

	@Override
	public Mapping createEmbeddedMapping(Mapping context, FieldOutline fieldOutline)
	{
		// TODO Rework with wrappers
		final Mapping embeddedMapping = clone();
		embeddedMapping.setNaming(getNaming().createEmbeddedNaming(context, fieldOutline));
		embeddedMapping.setAssociationMapping(getAssociationMapping().createEmbeddedAssociationMapping(fieldOutline));
		return embeddedMapping;
	}

	@Override
	public Mapping clone()
	{
		try
		{
			return (Mapping) super.clone();
		}
		catch (CloneNotSupportedException cnsex)
		{
			throw new UnsupportedOperationException(cnsex);
		}
	}
}
