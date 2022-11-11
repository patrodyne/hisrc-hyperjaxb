package org.jvnet.hyperjaxb.ejb.strategy.processor;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.ModelProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;

/**
 * Interfaces for the XJC plugin context, the schema model, the model outline,
 * and configuration options processing strategy.
 * 
 * The outline captures which Java code is generated for which model component.
 * 
 * @param <C> XJC Plugin Context
 */
public interface ModelAndOutlineProcessor<C> extends ModelProcessor<C>, OutlineProcessor<C>
{
	public ModelProcessor<EJBPlugin> getModelProcessor();
	public OutlineProcessor<EJBPlugin> getOutlineProcessor();
}
