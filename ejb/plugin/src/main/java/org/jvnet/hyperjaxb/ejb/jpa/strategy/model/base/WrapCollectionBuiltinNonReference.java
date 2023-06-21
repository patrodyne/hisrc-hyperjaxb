package org.jvnet.hyperjaxb.ejb.jpa.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.hyperjaxb.locator.util.LocatorUtils.getLocation;

import java.util.Collection;

import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.AbstractWrapBuiltin;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.AdaptCollectionBuiltinNonReference;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.CreateNoPropertyInfos;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

/**
 * Extend AbstractWrapBuiltin to process CreatePropertyInfos with a dependent fallback.
 */
@Dependent
@Alternative
@Priority(APPLICATION + 1)
public class WrapCollectionBuiltinNonReference extends AbstractWrapBuiltin
{
	private CreatePropertyInfos fallback;
	public CreatePropertyInfos getFallback()
	{
		return fallback;
	}
	public void setFallback(CreatePropertyInfos fallback)
	{
		this.fallback = fallback;
	}

	@Override
	public CBuiltinLeafInfo getTypeUse(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return (CBuiltinLeafInfo) context.getGetTypes().process(context, propertyInfo).iterator().next();
	}

	@Override
	public CreatePropertyInfos getCreatePropertyInfos(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final CBuiltinLeafInfo originalTypeUse = getTypeUse(context, propertyInfo);
		final TypeUse adaptingTypeUse = context.getAdaptBuiltinTypeUse().process(context, propertyInfo);
		if (adaptingTypeUse == originalTypeUse || adaptingTypeUse.getAdapterUse() == null)
		{
			if ( getPlugin().isTraceEnabled() && propertyInfo.parent() instanceof CClassInfo )
			{
				CClassInfo parent = (CClassInfo) propertyInfo.parent();
				getPlugin().trace("{}, {}: class={}, property={}; no adaptation required",
					getLocation(propertyInfo), getClass().getSimpleName(),
					parent.shortName, propertyInfo.getName(false));
			}
			return CreateNoPropertyInfos.INSTANCE;
		}
		else
		{
			return new AdaptCollectionBuiltinNonReference(adaptingTypeUse);
		}
	}

	@Override
	protected Collection<CPropertyInfo> wrapAnyType(ProcessModel context, CPropertyInfo propertyInfo)
	{
		todo("Element collections of any type is not supported. See issue #HJIII-89 (http://jira.highsource.org/browse/HJIII-89)");
		return getFallback().process(context, propertyInfo);
	}
}
