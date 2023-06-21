package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static java.lang.String.format;
import static org.jvnet.hyperjaxb.ejb.Constants.TODO_LOG_LEVEL;
import static org.jvnet.hyperjaxb.locator.util.LocatorUtils.getLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessPropertyInfos;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.xjc.model.CClassifier;
import org.jvnet.hyperjaxb.xjc.model.CClassifyingVisitor;

import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassRef;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class DefaultProcessPropertyInfos implements ProcessPropertyInfos
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CClassInfo classInfo)
	{
		setPlugin(context.getPlugin());
		
		final Collection<CPropertyInfo> newPropertyInfos = new LinkedList<CPropertyInfo>();
		
		// In case this is a root entity, create default id properties
		if (CustomizationUtils.containsCustomization(classInfo, Customizations.GENERATED_ID_ELEMENT_NAME))
		{
			final Collection<CPropertyInfo> idPropertyInfos =
				context.getGetIdPropertyInfos().process(context, classInfo);
			
			if (!idPropertyInfos.isEmpty())
			{
				todo(format("Class info [%s] is annotated with hj:generated-id customization, "
					+ "but it already contains id properties. The customization will be ignored.",
					classInfo.getName()));
			}
			else
				newPropertyInfos.addAll(createDefaultIdPropertyInfos(context, classInfo));
		}
		else if (isRootClass(context, classInfo))
		{
			final Collection<CPropertyInfo> idPropertyInfos =
				context.getGetIdPropertyInfos().process(context, classInfo);
			
			// If no id properties found, create default.
			if (idPropertyInfos.isEmpty())
				newPropertyInfos.addAll(createDefaultIdPropertyInfos(context, classInfo));
		}
		
		if (CustomizationUtils.containsCustomization(classInfo, Customizations.GENERATED_VERSION_ELEMENT_NAME))
		{
			final Collection<CPropertyInfo> versionPropertyInfos =
				context.getGetVersionPropertyInfos().process(context, classInfo);
			
			if (!versionPropertyInfos.isEmpty())
			{
				todo(format("Class info [%s] is annotated with hj:generated-version customization, "
						+ "but it already contains version properties. The customization will be ignored.",
						classInfo.getName()));
			}
			else
				newPropertyInfos.addAll(createDefaultVersionPropertyInfos(context, classInfo));
		}
		else if (isRootClass(context, classInfo))
		{
			final Collection<CPropertyInfo> versionPropertyInfos =
				context.getGetVersionPropertyInfos().process(context, classInfo);
			// If no version properties found, create default.
			if (versionPropertyInfos.isEmpty())
				newPropertyInfos.addAll(createDefaultVersionPropertyInfos(context, classInfo));
		}
		
		final CPropertyInfo[] propertyInfos = classInfo.getProperties()
			.toArray(new CPropertyInfo[classInfo.getProperties().size()]);
		for (final CPropertyInfo propertyInfo : propertyInfos)
		{
			if (!context.getIgnoring().isPropertyInfoIgnored(context, propertyInfo))
				newPropertyInfos.addAll(process(context, propertyInfo));
		}
		
		if (classInfo.declaresAttributeWildcard())
			todo("Class ["	+ classInfo.getName() + "] declares an attribute wildcard. This is currently not supported. See issue #46.");
		
		// Add properties if they're not yet there
		for (final CPropertyInfo newPropertyInfo : newPropertyInfos)
		{
			if (newPropertyInfo.parent() == null)
			{
				classInfo.addProperty(newPropertyInfo);
				getPlugin().debug("{}, DefaultProcessPropertyInfos: class={}, property={}",
					getLocation(newPropertyInfo, classInfo), classInfo.shortName, newPropertyInfo.getName(false));
			}
		}

		return newPropertyInfos;
	}

	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final CClassifyingVisitor<Collection<CPropertyInfo>> classifyingVisitor =
			new CClassifyingVisitor<Collection<CPropertyInfo>>(context, new PropertyClassifier(context));
		return propertyInfo.accept(classifyingVisitor);
	}

	public boolean isRootClass(ProcessModel context, CClassInfo classInfo)
	{
		final boolean notMappedSuperclassAndNotEmbeddable = 
			!CustomizationUtils.containsCustomization(classInfo, Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME)
			&& !CustomizationUtils.containsCustomization(classInfo,	Customizations.EMBEDDABLE_ELEMENT_NAME);
		
		if (classInfo.getRefBaseClass() != null)
		{
			return notMappedSuperclassAndNotEmbeddable
					&& !isSelfOrAncestorRootClass(context, classInfo.getRefBaseClass());
		}
		else if (classInfo.getBaseClass() != null)
			return notMappedSuperclassAndNotEmbeddable && !isSelfOrAncestorRootClass(context, classInfo.getBaseClass());
		else
			return notMappedSuperclassAndNotEmbeddable;
	}

	public boolean isSelfOrAncestorRootClass(ProcessModel context, CClassInfo classInfo)
	{
		if (isRootClass(context, classInfo))
			return true;
		else if (classInfo.getRefBaseClass() != null)
			return isSelfOrAncestorRootClass(context, classInfo.getRefBaseClass());
		else if (classInfo.getBaseClass() != null)
			return isSelfOrAncestorRootClass(context, classInfo.getBaseClass());
		else
		{
			return !CustomizationUtils.containsCustomization(classInfo, Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME)
				&& !CustomizationUtils.containsCustomization(classInfo, Customizations.EMBEDDABLE_ELEMENT_NAME);
		}
	}

	public boolean isSelfOrAncestorRootClass(ProcessModel context, CClassRef classRef)
	{
		final String className = classRef.fullName();
		try
		{
			final Class<?> referencedClass = Class.forName(className);
			return isSelfOrAncestorRootClass(referencedClass);
		}
		catch (ClassNotFoundException cnfex)
		{
			getPlugin().warn("Referenced class [{}] could not be found, this may lead to incorrect generation of the identifier fields.", className);
			return true;
		}
	}

	public boolean isRootClass(Class<?> theClass)
	{
		final boolean notMappedSuperclassAndNotEmbeddable = theClass.getAnnotation(MappedSuperclass.class) == null
															&& theClass.getAnnotation(Embeddable.class) == null;
		if (theClass.getSuperclass() != null)
			return notMappedSuperclassAndNotEmbeddable && !isSelfOrAncestorRootClass(theClass.getSuperclass());
		else
			return notMappedSuperclassAndNotEmbeddable;
	}

	public boolean isSelfOrAncestorRootClass(Class<?> theClass)
	{
		if (isRootClass(theClass))
		{
			return true;
		}
		else if (theClass.getSuperclass() != null)
		{
			return isSelfOrAncestorRootClass(theClass.getSuperclass());
		}
		else
		{
			return theClass.getAnnotation(MappedSuperclass.class) == null
				&& theClass.getAnnotation(Embeddable.class) == null;
		}
	}

	public Collection<CPropertyInfo> createDefaultIdPropertyInfos(ProcessModel context, CClassInfo classInfo)
	{
		return context.getCreateDefaultIdPropertyInfos().process(context, classInfo);
	}

	public Collection<CPropertyInfo> createDefaultVersionPropertyInfos(ProcessModel context, CClassInfo classInfo)
	{
		return context.getCreateDefaultVersionPropertyInfos().process(context, classInfo);
	}

	private class PropertyClassifier implements CClassifier<Collection<CPropertyInfo>>
	{
		private ProcessModel context;
		public PropertyClassifier(ProcessModel context)
		{
			this.context = context;
		}
		
		// / Attribute
		// Single

		@Override
		public Collection<CPropertyInfo> onSingleBuiltinAttribute(CAttributePropertyInfo attributePropertyInfo)
		{
			return context.getWrapSingleBuiltinAttribute().process(context, attributePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleEnumAttribute(CAttributePropertyInfo attributePropertyInfo)
		{
			return context.getWrapSingleEnumAttribute().process(context, attributePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleOtherAttribute(CAttributePropertyInfo attributePropertyInfo)
		{
			todo("[" + attributePropertyInfo.getName(true) + "] is a single other attribute. See issue #56.");
			return Collections.emptyList();
		}
		
		// Collection

		@Override
		public Collection<CPropertyInfo> onCollectionBuiltinAttribute(CAttributePropertyInfo attributePropertyInfo)
		{
			return context.getWrapCollectionBuiltinAttribute().process(context, attributePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionEnumAttribute(CAttributePropertyInfo attributePropertyInfo)
		{
			return context.getWrapCollectionEnumAttribute().process(context, attributePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionOtherAttribute(CAttributePropertyInfo attributePropertyInfo)
		{
			todo("[" + attributePropertyInfo.getName(true) + "] is a collection other attribute. See issue #59.");
			return Collections.emptyList();
		}
		
		// / Value
		// Single

		@Override
		public Collection<CPropertyInfo> onSingleBuiltinValue(CValuePropertyInfo valuePropertyInfo)
		{
			return context.getWrapSingleBuiltinValue().process(context, valuePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleEnumValue(CValuePropertyInfo valuePropertyInfo)
		{
			return context.getWrapSingleEnumValue().process(context, valuePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleOtherValue(CValuePropertyInfo valuePropertyInfo)
		{
			todo("[" + valuePropertyInfo.getName(true) + "] is a single other value. See issue #60.");
			return Collections.emptyList();
		}
		
		// Collection

		@Override
		public Collection<CPropertyInfo> onCollectionBuiltinValue(CValuePropertyInfo valuePropertyInfo)
		{
			return context.getWrapCollectionBuiltinValue().process(context, valuePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionEnumValue(CValuePropertyInfo valuePropertyInfo)
		{
			return context.getWrapCollectionEnumValue().process(context, valuePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionOtherValue(CValuePropertyInfo valuePropertyInfo)
		{
			todo("[" + valuePropertyInfo.getName(true) + "] is a collection other value. See issue #63.");
			return Collections.emptyList();
		}
		
		// Element
		// Single

		@Override
		public Collection<CPropertyInfo> onSingleBuiltinElement(CElementPropertyInfo elementPropertyInfo)
		{
			return context.getWrapSingleBuiltinElement().process(context, elementPropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleEnumElement(CElementPropertyInfo elementPropertyInfo)
		{
			return context.getWrapSingleEnumElement().process(context, elementPropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleArrayElement(CElementPropertyInfo elementPropertyInfo)
		{
			throw new UnsupportedOperationException("Arrays are not supported.");
		}

		@Override
		public Collection<CPropertyInfo> onSingleClassElement(CElementPropertyInfo elementPropertyInfo)
		{
			return Collections.emptyList();
		}

		@Override
		public Collection<CPropertyInfo> onSingleHeteroElement(CElementPropertyInfo elementPropertyInfo)
		{
			return context.getWrapSingleHeteroElement().process(context, elementPropertyInfo);
		}
		
		// Collection

		@Override
		public Collection<CPropertyInfo> onCollectionBuiltinElement(CElementPropertyInfo elementPropertyInfo)
		{
			return context.getWrapCollectionBuiltinElement().process(context, elementPropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionEnumElement(CElementPropertyInfo elementPropertyInfo)
		{
			return context.getWrapCollectionEnumElement().process(context, elementPropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionArrayElement(CElementPropertyInfo elementPropertyInfo)
		{
			throw new UnsupportedOperationException("Arrays are not supported.");
		}

		@Override
		public Collection<CPropertyInfo> onCollectionClassElement(CElementPropertyInfo elementPropertyInfo)
		{
			return Collections.emptyList();
		}

		@Override
		public Collection<CPropertyInfo> onCollectionHeteroElement(CElementPropertyInfo elementPropertyInfo)
		{
			return context.getWrapCollectionHeteroElement().process(context, elementPropertyInfo);
		}
		
		// / Reference
		// Single

		@Override
		public Collection<CPropertyInfo> onSingleBuiltinElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapSingleBuiltinElementReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleEnumElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapSingleEnumElementReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleArrayElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			throw new UnsupportedOperationException("Arrays are not supported.");
		}
		
		// public Collection<CPropertyInfo> onSingleElementReference(
		// CReferencePropertyInfo referencePropertyInfo) {
		// todo("[" + referencePropertyInfo.getName(true)
		// + "] is a single element reference. See issue #65.");
		// return Collections.emptyList();
		// }

		@Override
		public Collection<CPropertyInfo> onSingleClassReference(CReferencePropertyInfo referencePropertyInfo)
		{
			// todo("[" + referencePropertyInfo.getName(true)
			// + "] is a single class reference. See issue #66.");
			// return Collections.emptyList();
			return context.getWrapSingleClassReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleWildcardReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapSingleWildcardReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleHeteroReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapSingleHeteroReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleClassElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapSingleClassElementReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onSingleSubstitutedElementReference(
			CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapSingleSubstitutedElementReference().process(context, referencePropertyInfo);
		}
		
		// Collection

		@Override
		public Collection<CPropertyInfo> onCollectionBuiltinElementReference(
			CReferencePropertyInfo referencePropertyInfo)
		{
			todo("["	+ referencePropertyInfo.getName(true)
					+ "] is a collection builtin element reference. See issue #67 (http://java.net/jira/browse/HYPERJAXB3-67).");
			return Collections.emptyList();
		}

		@Override
		public Collection<CPropertyInfo> onCollectionEnumElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			todo("["	+ referencePropertyInfo.getName(true)
					+ "] is a collection enum element reference. See issue #68 (http://java.net/jira/browse/HYPERJAXB3-68).");
			return Collections.emptyList();
		}

		@Override
		public Collection<CPropertyInfo> onCollectionArrayElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			throw new UnsupportedOperationException("Arrays are not supported.");
		}
		
		// public Collection<CPropertyInfo> onCollectionElementReference(
		// CReferencePropertyInfo referencePropertyInfo) {
		// todo("[" + referencePropertyInfo.getName(true)
		// + "] is a collection element reference. See issue #69.");
		// return Collections.emptyList();
		// }

		@Override
		public Collection<CPropertyInfo> onCollectionClassReference(CReferencePropertyInfo referencePropertyInfo)
		{
			todo("[" + referencePropertyInfo.getName(true) + "] is a collection class reference. See issue #70.");
			return Collections.emptyList();
		}

		@Override
		public Collection<CPropertyInfo> onCollectionWildcardReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapCollectionWildcardReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionClassElementReference(CReferencePropertyInfo referencePropertyInfo)
		{
			todo("["	+ referencePropertyInfo.getName(true)
					+ "] is a collection class element reference. See issue #71.");
			return Collections.emptyList();
		}

		@Override
		public Collection<CPropertyInfo> onCollectionSubstitutedElementReference(
			CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapCollectionHeteroReference().process(context, referencePropertyInfo);
		}

		@Override
		public Collection<CPropertyInfo> onCollectionHeteroReference(CReferencePropertyInfo referencePropertyInfo)
		{
			return context.getWrapCollectionHeteroReference().process(context, referencePropertyInfo);
		}
	};

	private void todo(String comment)
	{
		String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
		String level = System.getProperty(TODO_LOG_LEVEL);
		if ("DEBUG".equalsIgnoreCase(level))
			getPlugin().debug(msg);
		else if ("INFO".equalsIgnoreCase(level))
			getPlugin().info(msg);
		else if ("WARN".equalsIgnoreCase(level))
			getPlugin().warn(msg);
		else if ("ERROR".equalsIgnoreCase(level))
			getPlugin().error(msg);
		else
			getPlugin().error(msg);
	}
}
