package org.jvnet.hyperjaxb3.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;

import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Basic;
import org.jvnet.hyperjaxb3.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapSingleEnumNonReference implements CreatePropertyInfos
{
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		// Single
		assert !propertyInfo.isCollection();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		// Builtin
		assert types.size() == 1;
		assert types.iterator().next() instanceof CEnumLeafInfo;
		return getCreatePropertyInfos(context, propertyInfo).process(context, propertyInfo);
	}

	public CreatePropertyInfos getCreatePropertyInfos(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final Basic basic = context.getCustomizing().getBasic(propertyInfo);
		if (basic != null && basic.getEnumeratedValue() != null)
			return new AdaptSingleEnumNonReferenceAsEnumValue();
		else
			return CreateNoPropertyInfos.INSTANCE;
	}
}
