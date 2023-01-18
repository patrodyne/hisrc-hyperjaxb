package org.jvnet.hyperjaxb.ejb.strategy.naming;

import org.jvnet.basicjaxb.config.LocatorLoader;

/**
 * An interface for a map of key-value pairs that is compatible with java.util.Properties.
 * This interface is used to implement specialized property objects that can be injected
 * unambiguously.
 */
public interface ReservedNames extends LocatorLoader<Object,Object>
{
}
