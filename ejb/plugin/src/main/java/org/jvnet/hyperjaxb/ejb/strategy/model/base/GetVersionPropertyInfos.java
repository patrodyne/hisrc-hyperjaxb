package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.hyperjaxb.jpa.Customizations.IGNORED_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.VERSION_ELEMENT_NAME;

import java.util.Collection;
import java.util.LinkedList;

import org.jvnet.hyperjaxb.ejb.strategy.model.GetVersionPropertyInfoProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class GetVersionPropertyInfos implements GetVersionPropertyInfoProcessor
{
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CClassInfo classInfo)
	{
		final Collection<CPropertyInfo> version = new LinkedList<CPropertyInfo>();
		
		if (classInfo.getBaseClass() != null)
			version.addAll(process(context, classInfo.getBaseClass()));
		
		if (!containsCustomization(classInfo, IGNORED_ELEMENT_NAME))
		{
			for (CPropertyInfo propertyInfo : classInfo.getProperties())
			{
				if (containsCustomization(propertyInfo, VERSION_ELEMENT_NAME)
					&& !containsCustomization(propertyInfo, IGNORED_ELEMENT_NAME))
				{
					version.add(propertyInfo);
				}
			}
		}
		return version;
	}
}
