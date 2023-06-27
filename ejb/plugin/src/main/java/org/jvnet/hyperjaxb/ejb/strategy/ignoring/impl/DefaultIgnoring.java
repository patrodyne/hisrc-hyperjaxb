package org.jvnet.hyperjaxb.ejb.strategy.ignoring.impl;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.jpa.Customizations.IGNORED_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.IGNORED_PACKAGE_ELEMENT_NAME;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.customizing.Customizing;
import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.jpa.IgnoredPackage;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CClassInfoParent.Package;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

/**
 * The default strategy to ignore processing, at various mapping levels.
 * 
 * Injected: Customizing
 * Instantiated: none
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class DefaultIgnoring implements Ignoring
{
	@Inject
	private Customizing customizing;
	@Override
	public Customizing getCustomizing() { return customizing; }
	public void setCustomizing(Customizing customizing) { this.customizing = customizing; }

	private Collection<IgnoredPackage> findCustomizations(Model model, QName name)
	{
		return getCustomizing().findCustomizations(model, name);
	}
	
	@Override
	public boolean isPackageOutlineIgnored(Mapping context, Outline outline, PackageOutline packageOutline)
	{
		for (IgnoredPackage ignoredPackage : findCustomizations(outline.getModel(), IGNORED_PACKAGE_ELEMENT_NAME))
		{
			if (packageOutline._package().name().equals(ignoredPackage.getName()))
				return true;
		}
		return false;
	}

	@Override
	public boolean isPackageInfoIgnored(ProcessModel context, Model model, Package packageInfo)
	{
		for (IgnoredPackage ignoredPackage : findCustomizations(model, IGNORED_PACKAGE_ELEMENT_NAME))
		{
			if (packageInfo.pkg.name().equals(ignoredPackage.getName()))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean isClassOutlineIgnored(Mapping context, ClassOutline classOutline)
	{
		EJBPlugin plugin = context.getPlugin();
		if (isPackageOutlineIgnored(context, classOutline.parent(), classOutline._package()))
		{
			plugin.trace("{}, isClassOutlineIgnored: class={}; ignored since package is ignored.",
				toLocation(classOutline), classOutline.getImplClass().name());
			markAsAcknowledged(classOutline);
			return true;
		}
		else if (CustomizationUtils.containsCustomization(classOutline, IGNORED_ELEMENT_NAME))
		{
			plugin.trace("{}, isClassOutlineIgnored: class={}; ignored per customization.",
				toLocation(classOutline), classOutline.getImplClass().name());
			markAsAcknowledged(classOutline);
			return true;
		}
		else if (classOutline.getSuperClass() != null && isClassOutlineIgnored(context, classOutline.getSuperClass()))
		{
			plugin.trace("{}, isClassOutlineIgnored: class={}; ignored since superclass outline is ignored.",
				toLocation(classOutline), classOutline.getImplClass().name());
			markAsAcknowledged(classOutline);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isFieldOutlineIgnored(Mapping context, FieldOutline fieldOutline)
	{
		EJBPlugin plugin = context.getPlugin();
		if (isClassOutlineIgnored(context, fieldOutline.parent()))
		{
			plugin.trace("{}, isFieldOutlineIgnored: class={}, field={}; ignored since its class outline is ignored.",
				toLocation(fieldOutline), fieldOutline.parent().getImplClass().name(), fieldOutline.getPropertyInfo().getName(false));
			return true;
		}
		else if (CustomizationUtils.containsCustomization(fieldOutline, IGNORED_ELEMENT_NAME))
		{
			plugin.trace("{}, isFieldOutlineIgnored: class={}, field={}; ignored per customization.",
				toLocation(fieldOutline), fieldOutline.parent().getImplClass().name(), fieldOutline.getPropertyInfo().getName(false));
			return true;
		}
		else
		{
			boolean allIgnored = true;
			final Collection<? extends CTypeInfo> types =
				context.getGetTypes().process(context, fieldOutline.getPropertyInfo());
			
			for (final CTypeInfo type : types)
			{
				if (type instanceof CClassInfo)
				{
					final CClassInfo fieldClassInfo = (CClassInfo) type;
					final ClassOutline fieldClassOutline = fieldOutline.parent().parent().getClazz(fieldClassInfo);
					allIgnored = allIgnored && isClassOutlineIgnored(context, fieldClassOutline);
				}
				else
					allIgnored = false;
			}
			
			if (allIgnored)
			{
				plugin.trace("{}, isFieldOutlineIgnored: class={}, field={}; ignored since all types are ignored.",
					toLocation(fieldOutline), fieldOutline.parent().getImplClass().name(), fieldOutline.getPropertyInfo().getName(false));
				markAsAcknowledged(fieldOutline);
			}
			
			return allIgnored;
		}
	}

	public void markAsAcknowledged(FieldOutline fieldOutline)
	{
		Customizations.markAsAcknowledged(fieldOutline.getPropertyInfo());
	}

	public void markAsAcknowledged(ClassOutline classOutline)
	{
		Customizations.markAsAcknowledged(classOutline.target);
		for (final FieldOutline fieldOutline : classOutline.getDeclaredFields())
			Customizations.markAsAcknowledged(fieldOutline.getPropertyInfo());
	}


	public void markAsAcknowledged(CClassInfo classInfo)
	{
		Customizations.markAsAcknowledged(classInfo);
	}
	
	@Override
	public boolean isClassInfoIgnored(ProcessModel context, CClassInfo classInfo)
	{
		EJBPlugin plugin = context.getPlugin();
		if (isPackageInfoIgnored(context, classInfo.model, getPackageInfo(classInfo)))
		{
			plugin.trace("{}, isClassInfoIgnored: class={}; ignored since package is ignored.",
				toLocation(classInfo), classInfo.shortName);
			markAsAcknowledged(classInfo);
			return true;
		}
		else if (CustomizationUtils.containsCustomization(classInfo, IGNORED_ELEMENT_NAME))
		{
			plugin.trace("{}, isClassInfoIgnored: class={}; ignored per customization.",
				toLocation(classInfo), classInfo.shortName);
			markAsAcknowledged(classInfo);
			return true;
		}
		else if (classInfo.getBaseClass() != null && isClassInfoIgnored(context, classInfo.getBaseClass()))
		{
			plugin.trace("{}, isClassInfoIgnored: class={}; ignored since base class info is ignored.",
				toLocation(classInfo), classInfo.shortName);
			markAsAcknowledged(classInfo);
			return true;
		}
		else
			return false;
	}
	
	public void markAsAcknowledged(CPropertyInfo propertyInfo)
	{
		Customizations.markAsAcknowledged(propertyInfo);
	}
	
	@Override
	public boolean isPropertyInfoIgnored(ProcessModel context, CPropertyInfo propertyInfo)
	{
		EJBPlugin plugin = context.getPlugin();
		if (propertyInfo.parent() instanceof CClassInfo	&& isClassInfoIgnored(context, (CClassInfo) propertyInfo.parent()))
		{
			plugin.trace("{}, isPropertyInfoIgnored: property={}; ignored since its class info is ignored.",
				toLocation(propertyInfo), propertyInfo.getName(false));
			return true;
		}
		else if (CustomizationUtils.containsCustomization(propertyInfo, IGNORED_ELEMENT_NAME))
		{
			plugin.trace("{}, isPropertyInfoIgnored: property={}; ignored per customization.",
				toLocation(propertyInfo), propertyInfo.getName(false));
			return true;
		}
		else
		{
			boolean allIgnored = true;
			final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
			for (final CTypeInfo type : types)
			{
				if (type instanceof CClassInfo)
				{
					final CClassInfo fieldClassInfo = (CClassInfo) type;
					allIgnored = allIgnored && isClassInfoIgnored(context, fieldClassInfo);
				}
				else
					allIgnored = false;
			}
			if (allIgnored)
			{
				plugin.trace("{}, isPropertyInfoIgnored: property={}; ignored since all types are ignored.",
					toLocation(propertyInfo), propertyInfo.getName(false));
				markAsAcknowledged(propertyInfo);
			}
			return allIgnored;
		}
	}

	private CClassInfoParent.Package getPackageInfo(CClassInfoParent parent)
	{
		if (parent instanceof CClassInfoParent.Package)
			return (Package) parent;
		else if (parent instanceof CClassInfo)
			return getPackageInfo(((CClassInfo) parent).parent());
		else if (parent instanceof CElementInfo)
			return getPackageInfo(((CElementInfo) parent).parent);
		else
			throw new AssertionError("Unexpexted class info parent [" + parent + "].");
	}
}
