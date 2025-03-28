package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.TypeInfo;
import org.jvnet.hyperjaxb.ejb.strategy.model.GetTypes;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

/**
 * Helper methods to get CTypeInfo information.
 * 
 * CTypeInfo is {@link TypeInfo} at the compile-time; either,
 * {@link CClassInfo}, {@link CBuiltinLeafInfo}, or {@link CElementInfo}.
 * 
 * Injected: none
 * Instantiated: none
 * 
 * Note: If the producer field type is a parameterized type with a type variable,
 *       it must have scope @Dependent
 */
@Dependent
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class DefaultGetTypes<C> implements GetTypes<C>
{
	@Override
	public Collection<? extends CTypeInfo> process(C context, CPropertyInfo propertyInfo)
	{
		return ref(context, propertyInfo);
	}

	@Override
	public Collection<? extends CTypeInfo> ref(C context, CPropertyInfo propertyInfo)
	{
		final Collection<? extends CTypeInfo> types = propertyInfo.ref();
		final JType baseType = propertyInfo.baseType;
		final ID id = propertyInfo.id();
		final CTypeInfo parent = propertyInfo.parent();
		if (ID.IDREF.equals(id) && baseType != null)
		{
			if (parent instanceof CClassInfo)
			{
				final CClassInfo parentClassInfo = (CClassInfo) parent;
				final String fullName = baseType.fullName();
				for (CClassInfo possibleClassInfo : parentClassInfo.model.beans().values())
				{
					final String possibleFullName = possibleClassInfo.fullName();
					if (fullName != null && fullName.equals(possibleFullName))
						return Collections.singleton(possibleClassInfo);
				}
			}
		}
		return types;
	}

	@Override
	public Collection<? extends CTypeRef> getTypes(C context, CElementPropertyInfo propertyInfo)
	{
		return propertyInfo.getTypes();
	}

	@Override
	public CNonElement getTarget(C context, CAttributePropertyInfo propertyInfo)
	{
		return propertyInfo.getTarget();
	}

	@Override
	public CNonElement getTarget(C context, CValuePropertyInfo propertyInfo)
	{
		return propertyInfo.getTarget();
	}

	@Override
	public Set<com.sun.tools.xjc.model.CElement> getElements(C context, CReferencePropertyInfo referencePropertyInfo)
	{
		return referencePropertyInfo.getElements();
	}

	@Override
	public TypeUse getTypeUse(C context, CPropertyInfo propertyInfo)
	{
		if (propertyInfo instanceof CValuePropertyInfo)
			return ((CValuePropertyInfo) propertyInfo).getTarget();
		else if (propertyInfo instanceof CAttributePropertyInfo)
			return ((CAttributePropertyInfo) propertyInfo).getTarget();
		else
		{
			final CTypeInfo type = propertyInfo.ref().iterator().next();
			if (type instanceof CBuiltinLeafInfo)
			{
				if (propertyInfo.getAdapter() != null)
					return TypeUseFactory.adapt((CBuiltinLeafInfo) type, propertyInfo.getAdapter());
				else
					return (CBuiltinLeafInfo) type;
			}
			else if (type instanceof CElementInfo)
			{
				final CElementInfo elementInfo = (CElementInfo) type;
				return getTypeUse(context, elementInfo.getProperty());
			}
			else
				throw new AssertionError("Unexpected type.");
		}
	}
}
