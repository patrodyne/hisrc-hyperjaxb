package org.jvnet.hyperjaxb.ejb.strategy.processor;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.MojoConfigured;
import org.jvnet.hyperjaxb.ejb.strategy.model.ModelProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelBase;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

/**
 * The default strategy to process the XJC Model and Outline.
 * 
 * Note: The outline's is configurable to output to source code or mapping files.
 * 
 * Injected: ModelProcessor<EJBPlugin>, OutlineProcessor<EJBPlugin>
 * Instantiated: none
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class DefaultModelAndOutlineProcessor implements ModelAndOutlineProcessor<EJBPlugin>
{
	@Inject @ModelBase
	private ModelProcessor<EJBPlugin> modelProcessor;
	public ModelProcessor<EJBPlugin> getModelProcessor()
	{
		return modelProcessor;
	}
	public void setModelProcessor(ModelProcessor<EJBPlugin> modelProcessor)
	{
		this.modelProcessor = modelProcessor;
	}

	@Inject @MojoConfigured
	private OutlineProcessor<EJBPlugin> outlineProcessor;
	public OutlineProcessor<EJBPlugin> getOutlineProcessor()
	{
		return outlineProcessor;
	}
	public void setOutlineProcessor(OutlineProcessor<EJBPlugin> outlineProcessor)
	{
		this.outlineProcessor = outlineProcessor;
	}

	public Collection<CClassInfo> process(EJBPlugin context, Model model, Options options)
		throws Exception
	{
		return getModelProcessor().process(context, model, options);
	}

	public Collection<ClassOutline> process(EJBPlugin context, Outline outline, Options options)
		throws Exception
	{
		return getOutlineProcessor().process(context, outline, options);
	}
}
