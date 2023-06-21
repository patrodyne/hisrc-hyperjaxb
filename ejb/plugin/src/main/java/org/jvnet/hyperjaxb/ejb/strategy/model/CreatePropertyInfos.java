package org.jvnet.hyperjaxb.ejb.strategy.model;

import java.util.Collection;

import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * Interface to create a {@link Collection} of {@link CPropertyInfo}s for the
 * given {@link CPropertyInfo} instance in the given {@link ProcessModel} context.
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
	 *         Must not be {@code null}, if nothing is created,
	 *         return an empty collection instead.
	 */
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo);
}
