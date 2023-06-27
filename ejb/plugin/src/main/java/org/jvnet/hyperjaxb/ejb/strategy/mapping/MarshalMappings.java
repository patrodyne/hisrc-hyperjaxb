package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.basicjaxb.util.OutlineUtils.getContextPath;
import static org.jvnet.basicjaxb.util.OutlineUtils.getLocalClassName;
import static org.jvnet.hyperjaxb.ejb.Constants.ORM_EJB_VERSION;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import org.jvnet.basicjaxb.util.CodeModelUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.outline.EJBOutlineProcessor;
import org.jvnet.hyperjaxb.persistence.jpa.JPAUtils;

import com.sun.codemodel.fmt.JTextFile;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.orm.Embeddable;
import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.EntityMappings;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@StrategyMapping
public class MarshalMappings extends EJBOutlineProcessor
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
	
	protected Marshaller getMarshaller() throws JAXBException
	{
		return JPAUtils.createMarshaller();
	}

	@Override
	public Collection<ClassOutline> process(EJBPlugin context, Outline outline)
		throws Exception
	{
		setPlugin(context);
		debug("{}, process: ContextPath={}", toLocation("unknown"), getContextPath(outline));
		
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

	private ClassOutline processClassOutline(MarshalMappings context, ClassOutline classOutline)
		throws Exception
	{
		final String className = CodeModelUtils.getLocalClassName(classOutline.ref);
		final JTextFile classOrmXmlFile = new JTextFile(className + ".orm.xml");
		classOutline._package()._package().addResourceFile(classOrmXmlFile);
		final EntityMappings entityMappings = createEntityMappings();
		final Object draftEntityOrMappedSuperclassOrEmbeddable = context.getMapping()
			.getEntityOrMappedSuperclassOrEmbeddableMapping().process(context.getMapping(), classOutline);
		
		if (draftEntityOrMappedSuperclassOrEmbeddable instanceof Entity)
		{
			final Entity draftEntity = (Entity) draftEntityOrMappedSuperclassOrEmbeddable;
			final Entity entity = new Entity();
			entity.mergeFrom(draftEntity, entity);
			entityMappings.getEntity().add(entity);
		}
		else if (draftEntityOrMappedSuperclassOrEmbeddable instanceof MappedSuperclass)
		{
			final MappedSuperclass draftMappedSuperclass = (MappedSuperclass) draftEntityOrMappedSuperclassOrEmbeddable;
			final MappedSuperclass entity = new MappedSuperclass();
			entity.mergeFrom(draftMappedSuperclass, entity);
			entityMappings.getMappedSuperclass().add(entity);
		}
		else if (draftEntityOrMappedSuperclassOrEmbeddable instanceof Embeddable)
		{
			final Embeddable draftEmbeddable = (Embeddable) draftEntityOrMappedSuperclassOrEmbeddable;
			final Embeddable entity = new Embeddable();
			entity.mergeFrom(draftEmbeddable, entity);
			entityMappings.getEmbeddable().add(entity);
		}
		else
			throw new AssertionError("Either one-to-many or many-to-many mappings are expected.");
		
		final Writer writer = new StringWriter();
		getMarshaller().marshal(entityMappings, writer);
		classOrmXmlFile.setContents(writer.toString());
		
		debug("{}, processClassOutline: Class={}", toLocation(classOutline), getLocalClassName(classOutline));
		
		return classOutline;
	}

	protected EntityMappings createEntityMappings()
	{
		final EntityMappings entityMappings = new EntityMappings();
		entityMappings.setVersion(ORM_EJB_VERSION);
		return entityMappings;
	}
}
