package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.getter;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.codemodel.util.JTypeUtils.isBasicType;
import static org.jvnet.hyperjaxb.jpa.Customizations.EMBEDDED_ID_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.ID_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.VERSION_ELEMENT_NAME;

import java.util.Collection;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.Variant;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import ee.jakarta.xml.ns.persistence.orm.Transient;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

/**
 * An implementation of ClassOutlineMapping to process the Mapping context into EmbeddableAttributes.
 * 
 * Injected: none
 * Instantiated: none 
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@Variant(type = Variant.Type.EJB)
public class EmbeddableAttributesMapping implements ClassOutlineMapping<EmbeddableAttributes>
{
	private EJBPlugin plugin;
	protected EJBPlugin getPlugin() { return plugin; }
	protected void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public EmbeddableAttributes process(Mapping context, ClassOutline classOutline)
	{
		setPlugin(context.getPlugin());
		
		final EmbeddableAttributes attributes = new EmbeddableAttributes();
		final FieldOutline[] fieldOutlines = classOutline.getDeclaredFields();
		for (final FieldOutline fieldOutline : fieldOutlines)
		{
			final Object attributeMapping = getAttributeMapping(context, fieldOutline)
				.process(context, fieldOutline);
			
			if (attributeMapping instanceof Basic)
				attributes.getBasic().add((Basic) attributeMapping);
			else if (attributeMapping instanceof Transient)
				attributes.getTransient().add((Transient) attributeMapping);
		}
		return attributes;
	}

	public FieldOutlineMapping<?> getAttributeMapping(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final ClassOutline classOutline = fieldOutline.parent();
		
		if (context.getIgnoring().isFieldOutlineIgnored(context, fieldOutline))
		{
			getPlugin().trace("{}, getAttributeMapping: class={}, field={};"
				+ " 'marked as [ignored]' field."
				+ " It will be made transient.",
				toLocation(propertyInfo, classOutline.target),
				classOutline.getImplClass().name(), propertyInfo.getName(false) );
			return context.getTransientMapping();
		}
		else if (isFieldOutlineId(context, fieldOutline))
		{
			getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
				+ " 'marked as [id]' field is not supported in embeddable classes."
				+ " It will be made transient.",
				toLocation(propertyInfo, classOutline.target),
				classOutline.getImplClass().name(), propertyInfo.getName(false) );
			return context.getTransientMapping();
		}
		else if (isFieldOutlineEmbeddedId(context, fieldOutline))
		{
			getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
				+ " 'marked as [embedded-id]' field is not supported in embeddable classes."
				+ " It will be made transient.",
				toLocation(propertyInfo, classOutline.target),
				classOutline.getImplClass().name(), propertyInfo.getName(false) );
			return context.getTransientMapping();
		}
		else if (isFieldOutlineVersion(context, fieldOutline))
		{
			getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
				+ " 'marked as [version]' field is not supported in embeddable classes."
				+ " It will be made transient.",
				toLocation(propertyInfo, classOutline.target),
				classOutline.getImplClass().name(), propertyInfo.getName(false) );
			return context.getTransientMapping();
		}
		else
		{
			if (!propertyInfo.isCollection())
			{
				final Collection<? extends CTypeInfo> types = context.getGetTypes()
					.process(context, propertyInfo);

				if (types.size() == 1)
				{
					if (isFieldOutlineBasic(context, fieldOutline))
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={};"
							+ " basic homogeneous single field.",
							toLocation(propertyInfo, classOutline.target),
							classOutline.getImplClass().name(), propertyInfo.getName(false) );
						return context.getBasicMapping();
					}
					else if (isFieldOutlineComplex(context, fieldOutline))
					{
						getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
							+ " complex field is not supported in embeddable classes."
							+ " It will be made transient.",
							toLocation(propertyInfo, classOutline.target),
							classOutline.getImplClass().name(), propertyInfo.getName(false) );
						return context.getTransientMapping();
					}
					else
					{
						getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
							+ " non-basic field is not supported in embeddable classes."
							+ " It will be made transient.",
							toLocation(propertyInfo, classOutline.target),
							classOutline.getImplClass().name(), propertyInfo.getName(false) );
						return context.getTransientMapping();
					}
				}
				else
				{
					getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
						+ " heterogeneous field is not supported in embeddable classes."
						+ " It will be made transient.",
						toLocation(propertyInfo, classOutline.target),
						classOutline.getImplClass().name(), propertyInfo.getName(false) );
					return context.getTransientMapping();
				}
			}
			else
			{
				getPlugin().warn("{}, getAttributeMapping: class={}, field={};"
					+ " collection field is not supported in embeddable classes."
					+ " It will be made transient.",
					toLocation(propertyInfo, classOutline.target),
					classOutline.getImplClass().name(), propertyInfo.getName(false) );
				return context.getTransientMapping();
			}
		}
	}

	public boolean isFieldOutlineId(Mapping context, FieldOutline fieldOutline)
	{
		return containsCustomization(fieldOutline, ID_ELEMENT_NAME);
	}

	public boolean isFieldOutlineVersion(Mapping context, FieldOutline fieldOutline)
	{
		return containsCustomization(fieldOutline, VERSION_ELEMENT_NAME);
	}

	public boolean isFieldOutlineBasic(Mapping context, FieldOutline fieldOutline)
	{
		return isFieldOutlineCore(context, fieldOutline) || isFieldOutlineEnumerated(context, fieldOutline);
	}

	public boolean isFieldOutlineCore(Mapping context, FieldOutline fieldOutline)
	{
		final JMethod getter = getter(fieldOutline);
		final JType type = getter.type();
		return isBasicType(type);
	}

	public boolean isFieldOutlineEnumerated(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes()
			.process(context, propertyInfo);
		if (types.size() == 1)
		{
			final CTypeInfo type = types.iterator().next();
			return type instanceof CEnumLeafInfo;
		}
		else
			return false;
	}

	public boolean isFieldOutlineComplex(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		assert types.size() == 1;
		final CTypeInfo type = types.iterator().next();
		return type instanceof CClass;
	}

	public boolean isFieldOutlineEmbeddedId(Mapping context, FieldOutline fieldOutline)
	{
		return containsCustomization(fieldOutline, EMBEDDED_ID_ELEMENT_NAME);
	}
}
