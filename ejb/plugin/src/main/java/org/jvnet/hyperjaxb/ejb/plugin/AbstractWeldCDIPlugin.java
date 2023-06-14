package org.jvnet.hyperjaxb.ejb.plugin;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jvnet.hyperjaxb.ejb.jpa.strategy.model.base.WrapCollectionBuiltinNonReference;
import org.jvnet.hyperjaxb.ejb.strategy.annotate.AnnotateOutline;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.MappingContext;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.MarshalMappings;
import org.jvnet.hyperjaxb.ejb.strategy.model.ModelProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.DefaultProcessModel;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.processor.ClassPersistenceProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.processor.MappingFilePersistenceProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.processor.ModelAndOutlineProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.service.StrategyProducer;
import org.jvnet.hyperjaxb.ejb.strategy.service.StrategyService;
import org.jvnet.basicjaxb.config.LocatorProperties;
import org.jvnet.basicjaxb.plugin.AbstractParameterizablePlugin;

import com.sun.tools.xjc.outline.Outline;

import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * An XJC plugin that can be parameterized using '-X' arguments and is 
 * customizable via Weld/CDI alternatives.
 */
public abstract class AbstractWeldCDIPlugin
	extends AbstractParameterizablePlugin
{
	private Weld weld;
	public Weld getWeld() { return weld; }
	public void setWeld(Weld weld) { this.weld = weld; }
	
	private WeldContainer weldContainer;
	public WeldContainer getWeldContainer() { return weldContainer; }
	public void setWeldContainer(WeldContainer weldContainer) { this.weldContainer = weldContainer; }

	private BeanManager beanManager;
	public BeanManager getBeanManager() { return beanManager; }
	public void setBeanManager(BeanManager beanManager) { this.beanManager = beanManager; }

	private StrategyProducer strategyProducer;
	public StrategyProducer getStrategyProducer(){ return strategyProducer; }
	public void setStrategyProducer(StrategyProducer strategyProducer) { this.strategyProducer = strategyProducer; }
	
	private StrategyService strategyService;
	public StrategyService getStrategyService() { return strategyService; }
	public void setStrategyService(StrategyService strategyService) { this.strategyService = strategyService; }

	// Usage: <args>-Xhyperjaxb-ejb-beansPropertiesLocator=classpath:/META-INF/beans.properties</args>
	private String beansPropertiesLocator;
	public String getBeansPropertiesLocator() { return beansPropertiesLocator; }
	public void setBeansPropertiesLocator(String beansPropertiesLocator) { this.beansPropertiesLocator = beansPropertiesLocator; }

	private JAXBContext jaxbContext;
	public JAXBContext getJaxbContext() { return jaxbContext; }
	public void setJaxbContext(JAXBContext jaxbContext) { this.jaxbContext = jaxbContext; }

	@Override
	protected void beforeRun(Outline outline) throws Exception
	{
		super.beforeRun(outline);
		configureWeldContext();
	}
	
	@Override
	protected void afterRun(Outline outline) throws Exception
	{
		super.afterRun(outline);
		if ( getWeldContainer() != null )
			getWeldContainer().close();
	}

	private void configureWeldContext()
	{
		setWeld(new Weld());
		setWeldContainer(weld.initialize());
		setBeanManager(getWeldContainer().getBeanManager());
		logBeans();
		setStrategyService(getWeldContainer().select(StrategyService.class).get());
		setStrategyProducer(getWeldContainer().select(StrategyProducer.class).get());
		getStrategyProducer().setBeansProperties(loadBeansProperties());
		setJaxbContext(createJAXBContext());
		getStrategyProducer().setMarshaller(createMarshaller(getJaxbContext()));
		getStrategyProducer().setUnmarshaller(createUnmarshaller(getJaxbContext()));
	}

	private LocatorProperties loadBeansProperties()
	{
		LocatorProperties beansProperties = new LocatorProperties();
		if ( getBeansPropertiesLocator() != null )
		{
			try
			{
				beansProperties.load(getBeansPropertiesLocator());
			}
			catch (IOException ex)
			{
				warn("Beans properties not loaded: {}", getBeansPropertiesLocator(), ex);
			}			
		}
		return beansProperties;
	}
	
	private JAXBContext createJAXBContext()
	{
		JAXBContext jaxbContext = null;
		try
		{
			Class<?>[] classesToBeBound =
			{
				org.jvnet.hyperjaxb.jpa.ObjectFactory.class,
				ee.jakarta.xml.ns.persistence.orm.ObjectFactory.class,
				ee.jakarta.xml.ns.persistence.ObjectFactory.class
			 };
			jaxbContext = JAXBContext.newInstance(classesToBeBound);
		}
		catch ( JAXBException ex)
		{
			error("Cannot create JAXB context", ex);
		}
		return jaxbContext;
	}

	private Marshaller createMarshaller(JAXBContext jaxbContext)
	{
		Marshaller marshaller = null;
		try
		{
			if ( jaxbContext != null )
			{
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
			}
			else
				error("Cannot create marshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			error("Cannot create JAXB marshaller", ex);
		}
		return marshaller;
	}
	
	private Unmarshaller createUnmarshaller(JAXBContext jaxbContext)
	{
		Unmarshaller unmarshaller = null;
		try
		{
			if ( jaxbContext != null )
			{
				unmarshaller = jaxbContext.createUnmarshaller();
			}
			else
				error("Cannot create unmarshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			error("Cannot create JAXB unmarshaller", ex);
		}
		return unmarshaller;
	}

	/**
	 * Log any CDI beans.
	 */
	public void logBeans()
	{
		@SuppressWarnings("serial")
		AnnotationLiteral<Any> any = new AnnotationLiteral<Any>() {};
		Set<Bean<?>> beans = getBeanManager().getBeans(Object.class, any);
		logBeans(beans);
	}
	
	/**
	 * Log current CDI managed beans.
	 * 
	 * @param beans A set of CDI beans.
	 */
	public void logBeans(Set<Bean<?>> beans)
	{
		Map<String, Bean<?>> beanMap = new TreeMap<>();
		for (Bean<?> bean : beans)
			beanMap.put(bean.getBeanClass().getName(), bean);
		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append("Class\t|");
		sb.append("Scope\t|");
		sb.append("Type\t|");
		sb.append("Name\t|");
		sb.append("Qualifiers\t|");
		sb.append("InjectPoints\t|");
		sb.append("\n");
		for (Bean<?> bean : beanMap.values())
		{
			StringBuilder qsb = new StringBuilder();
			int qsz = bean.getQualifiers().size();
			for (  Annotation qualifier : bean.getQualifiers() )
				qsb.append(" @" + qualifier.annotationType().getSimpleName());
			int psz = bean.getInjectionPoints().size();
			StringBuilder psb = new StringBuilder();
			for ( InjectionPoint point : bean.getInjectionPoints() )
				psb.append(" " + point.getMember().getName());
			sb.append("\t");
			sb.append(bean.getBeanClass().getName() + "\t|");
			sb.append(bean.getScope().getSimpleName() + "\t|");
			sb.append(bean.getClass().getSimpleName() + "\t|");
			sb.append(bean.getName() + "\t|");
			sb.append(qsz + ":" + qsb + "\t|");
			sb.append(psz + ":" + psb + "\t|");
			sb.append("\n");
		}
		trace("CDI Beans: \n{}", sb);
	}
	
	public void logContext(Naming naming)
	{
		if ( isTraceEnabled() )
		{
			trace(identify(naming));
			trace(identify(naming.getReservedNames()));
			trace(identify(naming.getIgnoring()));
			trace(identify(naming.getIgnoring().getCustomizing()));
		}
	}
	
	public void logContext(MappingContext mappingContext)
	{
		if ( isTraceEnabled() )
		{
			trace(identify(mappingContext));
			trace(identify(mappingContext.getGetTypes()));
			trace(identify(mappingContext.getCustomizing()));
			trace(identify(mappingContext.getNaming()));
			trace(identify(mappingContext.getIgnoring()));
			trace(identify(mappingContext.getIgnoring().getCustomizing()));
			trace(identify(mappingContext.getEmbeddableAttributesMapping()));
		}
	}
	
	public void logContext(ModelAndOutlineProcessor<EJBPlugin> maoProcessor)
	{
		if ( isTraceEnabled() )
		{
			trace(identify(maoProcessor));
			trace(identify(maoProcessor.getModelProcessor()));
			trace(identify(maoProcessor.getOutlineProcessor()));
			ModelProcessor<EJBPlugin> maoModelProcessor = maoProcessor.getModelProcessor();
			if ( maoModelProcessor instanceof DefaultProcessModel )
			{
				DefaultProcessModel processModel = (DefaultProcessModel) maoModelProcessor;
				
				// Process properties
				trace(identify(processModel.getProcessClassInfo()));	
				trace(identify(processModel.getProcessPropertyInfos()));
				trace(identify(processModel.getCreateIdClass()));
				trace(identify(processModel.getCreateDefaultIdPropertyInfos()));
				trace(identify(processModel.getCreateDefaultVersionPropertyInfos()));
				trace(identify(processModel.getGetIdPropertyInfos()));
				trace(identify(processModel.getGetVersionPropertyInfos()));
				trace(identify(processModel.getGetTypes()));
				trace(identify(processModel.getAdaptBuiltinTypeUse()));
				
				// Wrap Attributes
				trace(identify(processModel.getWrapSingleBuiltinAttribute()));
				trace(identify(processModel.getWrapSingleEnumAttribute()));
				trace(identify(processModel.getWrapCollectionBuiltinAttribute()));
				if ( processModel.getWrapCollectionBuiltinAttribute() instanceof WrapCollectionBuiltinNonReference )
				{
					WrapCollectionBuiltinNonReference wcbnr = (WrapCollectionBuiltinNonReference) processModel.getWrapCollectionBuiltinAttribute();
					trace("fallback: {}", identify(wcbnr.getFallback()));
				}
				trace(identify(processModel.getWrapCollectionEnumAttribute()));
				// Wrap Values
				trace(identify(processModel.getWrapSingleBuiltinValue()));
				trace(identify(processModel.getWrapSingleEnumValue()));
				trace(identify(processModel.getWrapCollectionBuiltinValue()));
				if ( processModel.getWrapCollectionBuiltinValue() instanceof WrapCollectionBuiltinNonReference )
				{
					WrapCollectionBuiltinNonReference wcbnr = (WrapCollectionBuiltinNonReference) processModel.getWrapCollectionBuiltinValue();
					trace("fallback: {}", identify(wcbnr.getFallback()));
				}
				trace(identify(processModel.getWrapCollectionEnumValue()));
				// Wrap Elements
				trace(identify(processModel.getWrapSingleBuiltinElement()));
				trace(identify(processModel.getWrapSingleEnumElement()));
				trace(identify(processModel.getWrapSingleHeteroElement()));
				trace(identify(processModel.getWrapCollectionBuiltinElement()));
				if ( processModel.getWrapCollectionBuiltinElement() instanceof WrapCollectionBuiltinNonReference )
				{
					WrapCollectionBuiltinNonReference wcbnr = (WrapCollectionBuiltinNonReference) processModel.getWrapCollectionBuiltinElement();
					trace("fallback: {}", identify(wcbnr.getFallback()));
				}
				trace(identify(processModel.getWrapCollectionEnumElement()));
				trace(identify(processModel.getWrapCollectionHeteroElement()));
				// Wrap ElementReferences
				trace(identify(processModel.getWrapSingleBuiltinElementReference()));
				trace(identify(processModel.getWrapSingleEnumElementReference()));
				trace(identify(processModel.getWrapSingleClassElementReference()));
				trace(identify(processModel.getWrapSingleSubstitutedElementReference()));
				// Wrap References
				trace(identify(processModel.getWrapSingleHeteroReference()));
				trace(identify(processModel.getWrapSingleClassReference()));
				trace(identify(processModel.getWrapSingleWildcardReference()));
				trace(identify(processModel.getWrapCollectionHeteroReference()));
				trace(identify(processModel.getWrapCollectionWildcardReference()));
				
				// Ubiquitous properties
				trace(identify(processModel.getIgnoring()));
				trace(identify(processModel.getCustomizing()));
				
				processModel = null;
			}
			OutlineProcessor<EJBPlugin> maoOutlineProcessor = maoProcessor.getOutlineProcessor();
			if ( maoOutlineProcessor instanceof ClassPersistenceProcessor )
			{
				ClassPersistenceProcessor classPersistenceProcessor = (ClassPersistenceProcessor) maoOutlineProcessor;
				trace(identify(classPersistenceProcessor.getOutlineProcessor()));
				OutlineProcessor<EJBPlugin> cpOutlineProcessor = classPersistenceProcessor.getOutlineProcessor();
				if ( cpOutlineProcessor instanceof AnnotateOutline )
				{
					AnnotateOutline annotateOutline = (AnnotateOutline) cpOutlineProcessor;
					trace(identify(annotateOutline.getIgnoring()));
					trace(identify(annotateOutline.getMapping()));
					trace(identify(annotateOutline.getCreateXAnnotations()));
				}
				trace(identify(classPersistenceProcessor.getNaming()));
				trace(identify(classPersistenceProcessor.getPersistenceFactory()));
				trace(identify(classPersistenceProcessor.getPersistenceMarshaller()));
			}
			else if ( maoOutlineProcessor instanceof MappingFilePersistenceProcessor )
			{
				MappingFilePersistenceProcessor mfPersistenceProcessor = (MappingFilePersistenceProcessor) maoOutlineProcessor;
				trace(identify(mfPersistenceProcessor.getOutlineProcessor()));
				OutlineProcessor<EJBPlugin> mfOutlineProcessor = mfPersistenceProcessor.getOutlineProcessor();
				if ( mfOutlineProcessor instanceof MarshalMappings )
				{
					MarshalMappings marshalMappings = (MarshalMappings) mfOutlineProcessor;
					trace(identify(marshalMappings.getIgnoring()));
					trace(identify(marshalMappings.getMapping()));
				}
			}
		}
	}
	
	/* Identify with prefix */
	private String identify(Object obj)
	{
		return "CDI " + org.jvnet.basicjaxb.util.ClassUtils.identify(obj);
	}
}
