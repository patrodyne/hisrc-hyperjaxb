package org.jvnet.hyperjaxb.ejb.plugin;

import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.jvnet.basicjaxb.util.ClassUtils.identify;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.xjc.Options;
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
	protected Logger logger = LoggerFactory.getLogger(getClass());

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
	protected void beforeRun(Outline outline, Options options) throws Exception
	{
		super.beforeRun(outline, options);
		configureWeldContext();
	}
	
	@Override
	protected void afterRun(Outline outline, Options options) throws Exception
	{
		super.afterRun(outline, options);
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
				logger.warn("Beans properties not loaded: "+getBeansPropertiesLocator(), ex);
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
			logger.error("Cannot create JAXB context", ex);
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
				logger.error("Cannot create marshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			logger.error("Cannot create JAXB marshaller", ex);
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
				logger.error("Cannot create unmarshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			logger.error("Cannot create JAXB unmarshaller", ex);
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
		logger.debug("CDI Beans: \n" + sb);
	}
	
	public void logContext(Naming naming)
	{
		if ( logger.isDebugEnabled() )
		{
			logger.debug(identify(naming));
			logger.debug(identify(naming.getReservedNames()));
			logger.debug(identify(naming.getIgnoring()));
			logger.debug(identify(naming.getIgnoring().getCustomizing()));
		}
	}
	
	public void logContext(MappingContext mappingContext)
	{
		if ( logger.isDebugEnabled() )
		{
			logger.debug(identify(mappingContext));
			logger.debug(identify(mappingContext.getGetTypes()));
			logger.debug(identify(mappingContext.getCustomizing()));
			logger.debug(identify(mappingContext.getNaming()));
			logger.debug(identify(mappingContext.getIgnoring()));
			logger.debug(identify(mappingContext.getIgnoring().getCustomizing()));
			logger.debug(identify(mappingContext.getEmbeddableAttributesMapping()));
		}
	}
	
	public void logContext(ModelAndOutlineProcessor<EJBPlugin> maoProcessor)
	{
		if ( logger.isDebugEnabled() )
		{
			logger.debug(identify(maoProcessor));
			logger.debug(identify(maoProcessor.getModelProcessor()));
			logger.debug(identify(maoProcessor.getOutlineProcessor()));
			ModelProcessor<EJBPlugin> maoModelProcessor = maoProcessor.getModelProcessor();
			if ( maoModelProcessor instanceof DefaultProcessModel )
			{
				DefaultProcessModel processModel = (DefaultProcessModel) maoModelProcessor;
				
				// Process properties
				logger.debug(identify(processModel.getProcessClassInfo()));	
				logger.debug(identify(processModel.getProcessPropertyInfos()));
				logger.debug(identify(processModel.getCreateIdClass()));
				logger.debug(identify(processModel.getCreateDefaultIdPropertyInfos()));
				logger.debug(identify(processModel.getCreateDefaultVersionPropertyInfos()));
				logger.debug(identify(processModel.getGetIdPropertyInfos()));
				logger.debug(identify(processModel.getGetVersionPropertyInfos()));
				logger.debug(identify(processModel.getGetTypes()));
				logger.debug(identify(processModel.getAdaptBuiltinTypeUse()));
				
				// Wrap Attributes
				logger.debug(identify(processModel.getWrapSingleBuiltinAttribute()));
				logger.debug(identify(processModel.getWrapSingleEnumAttribute()));
				logger.debug(identify(processModel.getWrapCollectionBuiltinAttribute()));
				if ( processModel.getWrapCollectionBuiltinAttribute() instanceof WrapCollectionBuiltinNonReference )
				{
					WrapCollectionBuiltinNonReference wcbnr = (WrapCollectionBuiltinNonReference) processModel.getWrapCollectionBuiltinAttribute();
					logger.debug("fallback: " + identify(wcbnr.getFallback()));
				}
				logger.debug(identify(processModel.getWrapCollectionEnumAttribute()));
				// Wrap Values
				logger.debug(identify(processModel.getWrapSingleBuiltinValue()));
				logger.debug(identify(processModel.getWrapSingleEnumValue()));
				logger.debug(identify(processModel.getWrapCollectionBuiltinValue()));
				if ( processModel.getWrapCollectionBuiltinValue() instanceof WrapCollectionBuiltinNonReference )
				{
					WrapCollectionBuiltinNonReference wcbnr = (WrapCollectionBuiltinNonReference) processModel.getWrapCollectionBuiltinValue();
					logger.debug("fallback: " + identify(wcbnr.getFallback()));
				}
				logger.debug(identify(processModel.getWrapCollectionEnumValue()));
				// Wrap Elements
				logger.debug(identify(processModel.getWrapSingleBuiltinElement()));
				logger.debug(identify(processModel.getWrapSingleEnumElement()));
				logger.debug(identify(processModel.getWrapSingleHeteroElement()));
				logger.debug(identify(processModel.getWrapCollectionBuiltinElement()));
				if ( processModel.getWrapCollectionBuiltinElement() instanceof WrapCollectionBuiltinNonReference )
				{
					WrapCollectionBuiltinNonReference wcbnr = (WrapCollectionBuiltinNonReference) processModel.getWrapCollectionBuiltinElement();
					logger.debug("fallback: " + identify(wcbnr.getFallback()));
				}
				logger.debug(identify(processModel.getWrapCollectionEnumElement()));
				logger.debug(identify(processModel.getWrapCollectionHeteroElement()));
				// Wrap ElementReferences
				logger.debug(identify(processModel.getWrapSingleBuiltinElementReference()));
				logger.debug(identify(processModel.getWrapSingleEnumElementReference()));
				logger.debug(identify(processModel.getWrapSingleClassElementReference()));
				logger.debug(identify(processModel.getWrapSingleSubstitutedElementReference()));
				// Wrap References
				logger.debug(identify(processModel.getWrapSingleHeteroReference()));
				logger.debug(identify(processModel.getWrapSingleClassReference()));
				logger.debug(identify(processModel.getWrapSingleWildcardReference()));
				logger.debug(identify(processModel.getWrapCollectionHeteroReference()));
				logger.debug(identify(processModel.getWrapCollectionWildcardReference()));
				
				// Ubiquitous properties
				logger.debug(identify(processModel.getIgnoring()));
				logger.debug(identify(processModel.getCustomizing()));
				
				processModel = null;
			}
			OutlineProcessor<EJBPlugin> maoOutlineProcessor = maoProcessor.getOutlineProcessor();
			if ( maoOutlineProcessor instanceof ClassPersistenceProcessor )
			{
				ClassPersistenceProcessor classPersistenceProcessor = (ClassPersistenceProcessor) maoOutlineProcessor;
				logger.debug(identify(classPersistenceProcessor.getOutlineProcessor()));
				OutlineProcessor<EJBPlugin> cpOutlineProcessor = classPersistenceProcessor.getOutlineProcessor();
				if ( cpOutlineProcessor instanceof AnnotateOutline )
				{
					AnnotateOutline annotateOutline = (AnnotateOutline) cpOutlineProcessor;
					logger.debug(identify(annotateOutline.getIgnoring()));
					logger.debug(identify(annotateOutline.getMapping()));
					logger.debug(identify(annotateOutline.getCreateXAnnotations()));
				}
				logger.debug(identify(classPersistenceProcessor.getNaming()));
				logger.debug(identify(classPersistenceProcessor.getPersistenceFactory()));
				logger.debug(identify(classPersistenceProcessor.getPersistenceMarshaller()));
			}
			else if ( maoOutlineProcessor instanceof MappingFilePersistenceProcessor )
			{
				MappingFilePersistenceProcessor mfPersistenceProcessor = (MappingFilePersistenceProcessor) maoOutlineProcessor;
				logger.debug(identify(mfPersistenceProcessor.getOutlineProcessor()));
				OutlineProcessor<EJBPlugin> mfOutlineProcessor = mfPersistenceProcessor.getOutlineProcessor();
				if ( mfOutlineProcessor instanceof MarshalMappings )
				{
					MarshalMappings marshalMappings = (MarshalMappings) mfOutlineProcessor;
					logger.debug(identify(marshalMappings.getIgnoring()));
					logger.debug(identify(marshalMappings.getMapping()));
				}
			}
		}
	}
}