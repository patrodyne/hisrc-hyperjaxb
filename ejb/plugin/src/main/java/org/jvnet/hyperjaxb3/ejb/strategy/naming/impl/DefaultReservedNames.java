package org.jvnet.hyperjaxb3.ejb.strategy.naming.impl;

import org.jvnet.hyperjaxb3.ejb.strategy.naming.ReservedNames;
import org.jvnet.jaxb2_commons.config.LocatorProperties;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

/**
 * An extension of LocatorProperties used to provide specialized property objects
 * that can be injected unambiguously.
 */
@Alternative
public class DefaultReservedNames extends LocatorProperties implements ReservedNames
{
	private static final long serialVersionUID = 20220901L;
}
