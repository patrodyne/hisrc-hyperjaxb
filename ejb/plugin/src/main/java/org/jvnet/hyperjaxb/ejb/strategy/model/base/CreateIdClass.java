package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import org.jvnet.basicjaxb.util.ClassUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreateIdClassProcessor;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.xml.sax.Locator;

import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CPropertyVisitor;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.LocalScoping;
import com.sun.xml.xsom.XSComponent;

import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.IdClass;
import ee.jakarta.xml.ns.persistence.orm.MappedSuperclass;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class CreateIdClass implements CreateIdClassProcessor
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public Collection<CClassInfo> process(final ProcessModel context, CClassInfo classInfo)
	{
		setPlugin(context.getPlugin());
		
		final XSComponent component = classInfo.getSchemaComponent();
		final Collection<CPropertyInfo> propertyInfos = context.getGetIdPropertyInfos().process(context, classInfo);
		
		if (propertyInfos.size() > 1)
		{
			context.getGetIdPropertyInfos().process(context, classInfo);
			
			final CClassInfoParent parent = Ring.get(BGMBuilder.class).getGlobalBinding()
				.getFlattenClasses() == LocalScoping.NESTED ? classInfo : classInfo.parent();
			
			final CClassInfo idClassInfo = new CClassInfo(classInfo.model, parent, classInfo.shortName + "Id", null,
				null, null, component, new CCustomizations());
			
			org.jvnet.basicjaxb.plugin.inheritance.Customizations._implements(idClassInfo, Serializable.class.getName());
			
			// Ignore IdClass from being an Entity.
			Customizations.markIgnored(idClassInfo);
			
			getPlugin().debug("{}, CreateIdClass: class={}", toLocation(idClassInfo), idClassInfo.shortName);
			
			// Customizations.markGenerated(idClassInfo);
			for (CPropertyInfo propertyInfo : propertyInfos)
			{
				idClassInfo.addProperty(propertyInfo.accept(new CPropertyVisitor<CPropertyInfo>()
				{
					@Override
					public CPropertyInfo onAttribute(CAttributePropertyInfo propertyInfo)
					{
						return new CAttributePropertyInfo(propertyInfo.getName(true), propertyInfo.getSchemaComponent(),
							new CCustomizations(), null, propertyInfo.getXmlName(),
							context.getGetTypes().getTarget(context, propertyInfo), propertyInfo.getSchemaType(),
							false);
					}

					@Override
					public CPropertyInfo onElement(CElementPropertyInfo propertyInfo)
					{
						final CElementPropertyInfo elementPropertyInfo = new CElementPropertyInfo(
							propertyInfo.getName(true),
							propertyInfo.isCollection() ? CollectionMode.REPEATED_ELEMENT : CollectionMode.NOT_REPEATED,
							propertyInfo.id(), propertyInfo.getExpectedMimeType(), propertyInfo.getSchemaComponent(),
							new CCustomizations(), (Locator) null, false);
						elementPropertyInfo.getTypes().addAll(context.getGetTypes().getTypes(context, propertyInfo));
						return elementPropertyInfo;
					}

					@Override
					public CPropertyInfo onReference(CReferencePropertyInfo propertyInfo)
					{
						final CReferencePropertyInfo referencePropertyInfo = new CReferencePropertyInfo(
							propertyInfo.getName(true), propertyInfo.isCollection(), false, propertyInfo.isMixed(),
							propertyInfo.getSchemaComponent(), new CCustomizations(), null, propertyInfo.isDummy(),
							propertyInfo.isContent(), propertyInfo.isMixedExtendedCust());
						referencePropertyInfo.getElements()
							.addAll(context.getGetTypes().getElements(context, referencePropertyInfo));
						return referencePropertyInfo;
					}

					@Override
					public CPropertyInfo onValue(CValuePropertyInfo propertyInfo)
					{
						return new CValuePropertyInfo(propertyInfo.getName(true), propertyInfo.getSchemaComponent(),
							new CCustomizations(), null, context.getGetTypes().getTarget(context, propertyInfo),
							propertyInfo.getSchemaType());
					}
				}));
			}
			final IdClass idClass = new IdClass();
			idClass.setClazz(ClassUtils.getPackagedClassName(idClassInfo));
			final Object customization = context.getCustomizing().getEntityOrMappedSuperclassOrEmbeddable(classInfo);
			if (customization instanceof Entity)
				((Entity) customization).setIdClass(idClass);
			else if (customization instanceof MappedSuperclass)
				((MappedSuperclass) customization).setIdClass(idClass);
			return Collections.singleton(idClassInfo);
		}
		else
			return Collections.emptyList();
	}
}
