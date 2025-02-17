package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;
import java.util.LinkedList;

import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.strategy.model.GetIdPropertyInfoProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Customizations;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class GetIdPropertyInfos implements GetIdPropertyInfoProcessor
{
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CClassInfo classInfo)
	{
		final Collection<CPropertyInfo> ids = new LinkedList<CPropertyInfo>();
		if (classInfo.getBaseClass() != null)
			ids.addAll(process(context, classInfo.getBaseClass()));
		if (!CustomizationUtils.containsCustomization(classInfo, Customizations.IGNORED_ELEMENT_NAME))
		{
			for (CPropertyInfo propertyInfo : classInfo.getProperties())
			{
				if ((CustomizationUtils.containsCustomization(propertyInfo, Customizations.ID_ELEMENT_NAME)
					|| CustomizationUtils.containsCustomization(propertyInfo, Customizations.EMBEDDED_ID_ELEMENT_NAME))
					&& !CustomizationUtils.containsCustomization(propertyInfo, Customizations.IGNORED_ELEMENT_NAME))
				{
					ids.add(propertyInfo);
				}
			}
		}
		return ids;
	}
}
