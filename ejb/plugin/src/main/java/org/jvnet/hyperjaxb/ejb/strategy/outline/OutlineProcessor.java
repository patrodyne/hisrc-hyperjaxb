package org.jvnet.hyperjaxb.ejb.strategy.outline;

import java.util.Collection;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

/**
 * Interface for the XJC plugin context, the model outline, and configuration options
 * processing strategy.
 * 
 * The outline captures which code is generated for which model component.
 * 
 * @param <C> XJC Plugin Context
 */
public interface OutlineProcessor<C>
{
	/**
	 * Process the XJC plugin context, the model outline, and configuration options.
	 * 
	 * @param context The XJC plugin context.
	 * @param outline The XJC outline captures which code is generated for which model component.
	 * @param options The XJC plugin configuration options.
	 * @return A collection of CClassInfo representing the JAXB bound type/element.
	 * 
	 * @throws Exception May be a processing issue.
	 */
	public Collection<ClassOutline> process(C context, Outline outline,	Options options) throws Exception;
}
