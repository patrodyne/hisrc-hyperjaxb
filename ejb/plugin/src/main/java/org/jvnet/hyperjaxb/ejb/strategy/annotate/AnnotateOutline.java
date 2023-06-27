package org.jvnet.hyperjaxb.ejb.strategy.annotate;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static java.lang.String.format;
import static org.jvnet.basicjaxb.util.CustomizationUtils.getInfo;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.basicjaxb.util.OutlineUtils.getContextPath;
import static org.jvnet.basicjaxb.util.OutlineUtils.getFieldName;
import static org.jvnet.basicjaxb.util.OutlineUtils.getLocalClassName;
import static org.jvnet.basicjaxb.util.OutlineUtils.getPackagedClassName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.basicjaxb.util.FieldAccessorUtils;
import org.jvnet.basicjaxb_annox.model.XAnnotation;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.MojoConfigured;
import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.outline.EJBOutlineProcessor;
import org.jvnet.hyperjaxb.persistence.util.AttributesUtils;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
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
public class AnnotateOutline extends EJBOutlineProcessor
{
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
	
	@Override
	public Collection<ClassOutline> process(EJBPlugin context, Outline outline)
		throws Exception
	{
		setPlugin(context);
		if ( isDebugEnabled() )
		{
			Iterator<? extends ClassOutline> classOutlines = outline.getClasses().iterator();
			if ( classOutlines.hasNext() )
				debug("{}, process; ContextPath={}", toLocation(classOutlines.next()), getContextPath(outline));
		}
		
		final Collection<? extends ClassOutline> classes = outline.getClasses();
		final Collection<ClassOutline> processedClassOutlines = new ArrayList<ClassOutline>(classes.size());
		for (final ClassOutline classOutline : classes)
		{
			if (!getIgnoring().isClassOutlineIgnored(getMapping(), classOutline))
			{
				final ClassOutline processedClassOutline = processClassOutline(this, classOutline);
				if (processedClassOutline != null)
					processedClassOutlines.add(processedClassOutline);
			}
		}
		return processedClassOutlines;
	}

	private ClassOutline processClassOutline(AnnotateOutline context, ClassOutline classOutline)
		throws Exception
	{
		final Object entityOrMappedSuperclassOrEmbeddable = context.getMapping()
			.getEntityOrMappedSuperclassOrEmbeddableMapping().process(context.getMapping(), classOutline);
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
			String msg = format("Either entity or mapped superclass expected, but an instance of [%s] received.",
				entityOrMappedSuperclassOrEmbeddable.getClass());
			throw new AssertionError(msg);
		}

		context.getApplyXAnnotations().annotate(classOutline.ref.owner(), classOutline.ref, annotations);
		debug("{}, processClassOutline: Class={}, Annotations={}",
			toLocation(classOutline), getLocalClassName(classOutline), arrayToString(annotations));

		// Prevent WARNING: "Unacknowledged customization check"
		for (CPluginCustomization cpc : CustomizationUtils.getCustomizations(classOutline))
		{
			trace(getInfo("mark", cpc));
			cpc.markAsAcknowledged();
		}

		if (classOutline.target.declaresAttributeWildcard())
			processAttributeWildcard(classOutline);

		final FieldOutline[] fieldOutlines = classOutline.getDeclaredFields();
		for (final FieldOutline fieldOutline : fieldOutlines)
			processFieldOutline(context, fieldOutline, attributes);
		return classOutline;
	}

	private void processAttributeWildcard(ClassOutline classOutline)
	{
		String FIELD_NAME = "otherAttributes";
		String METHOD_SEED = classOutline.parent().getModel().getNameConverter().toClassName(FIELD_NAME);
		final JMethod getOtherAttributesMethod = classOutline.ref.getMethod("get" + METHOD_SEED, new JType[0]);
		if (getOtherAttributesMethod != null)
		{
			debug("{}, processAttributeWildcard; Class={}, Wildcard={}, Annotations=@{}",
				toLocation(classOutline), getLocalClassName(classOutline),
				getOtherAttributesMethod.name(), Transient.class.getName());
			getOtherAttributesMethod.annotate(Transient.class);
		}
		else
			error("Could not find the attribute wildcard method in the class [{}].", getPackagedClassName(classOutline));
	}

	private FieldOutline processFieldOutline(AnnotateOutline context, FieldOutline fieldOutline, Object attributes)
	{
		final String fieldName = context.getMapping().getNaming().getPropertyName(context.getMapping(), fieldOutline);
		final String className = fieldOutline.parent().getImplClass().name();
		final JMethod issetter = FieldAccessorUtils.issetter(fieldOutline);

		if (issetter != null)
		{
			debug("{}, processFieldOutline; Class={}, IsSet={}, Annotations=@{}",
				toLocation(fieldOutline), className, issetter.name(), Transient.class.getName());
			issetter.annotate(Transient.class);
		}

		final Object attribute = AttributesUtils.getAttribute(attributes, fieldName);
		Collection<XAnnotation<?>> xannotations = context.getCreateXAnnotations().createAttributeAnnotations(attribute);
		if (xannotations == null)
			error("No annotations for the field [ {} ]: {}", getFieldName(fieldOutline), arrayToString(xannotations));
		else
		{
			final JMethod getter = FieldAccessorUtils.getter(fieldOutline);
			context.getApplyXAnnotations().annotate(fieldOutline.parent().ref.owner(), getter, xannotations);
		}
		
		debug("{}, processFieldOutline; Class={}, Field={}, Annotations={}",
			toLocation(fieldOutline), className, fieldName, arrayToString(xannotations));
		
		return fieldOutline;
	}
	
	private static String arrayToString(Object value)
	{
		return ArrayUtils.toString(value);
	}
}
