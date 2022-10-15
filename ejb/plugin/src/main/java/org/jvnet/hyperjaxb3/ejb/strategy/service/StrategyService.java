package org.jvnet.hyperjaxb3.ejb.strategy.service;

import java.util.Collection;

import org.jvnet.hyperjaxb3.ejb.plugin.EjbPlugin;
import org.jvnet.hyperjaxb3.ejb.strategy.mapping.MappingContext;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb3.ejb.strategy.processor.ModelAndOutlineProcessor;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StrategyService
{
	// Interface for the JPA entity, table, etc. naming strategy.
	@Inject
	private Naming naming;
	public Naming getNaming() { return naming; }
	public void setNaming(Naming naming) { this.naming = naming; }
	
	// A context of injected and instantiated mappings for strategic processing.
	@Inject
	private MappingContext mappingContext;
	public MappingContext getMappingContext() { return mappingContext; }
	public void setMappingContext(MappingContext mapping) { this.mappingContext = mapping; }
	
	// Interfaces to process the XJC plugin context, the schema model, the model outline, and configuration options.
	@Inject
	private ModelAndOutlineProcessor<EjbPlugin> modelAndOutlineProcessor;
    public ModelAndOutlineProcessor<EjbPlugin> getModelAndOutlineProcessor() { return modelAndOutlineProcessor; }
	public void setModelAndOutlineProcessor(ModelAndOutlineProcessor<EjbPlugin> modelAndOutlineProcessor) { this.modelAndOutlineProcessor = modelAndOutlineProcessor; }

	/**
	 * Process the XJC plugin context, the schema model, and configuration options.
	 * 
	 * @param context The XJC plugin context.
	 * @param model The XJC language-neutral representation of the schema.
	 * @param options The XJC plugin configuration options.
	 * @return A collection of CClassInfo representing the JAXB bound type/element.
	 * 
	 * @throws Exception May be a processing issue.
	 */
	public Collection<CClassInfo> process(EjbPlugin context, Model model, Options options)
		throws Exception
	{
		return getModelAndOutlineProcessor().process(context, model, options);
	}
	
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
	public Collection<ClassOutline> process(EjbPlugin context, Outline outline,	Options options)
		throws Exception
	{
		return getModelAndOutlineProcessor().process(context, outline, options);
	}
}
