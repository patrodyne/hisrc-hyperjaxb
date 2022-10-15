package org.jvnet.hyperjaxb3.ejb.strategy.customizing;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Basic;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.ElementCollection;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Embeddable;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Embedded;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.EmbeddedId;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Entity;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.GeneratedClass;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.GeneratedId;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.GeneratedProperty;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.GeneratedVersion;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Id;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.JaxbContext;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.ManyToMany;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.ManyToOne;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.MappedSuperclass;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.OneToMany;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.OneToOne;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Persistence;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Version;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 * Customizations interface.
 * 
 * There is no "one and only true" object-relational mapping for your classes.
 * There are many ways to express object constructs, properties and associations
 * relationally.
 * 
 * By default, HyperJAXB does its best to generate the most suitable and
 * reasonable mappings. However, HyperJAXB also allows you to influence the
 * generated object-relational mappings or annotations using the customization
 * elements.
 * 
 * HyperJAXB customization elements are essentially XML elements in the
 * HyperJAXB customization namespace associated with target XML Schema
 * constructs (complex types, elements, the schema itself and so on). This
 * association can be done directly in the schema file or alternatively in
 * external binding files.
 * 
 * When HyperJAXB processes schema constructs, it detects the associated
 * customization elements and use the provided information to customize the
 * generated mappings.
 */
public interface Customizing
{
	public <T> Collection<T> findCustomizations(Model model, QName name);
	public Object getEntityOrMappedSuperclassOrEmbeddable(ClassOutline classOutline);
	public Object getEntityOrMappedSuperclassOrEmbeddable(CClassInfo classInfo);
	public GeneratedId getGeneratedId(CClassInfo classInfo);
	public GeneratedClass getGeneratedClass(CPropertyInfo propertyInfo);
	public GeneratedProperty getGeneratedProperty(CPropertyInfo propertyInfo);
	public Id getId(CPropertyInfo property);
	public Id getId(FieldOutline property);
	public EmbeddedId getEmbeddedId(CPropertyInfo property);
	public EmbeddedId getEmbeddedId(FieldOutline property);
	public Basic getBasic(CPropertyInfo property);
	public Basic getBasic(FieldOutline property);
	public Version getVersion(CPropertyInfo property);
	public Version getVersion(FieldOutline property);
	public GeneratedVersion getGeneratedVersion(CClassInfo classInfo);
	public Object getToOne(CPropertyInfo property);
	public Object getToOne(FieldOutline property);
	public Object getToMany(CPropertyInfo property);
	public Object getToMany(FieldOutline property);
	public OneToMany getOneToMany(CPropertyInfo property);
	public OneToMany getOneToMany(FieldOutline property);
	public ManyToOne getManyToOne(CPropertyInfo property);
	public ManyToOne getManyToOne(FieldOutline property);
	public OneToOne getOneToOne(CPropertyInfo property);
	public OneToOne getOneToOne(FieldOutline property);
	public ManyToMany getManyToMany(CPropertyInfo property);
	public ManyToMany getManyToMany(FieldOutline property);
	public ElementCollection getElementCollection(CPropertyInfo property);
	public ElementCollection getElementCollection(FieldOutline property);
	public Embedded getEmbedded(CPropertyInfo property);
	public Embedded getEmbedded(FieldOutline property);
	public Entity getEntity(ClassOutline classOutline);
	public Embeddable getEmbeddable(ClassOutline classOutline);
	public MappedSuperclass getMappedSuperclass(ClassOutline classOutline);
	public JaxbContext getJaxbContext(FieldOutline fieldOutline);
	public JaxbContext getJaxbContext(CPropertyInfo property);
}
