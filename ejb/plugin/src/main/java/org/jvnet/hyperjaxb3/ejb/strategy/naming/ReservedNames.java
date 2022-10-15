package org.jvnet.hyperjaxb3.ejb.strategy.naming;

import org.jvnet.jaxb2_commons.config.LocatorLoader;

/**
 * An interface for a map of key-value pairs that is compatible with java.util.Properties.
 * This interface is used to implement specialized property objects that can be injected
 * unambiguously.
 * 
 * @param <K> The key type.
 * @param <V> The value type.
 */
public interface ReservedNames extends LocatorLoader<Object,Object>
{
}