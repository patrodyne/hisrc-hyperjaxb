package org.jvnet.hyperjaxb.ejb.strategy.model;

import java.util.Collection;

import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * Creates properties for the given property.
 */
public interface CreatePropertyInfos 
	extends PropertyInfoProcessor<Collection<CPropertyInfo>, ProcessModel>
{
	/**
	 * Creates a collection of properties for the given property.
	 * 
	 * @param context Processing context.
	 * @param propertyInfo Property to be processed.
	 * 
	 * @return Collection of properties created for the given property.
	 *         Must not be &lt;code&gt;null&lt;/code&gt;, if nothing is created,
	 *         return an empty collection instead.
	 */
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo);
}
