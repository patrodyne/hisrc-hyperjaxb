package org.jvnet.hyperjaxb.ejb.strategy.model;

import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * An interface to process a {@link CPropertyInfo} in a given
 * context.
 * 
 * @param <T> The generic return type.
 * @param <C> The generic context type.
 */
public interface PropertyInfoProcessor<T, C>
{
	public T process(C context, CPropertyInfo propertyInfo);
}
