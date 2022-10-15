package org.jvnet.hyperjaxb3.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jvnet.hyperjaxb3.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.TypeUse;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapSingleBuiltinReference extends AbstractWrapBuiltin
{
	public CBuiltinLeafInfo getTypeUse(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		final CElementInfo elementInfo = ((CElementInfo) types.iterator().next());
		final CBuiltinLeafInfo type = (CBuiltinLeafInfo) elementInfo.getContentType();
		return type;
	}

	@Override
	public CreatePropertyInfos getCreatePropertyInfos(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final TypeUse adaptingTypeUse = context.getAdaptBuiltinTypeUse().process(context, propertyInfo);
		return new AdaptSingleBuiltinReference(adaptingTypeUse);
	}

	protected Collection<CPropertyInfo> wrapAnyType(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return new AdaptSingleWildcardReference(CBuiltinLeafInfo.STRING).process(context, propertyInfo);
	}
}
