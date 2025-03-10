package org.jvnet.hyperjaxb.ejb.strategy.model;

import java.util.Collection;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;

/**
 * Interface for the XJC plugin context, the schema model, and configuration options
 * processing strategy.
 * 
 * @param <C> XJC Plugin Context
 */
public interface ModelProcessor<C>
{
	/**
	 * Process the XJC plugin context and the schema model.
	 * 
	 * @param context The XJC plugin context.
	 * @param model The XJC language-neutral representation of the schema.
	 * 
	 * @return A collection of CClassInfo representing the JAXB bound type/element.
	 * 
	 * @throws Exception May be a processing issue.
	 */
	public Collection<CClassInfo> process(C context, Model model) throws Exception;
}
