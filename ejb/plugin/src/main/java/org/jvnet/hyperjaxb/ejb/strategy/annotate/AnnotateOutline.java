package org.jvnet.hyperjaxb.ejb.strategy.annotate;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;
import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.basicjaxb.util.FieldAccessorUtils;
import org.jvnet.basicjaxb.util.OutlineUtils;
import org.jvnet.basicjaxb_annox.model.XAnnotation;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.MojoConfigured;
import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.outline.OutlineProcessor;
import org.jvnet.hyperjaxb.persistence.util.AttributesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.Embeddable;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.persistence.Transient;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@StrategyAnnotate
public class AnnotateOutline implements OutlineProcessor<EJBPlugin>
{
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Inject
	private Ignoring ignoring;
	public Ignoring getIgnoring()
	{
		return ignoring;
	}
	public void setIgnoring(Ignoring ignoring)
	{
		this.ignoring = ignoring;
	}

	@Inject
	private Mapping mapping;
	public Mapping getMapping()
	{
		return mapping;
	}
	public void setMapping(Mapping mapping)
	{
		this.mapping = mapping;
	}

	@Inject @MojoConfigured
	private CreateXAnnotations createXAnnotations;
	public CreateXAnnotations getCreateXAnnotations()
	{
		return createXAnnotations;
	}
	public void setCreateXAnnotations(CreateXAnnotations createXAnnotations)
	{
		this.createXAnnotations = createXAnnotations;
	}

	private Annotator applyXAnnotations = new Annotator();
	public Annotator getApplyXAnnotations()
	{
		return applyXAnnotations;
	}
	public void setApplyXAnnotations(Annotator annotator)
	{
		this.applyXAnnotations = annotator;
	}

	
	public Collection<ClassOutline> process(EJBPlugin context, Outline outline, Options options)
		throws Exception
	{
		logger.debug("Processing outline with context path [" + OutlineUtils.getContextPath(outline) + "].");
		final Collection<? extends ClassOutline> classes = outline.getClasses();
		final Collection<ClassOutline> processedClassOutlines = new ArrayList<ClassOutline>(classes.size());
		for (final ClassOutline classOutline : classes)
		{
			if (!getIgnoring().isClassOutlineIgnored(getMapping(), classOutline))
			{
				final ClassOutline processedClassOutline = process(this, classOutline, options);
				if (processedClassOutline != null)
					processedClassOutlines.add(processedClassOutline);
			}
		}
		return processedClassOutlines;
	}

	public ClassOutline process(AnnotateOutline context, ClassOutline classOutline, Options options)
		throws Exception
	{
		logger.debug("Processing class outline [" + OutlineUtils.getClassName(classOutline) + "].");
		final Object entityOrMappedSuperclassOrEmbeddable = context.getMapping()
			.getEntityOrMappedSuperclassOrEmbeddableMapping().process(context.getMapping(), classOutline, options);
		final Object attributes;
		final Collection<XAnnotation<?>> annotations;

		if (entityOrMappedSuperclassOrEmbeddable instanceof Entity)
		{
			final Entity entity = (Entity) entityOrMappedSuperclassOrEmbeddable;
			attributes = entity.getAttributes() == null ? new Attributes() : entity.getAttributes();
			annotations = context.getCreateXAnnotations().createEntityAnnotations(entity);
		}
		else if (entityOrMappedSuperclassOrEmbeddable instanceof MappedSuperclass)
		{
			final MappedSuperclass entity = (MappedSuperclass) entityOrMappedSuperclassOrEmbeddable;
			attributes = entity.getAttributes() == null ? new Attributes() : entity.getAttributes();
			annotations = context.getCreateXAnnotations().createMappedSuperclassAnnotations(entity);
		}
		else if (entityOrMappedSuperclassOrEmbeddable instanceof Embeddable)
		{
			final Embeddable embeddable = (Embeddable) entityOrMappedSuperclassOrEmbeddable;
			attributes = embeddable.getAttributes() == null ? new EmbeddableAttributes() : embeddable.getAttributes();
			annotations = context.getCreateXAnnotations().createEmbeddableAnnotations(embeddable);
		}
		else
		{
			throw new AssertionError("Either entity or mapped superclass expected, but an instance of ["
				+ entityOrMappedSuperclassOrEmbeddable.getClass() + "] received.");
		}

		logger.debug("Annotating the class ["	+ OutlineUtils.getClassName(classOutline) + "]:\n"
			+ ArrayUtils.toString(annotations));
		context.getApplyXAnnotations().annotate(classOutline.ref.owner(), classOutline.ref, annotations);

		// Prevent WARNING: "Unacknowledged customization check"
		for (CPluginCustomization cpc : CustomizationUtils.getCustomizations(classOutline))
		{
			logger.debug("Customization: " + CustomizationUtils.getInfo("mark", cpc));
			cpc.markAsAcknowledged();
		}

		if (classOutline.target.declaresAttributeWildcard())
			processAttributeWildcard(classOutline);

		final FieldOutline[] fieldOutlines = classOutline.getDeclaredFields();
		for (final FieldOutline fieldOutline : fieldOutlines)
			process(context, fieldOutline, options, attributes);
		return classOutline;
	}

	private void processAttributeWildcard(ClassOutline classOutline)
	{
		logger.debug("The class ["	+ OutlineUtils.getClassName(classOutline)
			+ "] declares an attribute wildcard which will be made transient.");
		String FIELD_NAME = "otherAttributes";
		String METHOD_SEED = classOutline.parent().getModel().getNameConverter().toClassName(FIELD_NAME);
		final JMethod getOtherAttributesMethod = classOutline.ref.getMethod("get" + METHOD_SEED, new JType[0]);
		if (getOtherAttributesMethod == null)
		{
			logger.error("Could not find the attribute wildcard method in the class ["
							+ OutlineUtils.getClassName(classOutline) + "].");
		}
		else
			getOtherAttributesMethod.annotate(Transient.class);
	}

	public FieldOutline process(AnnotateOutline context, FieldOutline fieldOutline, Options options, Object attributes)
	{
		final String name = context.getMapping().getNaming().getPropertyName(context.getMapping(), fieldOutline);
		logger.debug("Processing field [" + name + "].");
		final JMethod issetter = FieldAccessorUtils.issetter(fieldOutline);

		if (issetter != null)
		{
			logger.debug("Annotating [" + issetter.name() + "] with @javax.persistence.Transient.");
			issetter.annotate(Transient.class);
		}

		final Object attribute = AttributesUtils.getAttribute(attributes, name);
		Collection<XAnnotation<?>> xannotations = context.getCreateXAnnotations().createAttributeAnnotations(attribute);

		final JMethod getter = FieldAccessorUtils.getter(fieldOutline);
		logger.debug("Annotating the field ["	+ OutlineUtils.getFieldName(fieldOutline) + "]:\n"
			+ ArrayUtils.toString(xannotations));
		if (xannotations == null)
		{
			logger.error("No annotations for the field ["	+ OutlineUtils.getFieldName(fieldOutline) + "]:\n"
							+ ArrayUtils.toString(xannotations));
		}
		else
			context.getApplyXAnnotations().annotate(fieldOutline.parent().ref.owner(), getter, xannotations);
		return fieldOutline;
	}
}