package org.jvnet.hyperjaxb.ejb.strategy.service;

import static org.jvnet.hyperjaxb.ejb.strategy.Variant.Type.EJB;
import static org.jvnet.hyperjaxb.ejb.strategy.Variant.Type.JPA;

import java.io.IOException;

import org.jvnet.basicjaxb.config.LocatorProperties;
import org.jvnet.basicjaxb.config.LocatorUnmarshaller;
import org.jvnet.hyperjaxb.ejb.jpa.strategy.model.base.WrapCollectionBuiltinNonReference;
import org.jvnet.hyperjaxb.ejb.jpa.strategy.model.base.WrapCollectionEnumNonReference;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.MojoConfigured;
import org.jvnet.hyperjaxb.ejb.strategy.Variant;
import org.jvnet.hyperjaxb.ejb.strategy.annotate.CreateXAnnotations;
import org.jvnet.hyperjaxb.ejb.strategy.annotate.DefaultCreateXAnnotations;
import org.jvnet.hyperjaxb.ejb.strategy.customizing.impl.DefaultCustomizing;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.ClassOutlineMapping;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapCollectionAttribute;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapCollectionElement;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapCollectionHeteroElement;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapCollectionHeteroReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapCollectionValue;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleBuiltinNonReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleBuiltinReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleClassElementReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleEnumElementReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleEnumNonReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleHeteroElement;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleHeteroReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleSubstitutedElementReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.WrapSingleWildcardReference;
import org.jvnet.hyperjaxb.ejb.strategy.naming.ReservedNames;
import org.jvnet.hyperjaxb.ejb.strategy.naming.impl.DefaultReservedNames;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.processor.ClassPersistence;
import org.jvnet.hyperjaxb.ejb.strategy.processor.MappingFilePersistence;
import org.jvnet.hyperjaxb.jpa.Persistence;

import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * CDI Producer methods for default and custom strategies.
 */
@ApplicationScoped
public class StrategyProducer
{
	@Inject
	private StrategyService strategyService;
	public StrategyService getStrategyService() { return strategyService; }
	
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	private LocatorProperties beansProperties;
	public LocatorProperties getBeansProperties() { return beansProperties; }
	public void setBeansProperties(LocatorProperties beansProperties) { this.beansProperties = beansProperties; }

	private Marshaller marshaller;
	public Marshaller getMarshaller() { return marshaller; }
	public void setMarshaller(Marshaller marshaller) { this.marshaller = marshaller; }

	private Unmarshaller unmarshaller;
	public Unmarshaller getUnmarshaller() { return unmarshaller; }
	public void setUnmarshaller(Unmarshaller unmarshaller) { this.unmarshaller = unmarshaller; }
	
	@Produces
	public ReservedNames produceReservedNames()
	{
		ReservedNames reservedNames = new DefaultReservedNames();
		String locator = getBeansProperties().getProperty("reservedNames");
		if ( locator == null )
			locator = "classpath:ReservedNames.properties";
		try
		{
			reservedNames.load(locator);
		}
		catch (IOException ex)
		{
			getPlugin().warn("Cannot load '" + locator + "'", ex);
		}
		return reservedNames;
	}
	
	@Produces
	public Persistence producePersistence()
	{
		String locator = getBeansProperties().getProperty("defaultCustomizations");
		if ( locator == null )
			locator = "classpath:DefaultCustomizations.xml";

		LocatorUnmarshaller<Persistence> locatorUnmarshaller =
			new LocatorUnmarshaller<>(getUnmarshaller());
		
		Persistence defaultCustomizations = null;
		try
		{
			defaultCustomizations = locatorUnmarshaller.unmarshal(locator, DefaultCustomizing.class);
		}
		catch (IOException | JAXBException ex)
		{
			getPlugin().error("Cannot unmarshal default customizations", ex);
		}
		return defaultCustomizations;
	}
	
	@Produces @MojoConfigured
	public ClassOutlineMapping<EmbeddableAttributes> producesEmbeddableAttributesMapping
	(
		@Variant(type = EJB) ClassOutlineMapping<EmbeddableAttributes> ejbEmbeddableAttributesMapping,
		@Variant(type = JPA) ClassOutlineMapping<EmbeddableAttributes> jpaEmbeddableAttributesMapping
	)
	{
		ClassOutlineMapping<EmbeddableAttributes> classOutlineMapping = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				classOutlineMapping = jpaEmbeddableAttributesMapping;
				break;
			case "Xhyperjaxb-ejb":
			default:
				classOutlineMapping = ejbEmbeddableAttributesMapping;
				break;
		}
		return classOutlineMapping;
	}
	
	@Produces @MojoConfigured
	public CreateXAnnotations producesCreateXAnnotations
	(
		@Variant(type = EJB) DefaultCreateXAnnotations ejbCreateXAnnotations,
		@Variant(type = JPA) DefaultCreateXAnnotations jpaCreateXAnnotations
	)
	{
		CreateXAnnotations createXAnnotations = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				createXAnnotations = jpaCreateXAnnotations;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createXAnnotations = ejbCreateXAnnotations;
				break;
		}
		return createXAnnotations;
	}
	
	/**
	 * Produce either a @ClassPersistence or a @MappingFilePersistence
	 * outline processor.
	 * 
	 * The Mojo configured parameter is ModelAndOutlineProcessorBeanName from "result".
	 * 
	 * @param classPersistenceProcessor An injected @ClassPersistence processor.
	 * @param mappingFilePersistenceProcessor An injected @MappingFilePersistence processor.
	 * @return An outline processor for the Mojo configured parameter.
	 */
	@Produces @MojoConfigured
	public OutlineProcessor<EJBPlugin> produceOutlineProcessorEJBPlugin
	(
		@ClassPersistence OutlineProcessor<EJBPlugin> classPersistenceProcessor,
		@MappingFilePersistence OutlineProcessor<EJBPlugin> mappingFilePersistenceProcessor
	)
	{
		OutlineProcessor<EJBPlugin> outlineProcessor = null;
		switch ( getPlugin().getModelAndOutlineProcessorBeanName().toLowerCase() )
		{
			case "mappingfiles":
				outlineProcessor = mappingFilePersistenceProcessor;
				break;
			case "annotations":
			default:
				outlineProcessor = classPersistenceProcessor;
				break;
		}
		return outlineProcessor;
	}
	
	// Wrap Attribute
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Attribute)
	public CreatePropertyInfos produceWrapSingleBuiltinAttribute(WrapSingleBuiltinNonReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Attribute)
	public CreatePropertyInfos produceWrapSingleEnumAttribute(WrapSingleEnumNonReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Attribute)
	public CreatePropertyInfos produceWrapCollectionBuiltinAttribute
	(
		WrapCollectionAttribute ejbCreatePropertyInfos,
		WrapCollectionBuiltinNonReference jpaCreatePropertyInfos
	)
	{
		CreatePropertyInfos createPropertyInfos = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				jpaCreatePropertyInfos.setFallback(ejbCreatePropertyInfos);
				createPropertyInfos = jpaCreatePropertyInfos;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createPropertyInfos = ejbCreatePropertyInfos;
				break;
		}
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Attribute)
	public CreatePropertyInfos produceWrapCollectionEnumAttribute
	(
		WrapCollectionAttribute ejbCreatePropertyInfos,
		WrapCollectionEnumNonReference jpaCreatePropertyInfos
	)
	{
		CreatePropertyInfos createPropertyInfos = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				createPropertyInfos = jpaCreatePropertyInfos;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createPropertyInfos = ejbCreatePropertyInfos;
				break;
		}
		return createPropertyInfos;
	}
	
	// Wrap Value
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Value)
	public CreatePropertyInfos produceWrapSingleBuiltInValue(WrapSingleBuiltinNonReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Value)
	public CreatePropertyInfos produceWrapSingleEnumValue(WrapSingleEnumNonReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Value)
	public CreatePropertyInfos produceWrapCollectionBuiltInValue
	(
		WrapCollectionValue ejbCreatePropertyInfos,
		WrapCollectionBuiltinNonReference jpaCreatePropertyInfos
	)
	{
		CreatePropertyInfos createPropertyInfos = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				jpaCreatePropertyInfos.setFallback(ejbCreatePropertyInfos);
				createPropertyInfos = jpaCreatePropertyInfos;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createPropertyInfos = ejbCreatePropertyInfos;
				break;
		}
		return createPropertyInfos;
	}

	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Value)
	public CreatePropertyInfos produceWrapCollectionEnumValue
	(
		WrapCollectionValue ejbCreatePropertyInfos,
		WrapCollectionEnumNonReference jpaCreatePropertyInfos
	)
	{
		CreatePropertyInfos createPropertyInfos = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				createPropertyInfos = jpaCreatePropertyInfos;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createPropertyInfos = ejbCreatePropertyInfos;
				break;
		}
		return createPropertyInfos;
	}
	
	// Wrap Element
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Element)
	public CreatePropertyInfos produceWrapSingleBuiltInElement(WrapSingleBuiltinNonReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Element)
	public CreatePropertyInfos produceWrapSingleEnumElement(WrapSingleEnumNonReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Element)
	public CreatePropertyInfos produceWrapSingleHeteroElement(WrapSingleHeteroElement createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Element)
	public CreatePropertyInfos produceWrapCollectionBuiltInElement
	(
		WrapCollectionElement ejbCreatePropertyInfos,
		WrapCollectionBuiltinNonReference jpaCreatePropertyInfos
	)
	{
		CreatePropertyInfos createPropertyInfos = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				jpaCreatePropertyInfos.setFallback(ejbCreatePropertyInfos);
				createPropertyInfos = jpaCreatePropertyInfos;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createPropertyInfos = ejbCreatePropertyInfos;
				break;
		}
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Element)
	public CreatePropertyInfos produceWrapCollectionEnumElement
	(
		WrapCollectionElement ejbCreatePropertyInfos,
		WrapCollectionEnumNonReference jpaCreatePropertyInfos
	)
	{
		CreatePropertyInfos createPropertyInfos = null;
		switch ( getPlugin().getOptionName() )
		{
			case "Xhyperjaxb-jpa":
				createPropertyInfos = jpaCreatePropertyInfos;
				break;
			case "Xhyperjaxb-ejb":
			default:
				createPropertyInfos = ejbCreatePropertyInfos;
				break;
		}
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Element)
	public CreatePropertyInfos produceWrapCollectionHeteroElement(WrapCollectionHeteroElement createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	// Wrap Element Reference

	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.ElementReference)
	public CreatePropertyInfos produceWrapSingleBuiltInElementReference(WrapSingleBuiltinReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.ElementReference)
	public CreatePropertyInfos produceWrapSingleEnumElementReference(WrapSingleEnumElementReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Class, schemaType = ModelWrap.SchemaType.ElementReference)
	public CreatePropertyInfos produceWrapSingleClassElementReference(WrapSingleClassElementReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Substituted, schemaType = ModelWrap.SchemaType.ElementReference)
	public CreatePropertyInfos produceWrapSingleSubstitutedElementReference(WrapSingleSubstitutedElementReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	// Wrap Reference

	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Reference)
	public CreatePropertyInfos produceWrapSingleHeteroReference(WrapSingleHeteroReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Class, schemaType = ModelWrap.SchemaType.Reference)
	public CreatePropertyInfos produceWrapSingleClassReference(WrapSingleHeteroReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Wildcard, schemaType = ModelWrap.SchemaType.Reference)
	public CreatePropertyInfos produceWrapSingleWildcardReference(WrapSingleWildcardReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Reference)
	public CreatePropertyInfos produceWrapCollectionHeteroReference(WrapCollectionHeteroReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
	
	@Produces @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Wildcard, schemaType = ModelWrap.SchemaType.Reference)
	public CreatePropertyInfos produceWrapCollectionWildcardReference(WrapCollectionHeteroReference createPropertyInfos)
	{
		return createPropertyInfos;
	}
}
