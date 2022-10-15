package org.jvnet.hyperjaxb3.ejb.jpa3.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;

import org.jvnet.hyperjaxb3.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.AbstractWrapBuiltin;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.AdaptCollectionBuiltinNonReference;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.CreateNoPropertyInfos;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
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
			logger.debug("No adaptation required.");
			return CreateNoPropertyInfos.INSTANCE;
		}
		else
		{
			return new AdaptCollectionBuiltinNonReference(adaptingTypeUse);
		}
	}

	protected Collection<CPropertyInfo> wrapAnyType(ProcessModel context, CPropertyInfo propertyInfo)
	{
		todo("Element collections of any type is not supported. See issue #HJIII-89 (http://jira.highsource.org/browse/HJIII-89)");
		return getFallback().process(context, propertyInfo);
	}
}
