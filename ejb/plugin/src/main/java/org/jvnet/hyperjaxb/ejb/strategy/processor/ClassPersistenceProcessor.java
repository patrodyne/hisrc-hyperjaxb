package org.jvnet.hyperjaxb.ejb.strategy.processor;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.jvnet.basicjaxb.lang.JAXBMergeCollectionsStrategy;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.annotate.StrategyAnnotate;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb.ejb.strategy.outline.EJBOutlineProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;
import org.jvnet.hyperjaxb.persistence.util.PersistenceUtils;

import com.sun.codemodel.JAnnotationClassValue;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.model.CClassRef;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.Persistence;
import ee.jakarta.xml.ns.persistence.Persistence.PersistenceUnit;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.xml.bind.JAXBException;

/**
 * The default strategy to generate annotated Java source code.
 * 
 * Injected: OutlineProcessor&lt;EJBPlugin&gt;, Naming, PersistenceFactory, PersistenceMarshaller
 * Instantiated: ClassPersistenceUnitFactory
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ClassPersistence
public class ClassPersistenceProcessor extends EJBOutlineProcessor
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
	
	@Override
	public Collection<ClassOutline> process(EJBPlugin plugin, Outline outline)
		throws Exception
	{
		Collection<ClassOutline> includedClasses = getOutlineProcessor().process(plugin, outline);
		final String pun = plugin.getPersistenceUnitName();
		final String persistenceUnitName = pun != null ? pun : getNaming().getPersistenceUnitName(plugin.getMapping(), outline);
		final PersistenceUnit persistenceUnit = getPersistenceUnitFactory().createPersistenceUnit(includedClasses);
		addExternalClasses(persistenceUnit, includedClasses);
		final Persistence persistence = createPersistence(plugin, persistenceUnit, persistenceUnitName);
		getPersistenceMarshaller().marshallPersistence(outline.getCodeModel(), persistence);
		return includedClasses;
	}

	/**
	 * Add externally defined classes. The XJC outline may not contain externally
	 * define entities; thus, this method digs into the structure for candidate 
	 * target entities, etc.
	 * 
	 * @param persistenceUnit The persistence unit to be marshalled.
	 * @param includedClasses Current list of included classes.
	 */
	private void addExternalClasses(PersistenceUnit persistenceUnit, Collection<ClassOutline> includedClasses)
	{
		List<String> puClassList = persistenceUnit.getClazz();
		for ( ClassOutline includedClass : includedClasses )
		{
			// Episode Entities
			CClassRef refBaseClass = includedClass.target.getRefBaseClass();
			if ( refBaseClass != null )
			{
				try
				{
					Class<?> baseClass = Class.forName(refBaseClass.fullName());
					addExternalBaseClasses(puClassList, baseClass);
				}
				catch (ClassNotFoundException ex)
				{
				}
			}
			
			// Method References
			for ( JMethod method : includedClass.getImplClass().methods() )
			{
				for ( JAnnotationUse annotationUse : method.annotations() )
				{
					if ( annotationUse.getAnnotationClass() != null )
					{
						String annotationName = annotationUse.getAnnotationClass().binaryName();
						if ( ManyToMany.class.getName().equals(annotationName) ||
							 ManyToOne.class.getName().equals(annotationName) ||
							 OneToMany.class.getName().equals(annotationName) ||
							 OneToOne.class.getName().equals(annotationName)
						)
						{
							for ( Entry<String, JAnnotationValue> entry : annotationUse.getAnnotationMembers().entrySet() )
							{
								if ( "targetEntity".equals(entry.getKey()) )
								{
									if ( entry.getValue() instanceof JAnnotationClassValue )
									{
										JAnnotationClassValue value = (JAnnotationClassValue) entry.getValue();
										String targetEntityClassName = value.type().binaryName();
										if ( !puClassList.contains(targetEntityClassName) )
											puClassList.add(targetEntityClassName);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void addExternalBaseClasses(List<String> puClassList, Class<?> baseClass)
	{
		if ( baseClass != null )
		{
			for (  java.lang.annotation.Annotation ann : baseClass.getAnnotations() )
			{
				if ( Entity.class.getPackage().equals(ann.annotationType().getPackage()) )
				{
					String targetEntityClassName = baseClass.getName();
					if ( !puClassList.contains(targetEntityClassName) )
					{
						puClassList.add(targetEntityClassName);
						addExternalBaseClasses(puClassList, baseClass.getSuperclass());
						break;
					}
				}
			}
		}
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
		
		uniqueSort(targetPersistenceUnit.getMappingFile());
		uniqueSort(targetPersistenceUnit.getClazz());
		
		return persistence;
	}
	
	private void uniqueSort(List<String> list)
	{
		ArrayList<String> tmpList = new ArrayList<>(new TreeSet<>(list));
		list.clear();
		list.addAll(tmpList);
	}
}
