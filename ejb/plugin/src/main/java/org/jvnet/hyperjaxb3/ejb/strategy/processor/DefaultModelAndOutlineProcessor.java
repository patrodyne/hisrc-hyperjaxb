package org.jvnet.hyperjaxb3.ejb.strategy.processor;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;

import org.jvnet.hyperjaxb3.ejb.plugin.EjbPlugin;
import org.jvnet.hyperjaxb3.ejb.strategy.MojoConfigured;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ModelProcessor;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.ModelBase;
import org.jvnet.hyperjaxb3.ejb.strategy.outline.OutlineProcessor;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

/**
 * The default strategy to process the XJC Model and Outline.
 * 
 * Note: The outline's is configurable to output to source code or mapping files.
 * 
 * Injected: ModelProcessor<EjbPlugin>, OutlineProcessor<EjbPlugin>
 * Instantiated: none
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class DefaultModelAndOutlineProcessor implements ModelAndOutlineProcessor<EjbPlugin>
{
	@Inject @ModelBase
	private ModelProcessor<EjbPlugin> modelProcessor;
	public ModelProcessor<EjbPlugin> getModelProcessor()
	{
		return modelProcessor;
	}
	public void setModelProcessor(ModelProcessor<EjbPlugin> modelProcessor)
	{
		this.modelProcessor = modelProcessor;
	}

	@Inject @MojoConfigured
	private OutlineProcessor<EjbPlugin> outlineProcessor;
	public OutlineProcessor<EjbPlugin> getOutlineProcessor()
	{
		return outlineProcessor;
	}
	public void setOutlineProcessor(OutlineProcessor<EjbPlugin> outlineProcessor)
	{
		this.outlineProcessor = outlineProcessor;
	}

	public Collection<CClassInfo> process(EjbPlugin context, Model model, Options options)
		throws Exception
	{
		return getModelProcessor().process(context, model, options);
	}

	public Collection<ClassOutline> process(EjbPlugin context, Outline outline, Options options)
		throws Exception
	{
		return getOutlineProcessor().process(context, outline, options);
	}
}
