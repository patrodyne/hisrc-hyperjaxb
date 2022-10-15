package org.jvnet.hyperjaxb3.ejb.strategy.processor;

import org.jvnet.hyperjaxb3.ejb.plugin.EjbPlugin;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ModelProcessor;
import org.jvnet.hyperjaxb3.ejb.strategy.outline.OutlineProcessor;

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
	public ModelProcessor<EjbPlugin> getModelProcessor();
	public OutlineProcessor<EjbPlugin> getOutlineProcessor();
}
