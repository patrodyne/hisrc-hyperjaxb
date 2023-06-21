package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.hyperjaxb.locator.util.LocatorUtils.getLocation;

import java.util.Collection;

import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.TypeUse;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapSingleBuiltinNonReference extends AbstractWrapBuiltin
{
	@Override
	public CBuiltinLeafInfo getTypeUse(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		return (CBuiltinLeafInfo) types.iterator().next();
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
			return new AdaptSingleBuiltinNonReference(adaptingTypeUse);
	}

	@Override
	protected Collection<CPropertyInfo> wrapAnyType(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return new AdaptSingleWildcardNonReference(CBuiltinLeafInfo.STRING).process(context, propertyInfo);
	}
}
