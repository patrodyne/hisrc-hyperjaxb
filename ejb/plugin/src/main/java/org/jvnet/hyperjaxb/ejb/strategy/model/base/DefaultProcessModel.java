package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.CustomizationUtils.findCustomization;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.jpa.Customizations.PERSISTENCE_ELEMENT_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.customizing.Customizing;
import org.jvnet.hyperjaxb.ejb.strategy.customizing.impl.DefaultCustomizing;
import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.model.AdaptTypeUse;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreateDefaultIdPropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreateDefaultVersionPropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreateIdClassProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.GetIdPropertyInfoProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.GetTypes;
import org.jvnet.hyperjaxb.ejb.strategy.model.GetVersionPropertyInfoProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessClassInfo;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessPropertyInfos;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.Model;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

/**
 * The default strategy to process the XJC model.
 * 
 * Injected: TBD Instantiated: TBD
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class DefaultProcessModel implements ProcessModel
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	// Process properties

	@Inject @ModelBase
	private ProcessClassInfo processClassInfo;
	@Override
	public ProcessClassInfo getProcessClassInfo()
	{
		return processClassInfo;
	}
	public void setProcessClassInfo(ProcessClassInfo processClassInfo)
	{
		this.processClassInfo = processClassInfo;
	}

	@Inject @ModelBase
	private ProcessPropertyInfos processPropertyInfos;
	@Override
	public ProcessPropertyInfos getProcessPropertyInfos()
	{
		return processPropertyInfos;
	}
	public void setProcessPropertyInfos(ProcessPropertyInfos processPropertyInfos)
	{
		this.processPropertyInfos = processPropertyInfos;
	}

	@Inject @ModelBase
	private CreateIdClassProcessor createIdClass;
	@Override
	public CreateIdClassProcessor getCreateIdClass()
	{
		return createIdClass;
	}
	public void setCreateIdClass(CreateIdClassProcessor createIdClass)
	{
		this.createIdClass = createIdClass;
	}

	@Inject @ModelBase
	private CreateDefaultIdPropertyInfos createDefaultIdPropertyInfos;
	@Override
	public CreateDefaultIdPropertyInfos getCreateDefaultIdPropertyInfos()
	{
		return createDefaultIdPropertyInfos;
	}
	public void setCreateDefaultIdPropertyInfos(CreateDefaultIdPropertyInfos createDefaultIdPropertyInfos)
	{
		this.createDefaultIdPropertyInfos = createDefaultIdPropertyInfos;
	}

	@Inject @ModelBase
	private CreateDefaultVersionPropertyInfos createDefaultVersionPropertyInfos;
	@Override
	public CreateDefaultVersionPropertyInfos getCreateDefaultVersionPropertyInfos()
	{
		return createDefaultVersionPropertyInfos;
	}
	public void setCreateDefaultVersionPropertyInfos(CreateDefaultVersionPropertyInfos createDefaultVersionPropertyInfos)
	{
		this.createDefaultVersionPropertyInfos = createDefaultVersionPropertyInfos;
	}

	@Inject @ModelBase
	private GetIdPropertyInfoProcessor getIdPropertyInfos;
	@Override
	public GetIdPropertyInfoProcessor getGetIdPropertyInfos()
	{
		return getIdPropertyInfos;
	}
	public void setGetIdPropertyInfos(GetIdPropertyInfoProcessor getIdPropertyInfos)
	{
		this.getIdPropertyInfos = getIdPropertyInfos;
	}

	@Inject @ModelBase
	private GetVersionPropertyInfoProcessor getVersionPropertyInfos;
	@Override
	public GetVersionPropertyInfoProcessor getGetVersionPropertyInfos()
	{
		return getVersionPropertyInfos;
	}
	public void setGetVersionPropertyInfos(GetVersionPropertyInfoProcessor getVersionPropertyInfos)
	{
		this.getVersionPropertyInfos = getVersionPropertyInfos;
	}

	@Inject @ModelBase
	private GetTypes<ProcessModel> getTypes;
	@Override
	public GetTypes<ProcessModel> getGetTypes()
	{
		return getTypes;
	}
	public void setGetTypes(GetTypes<ProcessModel> getTypes)
	{
		this.getTypes = getTypes;
	}
	
	@Inject @ModelBase
	private AdaptTypeUse adaptBuiltinTypeUse;
	@Override
	public AdaptTypeUse getAdaptBuiltinTypeUse()
	{
		return adaptBuiltinTypeUse;
	}
	public void setAdaptBuiltinTypeUse(AdaptTypeUse adaptBuiltinTypeUse)
	{
		this.adaptBuiltinTypeUse = adaptBuiltinTypeUse;
	}
	
	// Wrap Complex Collection

//	private CreatePropertyInfos wrapComplexHeteroCollection;
//	public CreatePropertyInfos getWrapComplexHeteroCollection()
//	{
//		return this.wrapComplexHeteroCollection;
//	}
//	public void setWrapComplexHeteroCollection(CreatePropertyInfos wrapHeteroCollection)
//	{
//		this.wrapComplexHeteroCollection = wrapHeteroCollection;
//	}

	// Wrap Attribute
	
	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Attribute)
	private CreatePropertyInfos wrapSingleBuiltinAttribute;
	@Override
	public CreatePropertyInfos getWrapSingleBuiltinAttribute()
	{
		return wrapSingleBuiltinAttribute;
	}
	public void setWrapSingleBuiltinAttribute(CreatePropertyInfos wrapSingleBuiltinAttribute)
	{
		this.wrapSingleBuiltinAttribute = wrapSingleBuiltinAttribute;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Attribute)
	private CreatePropertyInfos wrapSingleEnumAttribute;
	@Override
	public CreatePropertyInfos getWrapSingleEnumAttribute()
	{
		return wrapSingleEnumAttribute;
	}
	public void setWrapSingleEnumAttribute(CreatePropertyInfos wrapSingleEnumAttribute)
	{
		this.wrapSingleEnumAttribute = wrapSingleEnumAttribute;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Attribute)
	private CreatePropertyInfos wrapCollectionBuiltinAttribute;
	@Override
	public CreatePropertyInfos getWrapCollectionBuiltinAttribute()
	{
		return wrapCollectionBuiltinAttribute;
	}
	public void setWrapCollectionBuiltinAttribute(CreatePropertyInfos wrapCollectionBuiltinAttribute)
	{
		this.wrapCollectionBuiltinAttribute = wrapCollectionBuiltinAttribute;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Attribute)
	private CreatePropertyInfos wrapCollectionEnumAttribute;
	@Override
	public CreatePropertyInfos getWrapCollectionEnumAttribute()
	{
		return wrapCollectionEnumAttribute;
	}
	public void setWrapCollectionEnumAttribute(CreatePropertyInfos wrapCollectionEnumAttribute)
	{
		this.wrapCollectionEnumAttribute = wrapCollectionEnumAttribute;
	}

	// Wrap Value

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Value)
	private CreatePropertyInfos wrapSingleBuiltinValue;
	@Override
	public CreatePropertyInfos getWrapSingleBuiltinValue()
	{
		return wrapSingleBuiltinValue;
	}
	public void setWrapSingleBuiltinValue(CreatePropertyInfos wrapSingleBuiltinValue)
	{
		this.wrapSingleBuiltinValue = wrapSingleBuiltinValue;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Value)
	private CreatePropertyInfos wrapSingleEnumValue;
	@Override
	public CreatePropertyInfos getWrapSingleEnumValue()
	{
		return wrapSingleEnumValue;
	}
	public void setWrapSingleEnumValue(CreatePropertyInfos wrapSingleEnumValue)
	{
		this.wrapSingleEnumValue = wrapSingleEnumValue;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Value)
	private CreatePropertyInfos wrapCollectionBuiltinValue;
	@Override
	public CreatePropertyInfos getWrapCollectionBuiltinValue()
	{
		return wrapCollectionBuiltinValue;
	}
	public void setWrapCollectionBuiltinValue(CreatePropertyInfos wrapCollectionBuiltinValue)
	{
		this.wrapCollectionBuiltinValue = wrapCollectionBuiltinValue;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Value)
	private CreatePropertyInfos wrapCollectionEnumValue;
	@Override
	public CreatePropertyInfos getWrapCollectionEnumValue()
	{
		return wrapCollectionEnumValue;
	}
	public void setWrapCollectionEnumValue(CreatePropertyInfos wrapCollectionEnumValue)
	{
		this.wrapCollectionEnumValue = wrapCollectionEnumValue;
	}

	// Wrap Element

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Element)
	private CreatePropertyInfos wrapSingleBuiltinElement;
	@Override
	public CreatePropertyInfos getWrapSingleBuiltinElement()
	{
		return wrapSingleBuiltinElement;
	}
	public void setWrapSingleBuiltinElement(CreatePropertyInfos wrapSingleBuiltinElement)
	{
		this.wrapSingleBuiltinElement = wrapSingleBuiltinElement;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Element)
	private CreatePropertyInfos wrapSingleEnumElement;
	@Override
	public CreatePropertyInfos getWrapSingleEnumElement()
	{
		return wrapSingleEnumElement;
	}
	public void setWrapSingleEnumElement(CreatePropertyInfos wrapSingleEnumElement)
	{
		this.wrapSingleEnumElement = wrapSingleEnumElement;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Element)
	private CreatePropertyInfos wrapSingleHeteroElement;
	@Override
	public CreatePropertyInfos getWrapSingleHeteroElement()
	{
		return wrapSingleHeteroElement;
	}
	public void setWrapSingleHeteroElement(CreatePropertyInfos wrapSingleHeteroElement)
	{
		this.wrapSingleHeteroElement = wrapSingleHeteroElement;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.Element)
	private CreatePropertyInfos wrapCollectionBuiltinElement;
	@Override
	public CreatePropertyInfos getWrapCollectionBuiltinElement()
	{
		return wrapCollectionBuiltinElement;
	}
	public void setWrapCollectionBuiltinElement(CreatePropertyInfos wrapCollectionBuiltinElement)
	{
		this.wrapCollectionBuiltinElement = wrapCollectionBuiltinElement;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.Element)
	private CreatePropertyInfos wrapCollectionEnumElement;
	@Override
	public CreatePropertyInfos getWrapCollectionEnumElement()
	{
		return wrapCollectionEnumElement;
	}
	public void setWrapCollectionEnumElement(CreatePropertyInfos wrapCollectionEnumElement)
	{
		this.wrapCollectionEnumElement = wrapCollectionEnumElement;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Element)
	private CreatePropertyInfos wrapCollectionHeteroElement;
	@Override
	public CreatePropertyInfos getWrapCollectionHeteroElement()
	{
		return wrapCollectionHeteroElement;
	}
	public void setWrapCollectionHeteroElement(CreatePropertyInfos wrapCollectionHeteroElement)
	{
		this.wrapCollectionHeteroElement = wrapCollectionHeteroElement;
	}
	
	// Wrap ElementReference

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.BuiltIn, schemaType = ModelWrap.SchemaType.ElementReference)
	private CreatePropertyInfos wrapSingleBuiltinElementReference;
	@Override
	public CreatePropertyInfos getWrapSingleBuiltinElementReference()
	{
		return wrapSingleBuiltinElementReference;
	}
	public void setWrapSingleBuiltinElementReference(CreatePropertyInfos wrapSingleBuiltinElementReference)
	{
		this.wrapSingleBuiltinElementReference = wrapSingleBuiltinElementReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Enum, schemaType = ModelWrap.SchemaType.ElementReference)
	private CreatePropertyInfos wrapSingleEnumElementReference;
	@Override
	public CreatePropertyInfos getWrapSingleEnumElementReference()
	{
		return wrapSingleEnumElementReference;
	}
	public void setWrapSingleEnumElementReference(CreatePropertyInfos wrapSingleEnumElementReference)
	{
		this.wrapSingleEnumElementReference = wrapSingleEnumElementReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Class, schemaType = ModelWrap.SchemaType.ElementReference)
	private CreatePropertyInfos wrapSingleClassElementReference;
	@Override
	public CreatePropertyInfos getWrapSingleClassElementReference()
	{
		return wrapSingleClassElementReference;
	}
	public void setWrapSingleClassElementReference(CreatePropertyInfos wrapSingleClassElementReference)
	{
		this.wrapSingleClassElementReference = wrapSingleClassElementReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Substituted, schemaType = ModelWrap.SchemaType.ElementReference)
	private CreatePropertyInfos wrapSingleSubstitutedElementReference;
	@Override
	public CreatePropertyInfos getWrapSingleSubstitutedElementReference()
	{
		return wrapSingleSubstitutedElementReference;
	}
	public void setWrapSingleSubstitutedElementReference(CreatePropertyInfos wrapSingleSubstitutedElementReference)
	{
		this.wrapSingleSubstitutedElementReference = wrapSingleSubstitutedElementReference;
	}

	// Wrap Reference
	
	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Reference)
	private CreatePropertyInfos wrapSingleHeteroReference;
	@Override
	public CreatePropertyInfos getWrapSingleHeteroReference()
	{
		return wrapSingleHeteroReference;
	}
	public void setWrapSingleHeteroReference(CreatePropertyInfos wrapSingleHeteroReference)
	{
		this.wrapSingleHeteroReference = wrapSingleHeteroReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Class, schemaType = ModelWrap.SchemaType.Reference)
	private CreatePropertyInfos wrapSingleClassReference;
	@Override
	public CreatePropertyInfos getWrapSingleClassReference()
	{
		return wrapSingleClassReference;
	}
	public void setWrapSingleClassReference(CreatePropertyInfos wrapSingleClassReference)
	{
		this.wrapSingleClassReference = wrapSingleClassReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Single, javaType = ModelWrap.JavaType.Wildcard, schemaType = ModelWrap.SchemaType.Reference)
	private CreatePropertyInfos wrapSingleWildcardReference;
	@Override
	public CreatePropertyInfos getWrapSingleWildcardReference()
	{
		return wrapSingleWildcardReference;
	}
	public void setWrapSingleWildcardReference(CreatePropertyInfos wrapSingleWildcardReference)
	{
		this.wrapSingleWildcardReference = wrapSingleWildcardReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Hetero, schemaType = ModelWrap.SchemaType.Reference)
	private CreatePropertyInfos wrapCollectionHeteroReference;
	@Override
	public CreatePropertyInfos getWrapCollectionHeteroReference()
	{
		return wrapCollectionHeteroReference;
	}
	public void setWrapCollectionHeteroReference(CreatePropertyInfos wrapCollectionHeteroReference)
	{
		this.wrapCollectionHeteroReference = wrapCollectionHeteroReference;
	}

	@Inject @ModelWrap(plurality = ModelWrap.Plurality.Collection, javaType = ModelWrap.JavaType.Wildcard, schemaType = ModelWrap.SchemaType.Reference)
	private CreatePropertyInfos wrapCollectionWildcardReference;
	@Override
	public CreatePropertyInfos getWrapCollectionWildcardReference()
	{
		return wrapCollectionWildcardReference;
	}
	public void setWrapCollectionWildcardReference(CreatePropertyInfos wrapCollectionWildcardReference)
	{
		this.wrapCollectionWildcardReference = wrapCollectionWildcardReference;
	}

	// Ubiquitous properties

	@Inject
	private Ignoring ignoring;// = new DefaultIgnoring();
	@Override
	public Ignoring getIgnoring()
	{
		return ignoring;
	}
	public void setIgnoring(Ignoring ignoring)
	{
		this.ignoring = ignoring;
	}

	@Inject
	private Customizing customizing;
	@Override
	public Customizing getCustomizing()
	{
		return customizing;
	}
	public void setCustomizing(Customizing customizations)
	{
		this.customizing = customizations;
	}

	// Process
	
	@Override
	public Collection<CClassInfo> process(EJBPlugin context, Model model)
		throws Exception
	{
		setPlugin(context);
		
		if ( getCustomizing() instanceof DefaultCustomizing )
			((DefaultCustomizing) getCustomizing()).setPlugin(context);
		
		// Find and mark persistence customizations as acknowledged.
		CPluginCustomization pec = findCustomization(model, PERSISTENCE_ELEMENT_NAME);
		if ( pec != null )
			getPlugin().debug("{}, DefaultProcessModel: Persistence", toLocation(pec) );
		
		final Collection<CClassInfo> unorderedClassInfos = model.beans().values();
		final CClassInfo[] classInfos = orderClassInfos(unorderedClassInfos).toArray(new CClassInfo[0]);
		final Collection<CClassInfo> includedClasses = new HashSet<CClassInfo>();
		for (final CClassInfo classInfo : classInfos)
		{
			if (!getIgnoring().isClassInfoIgnored(this, classInfo))
			{
				final Collection<CClassInfo> targetClassInfos = getProcessClassInfo().process(this, classInfo);
				if (targetClassInfos != null)
				{
					for (final CClassInfo targetClassInfo : targetClassInfos)
					{
						includedClasses.add(targetClassInfo);
						// model.beans().put(targetClassInfo.getClazz(),
						// targetClassInfo);
						context.getCreatedClasses().add(targetClassInfo);
					}
				}
			}
		}
		return includedClasses;
	}

	private List<CClassInfo> orderClassInfos(Collection<CClassInfo> classInfos)
	{
		final List<CClassInfo> orderedClassInfos = new ArrayList<CClassInfo>(classInfos.size());
		final Set<CClassInfo> addedClassInfos = new HashSet<CClassInfo>();
		for (CClassInfo classInfo : classInfos)
			orderClassInfo(classInfo, orderedClassInfos, addedClassInfos);
		return Collections.unmodifiableList(orderedClassInfos);
	}

	private void orderClassInfo(CClassInfo classInfo, List<CClassInfo> orderedClassInfos,
		Set<CClassInfo> addedClassInfos)
	{
		if (!addedClassInfos.contains(classInfo))
		{
			if (classInfo.getBaseClass() != null)
				orderClassInfo(classInfo.getBaseClass(), orderedClassInfos, addedClassInfos);
			orderedClassInfos.add(classInfo);
			addedClassInfos.add(classInfo);
		}
	}
}
