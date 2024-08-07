package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;
import java.util.Set;

import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Customizations;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapSingleClassElementReference implements CreatePropertyInfos
{
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		assert propertyInfo instanceof CReferencePropertyInfo;
		final CReferencePropertyInfo referencePropertyInfo = (CReferencePropertyInfo) propertyInfo;
		assert referencePropertyInfo.getWildcard() == null;
		assert !referencePropertyInfo.isMixed();
		final Set<CElement> elements = context.getGetTypes().getElements(context, referencePropertyInfo);
		assert elements.size() == 1;
		final CElement element = elements.iterator().next();
		assert element instanceof CElementInfo;
		final CElementInfo elementInfo = (CElementInfo) element;
		final CNonElement contentType = elementInfo.getContentType();
		assert contentType instanceof CClassInfo;
		final CClassInfo classInfo = (CClassInfo) contentType;
		final CreatePropertyInfos createPropertyInfos = new AdaptSingleBuiltinReference(classInfo);
		final Collection<CPropertyInfo> newPropertyInfos = createPropertyInfos.process(context, propertyInfo);
		Customizations.markIgnored(propertyInfo);
		return newPropertyInfos;
	}
}
