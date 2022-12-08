package org.jvnet.hyperjaxb.ejb.strategy.processor;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.jvnet.basicjaxb.lang.JAXBMergeCollectionsStrategy;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.annotate.StrategyAnnotate;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;
import org.jvnet.hyperjaxb.persistence.util.PersistenceUtils;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.Persistence;
import ee.jakarta.xml.ns.persistence.Persistence.PersistenceUnit;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBException;

/**
 * The default strategy to generate annotated Java source code.
 * 
 * Injected: OutlineProcessor<EJBPlugin>, Naming, PersistenceFactory, PersistenceMarshaller
 * Instantiated: ClassPersistenceUnitFactory
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ClassPersistence
public class ClassPersistenceProcessor implements OutlineProcessor<EJBPlugin>
{
	@Inject @StrategyAnnotate
	private OutlineProcessor<EJBPlugin> outlineProcessor;
	public OutlineProcessor<EJBPlugin> getOutlineProcessor()
	{
		return outlineProcessor;
	}
	public void setOutlineProcessor(OutlineProcessor<EJBPlugin> outlineProcessor)
	{
		this.outlineProcessor = outlineProcessor;
	}
	
	@Inject
	private Naming naming;
	public Naming getNaming()
	{
		return naming;
	}
	public void setNaming(Naming naming)
	{
		this.naming = naming;
	}

	@Inject
	private PersistenceFactory persistenceFactory;
	public PersistenceFactory getPersistenceFactory()
	{
		return persistenceFactory;
	}
	public void setPersistenceFactory(PersistenceFactory persistenceFactory)
	{
		this.persistenceFactory = persistenceFactory;
	}

	@Inject
	private PersistenceMarshaller persistenceMarshaller;
	public PersistenceMarshaller getPersistenceMarshaller()
	{
		return persistenceMarshaller;
	}
	public void setPersistenceMarshaller(PersistenceMarshaller persistenceMarshaller)
	{
		this.persistenceMarshaller = persistenceMarshaller;
	}

	private PersistenceUnitFactory persistenceUnitFactory = new ClassPersistenceUnitFactory();
	public PersistenceUnitFactory getPersistenceUnitFactory()
	{
		return persistenceUnitFactory;
	}
	public void setPersistenceUnitFactory(PersistenceUnitFactory persistenceUnitFactory)
	{
		this.persistenceUnitFactory = persistenceUnitFactory;
	}
	
	public Collection<ClassOutline> process(EJBPlugin plugin, Outline outline, Options options)
		throws Exception
	{
		Collection<ClassOutline> includedClasses = getOutlineProcessor().process(plugin, outline, options);
		final String pun = plugin.getPersistenceUnitName();
		final String persistenceUnitName = pun != null ? pun : getNaming().getPersistenceUnitName(plugin.getMapping(), outline);
		final PersistenceUnit persistenceUnit = getPersistenceUnitFactory().createPersistenceUnit(includedClasses);
		final Persistence persistence = createPersistence(plugin, persistenceUnit, persistenceUnitName);
		getPersistenceMarshaller().marshallPersistence(outline.getCodeModel(), persistence);
		return includedClasses;
	}

	protected Persistence createPersistence(EJBPlugin plugin, PersistenceUnit persistenceUnit, String persistenceUnitName)
		throws JAXBException
	{
		// plugin.get
		final Persistence persistence;
		final PersistenceUnit targetPersistenceUnit;
		final File persistenceXml = plugin.getPersistenceXml();
		if (persistenceXml != null)
		{
			try
			{
				persistence = (Persistence) PersistenceUtils.CONTEXT.createUnmarshaller().unmarshal(persistenceXml);
				PersistenceUnit foundPersistenceUnit = null;
				for (final PersistenceUnit unit : persistence.getPersistenceUnit())
				{
					if (persistenceUnitName != null && persistenceUnitName.equals(unit.getName()))
						foundPersistenceUnit = unit;
					else if ("##generated".equals(unit.getName()))
					{
						foundPersistenceUnit = unit;
						// foundPersistenceUnit.setName(persistenceUnitName);
					}
				}
				if (foundPersistenceUnit != null)
					targetPersistenceUnit = foundPersistenceUnit;
				else
				{
					targetPersistenceUnit = new PersistenceUnit();
					persistence.getPersistenceUnit().add(targetPersistenceUnit);
					// targetPersistenceUnit.setName(persistenceUnitName);
				}
			}
			catch (Exception ex)
			{
				throw new JAXBException("Persistence XML file [" + persistenceXml + "] could not be parsed.", ex);
			}
		}
		else
		{
			persistence = getPersistenceFactory().createPersistence();
			targetPersistenceUnit = new PersistenceUnit();
			persistence.getPersistenceUnit().add(targetPersistenceUnit);
		}
		
		// targetPersistenceUnit.mergeFrom(persistenceUnit, targetPersistenceUnit);
		targetPersistenceUnit.mergeFrom(null, null, persistenceUnit, targetPersistenceUnit,
			JAXBMergeCollectionsStrategy.getInstance());
		// persistenceUnit.copyTo(targetPersistenceUnit);
		targetPersistenceUnit.setName(persistenceUnitName);
		Collections.sort(targetPersistenceUnit.getMappingFile());
		Collections.sort(targetPersistenceUnit.getClazz());
		
		return persistence;
	}
}
