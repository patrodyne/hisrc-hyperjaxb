package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static com.sun.tools.xjc.outline.Aspect.EXPOSED;
import static java.lang.String.format;
import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.getter;
import static org.jvnet.hyperjaxb.codemodel.util.JTypeUtils.isBasicType;
import static org.jvnet.hyperjaxb.ejb.Constants.TODO_LOG_LEVEL;
import static org.jvnet.hyperjaxb.jpa.Customizations.EMBEDDABLE_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.EMBEDDED_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.EMBEDDED_ID_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.ID_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.VERSION_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.locator.util.LocatorUtils.getLocation;

import java.util.Collection;

import org.jvnet.hyperjaxb.codemodel.util.JTypeUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.xjc.model.CTypeInfoUtils;

import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.ElementCollection;
import ee.jakarta.xml.ns.persistence.orm.Embedded;
import ee.jakarta.xml.ns.persistence.orm.EmbeddedId;
import ee.jakarta.xml.ns.persistence.orm.Id;
import ee.jakarta.xml.ns.persistence.orm.ManyToMany;
import ee.jakarta.xml.ns.persistence.orm.ManyToOne;
import ee.jakarta.xml.ns.persistence.orm.OneToMany;
import ee.jakarta.xml.ns.persistence.orm.OneToOne;
import ee.jakarta.xml.ns.persistence.orm.Transient;
import ee.jakarta.xml.ns.persistence.orm.Version;

/**
 * Process the {@link FieldOutline}s of a given {@link ClassOutline} to
 * obtain an {@link AttributeMapping} then collect by ORM type.
 */
public class AttributesMapping implements ClassOutlineMapping<Attributes>
{
	private EJBPlugin plugin;
	protected EJBPlugin getPlugin() { return plugin; }
	protected void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	@Override
	public Attributes process(Mapping context, ClassOutline classOutline)
	{
		setPlugin(context.getPlugin());
		
		final Attributes attributes = new Attributes();
		final FieldOutline[] fieldOutlines = classOutline.getDeclaredFields();
		for (final FieldOutline fieldOutline : fieldOutlines)
		{
			final Object attributeMapping =	getAttributeMapping(context, fieldOutline)
				.process(context, fieldOutline);
			
			if (attributeMapping instanceof Id)
			{
				if (attributes.getEmbeddedId() == null)
					attributes.getId().add((Id) attributeMapping);
				else
				{
					CClassInfo classInfo = fieldOutline.parent().target;
					CPropertyInfo propInfo = fieldOutline.getPropertyInfo();
					getPlugin().error("{}, AttributesMapping: class={}, field={};"
						+ " could not add an id element to the attributes"
						+ " because they already contain an embedded-id element.",
						getLocation(fieldOutline), classInfo.shortName, propInfo.getName(false));
				}
			}
			else if (attributeMapping instanceof EmbeddedId)
			{
				if (!attributes.getId().isEmpty())
				{
					CClassInfo classInfo = fieldOutline.parent().target;
					CPropertyInfo propInfo = fieldOutline.getPropertyInfo();
					getPlugin().error("{}, AttributesMapping: class={}, field={};"
						+ " could not add an embedded-id element to the attributes"
						+ " because they already contain an id element.",
						getLocation(fieldOutline), classInfo.shortName, propInfo.getName(false));
				}
				else if (attributes.getEmbeddedId() != null)
				{
					CClassInfo classInfo = fieldOutline.parent().target;
					CPropertyInfo propInfo = fieldOutline.getPropertyInfo();
					getPlugin().error("{}, AttributesMapping: class={}, field={};"
						+ " Could not add an embedded-id element to the attributes"
						+ " bbecause they already contain an embedded-id element.",
						getLocation(fieldOutline), classInfo.shortName, propInfo.getName(false));
				}
				else
					attributes.setEmbeddedId((EmbeddedId) attributeMapping);
			}
			else if (attributeMapping instanceof Basic)
				attributes.getBasic().add((Basic) attributeMapping);
			else if (attributeMapping instanceof Version)
				attributes.getVersion().add((Version) attributeMapping);
			else if (attributeMapping instanceof ManyToOne)
				attributes.getManyToOne().add((ManyToOne) attributeMapping);
			else if (attributeMapping instanceof OneToMany)
				attributes.getOneToMany().add((OneToMany) attributeMapping);
			else if (attributeMapping instanceof OneToOne)
				attributes.getOneToOne().add((OneToOne) attributeMapping);
			else if (attributeMapping instanceof ManyToMany)
				attributes.getManyToMany().add((ManyToMany) attributeMapping);
			else if (attributeMapping instanceof ElementCollection)
				attributes.getElementCollection().add((ElementCollection) attributeMapping);
			else if (attributeMapping instanceof Embedded)
				attributes.getEmbedded().add((Embedded) attributeMapping);
			else if (attributeMapping instanceof Transient)
				attributes.getTransient().add((Transient) attributeMapping);
		}
		return attributes;
	}

	public FieldOutlineMapping<?> getAttributeMapping(Mapping context, FieldOutline fieldOutline)
	{
		if (context.getIgnoring().isFieldOutlineIgnored(context, fieldOutline))
			return context.getTransientMapping();
		else if (isFieldOutlineId(fieldOutline))
			return context.getIdMapping();
		else if (isFieldOutlineVersion(fieldOutline))
			return context.getVersionMapping();
		else
		{
			final String location = getLocation(fieldOutline);
			final CClassInfo classInfo = fieldOutline.parent().target;
			final CPropertyInfo propInfo = fieldOutline.getPropertyInfo();
			if (!propInfo.isCollection())
			{
				final Collection<? extends CTypeInfo> types = context.getGetTypes()
					.process(context, propInfo);
				
				if (types.size() == 1)
				{
					if (isFieldOutlineBasic(context, fieldOutline))
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; basic single field.",
							location, classInfo.shortName, propInfo.getName(false));
						return context.getBasicMapping();
					}
					else if (isFieldOutlineComplex(context, fieldOutline))
					{
						if (isFieldOutlineEmbeddedId(context, fieldOutline))
						{
							getPlugin().trace("{}, getAttributeMapping: class={}, field={}; embedded-id complex single field.",
								location, classInfo.shortName, propInfo.getName(false));
							return context.getEmbeddedIdMapping();
						}
						else if (isFieldOutlineEmbedded(context, fieldOutline))
						{
							getPlugin().trace("{}, getAttributeMapping: class={}, field={}; embedded complex single field.",
								location, classInfo.shortName, propInfo.getName(false));
							return context.getEmbeddedMapping();
						}
						else
						{
							getPlugin().trace("{}, getAttributeMapping: class={}, field={}; complex single field.",
								location, classInfo.shortName, propInfo.getName(false));
							return context.getToOneMapping();
						}
					}
					else
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; single field.",
							location, classInfo.shortName, propInfo.getName(false));
					}
				}
				else
				{
					getPlugin().warn("{}, getAttributeMapping: class={}, field={}; heterogeneous single field.",
						location, classInfo.shortName, propInfo.getName(false));
				}
			}
			else
			{
				if (isFieldOutlineSingletypedHomogeneous(context, fieldOutline))
				{
					if (isFieldOutlineElementCollection(context, fieldOutline))
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; homogeneous collection field.",
							location, classInfo.shortName, propInfo.getName(false));
						return context.getElementCollectionMapping();
					}
					else if (isFieldOutlineComplex(context, fieldOutline))
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; complex homogeneous collection field.",
							location, classInfo.shortName, propInfo.getName(false));
						return context.getToManyMapping();
					}
					else
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; collection field.",
							location, classInfo.shortName, propInfo.getName(false));
					}
				}
				else if (isFieldOutlineMultitypedHomogeneous(context, fieldOutline))
				{
					if (isFieldOutlineComplex(context, fieldOutline))
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; complex multityped homogeneous collection field.",
							location, classInfo.shortName, propInfo.getName(false));
						return context.getToManyMapping();
					}
					else
					{
						getPlugin().trace("{}, getAttributeMapping: class={}, field={}; multityped homogeneous collection field.",
							location, classInfo.shortName, propInfo.getName(false));
					}
				}
				else
				{
					getPlugin().warn("{}, getAttributeMapping: class={}, field={}; heterogenous collection field.",
						location, classInfo.shortName, propInfo.getName(false));
				}
			}
			
			todo(format("%s, getAttributeMapping: class=%s, field=%s; could not be annotated. It will be made transient.",
				location, classInfo.shortName, propInfo.getName(false)));
			return context.getTransientMapping();
		}
	}

	public boolean isFieldOutlineId(FieldOutline fieldOutline)
	{
		return containsCustomization(fieldOutline, ID_ELEMENT_NAME);
	}

	public boolean isFieldOutlineVersion(FieldOutline fieldOutline)
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
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		if (types.size() == 1)
		{
			final CTypeInfo type = types.iterator().next();
			return (type instanceof CEnumLeafInfo);
		}
		else
			return false;
	}

	public boolean isFieldOutlineSingletypedHomogeneous(Mapping context, FieldOutline fieldOutline)
	{
		final Collection<? extends CTypeInfo> types =
			context.getGetTypes().process(context, fieldOutline.getPropertyInfo());
		return types.size() == 1;
	}

	public boolean isFieldOutlineMultitypedHomogeneous(Mapping context, FieldOutline fieldOutline)
	{
		return getCommonBaseTypeInfo(context, fieldOutline) != null;
	}

	public CTypeInfo getCommonBaseTypeInfo(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		return CTypeInfoUtils.getCommonBaseTypeInfo(types);
	}

	public boolean isFieldOutlineElementCollection(Mapping context, FieldOutline fieldOutline)
	{
		return isFieldOutlineCore2(context, fieldOutline) || isFieldOutlineEnumerated(context, fieldOutline);
	}

	public boolean isFieldOutlineCore2(Mapping context, FieldOutline fieldOutline)
	{
		final CTypeInfo type = getCommonBaseTypeInfo(context, fieldOutline);
		assert type != null;
		return JTypeUtils.isBasicType(type.toType(fieldOutline.parent().parent(), EXPOSED));
	}

	public boolean isFieldOutlineComplex(Mapping context, FieldOutline fieldOutline)
	{
		final CTypeInfo type = getCommonBaseTypeInfo(context, fieldOutline);
		assert type != null;
		return (type instanceof CClass);
	}

	public boolean isFieldOutlineEmbedded(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		assert types.size() == 1;
		final CTypeInfo type = types.iterator().next();
		return
			( (type instanceof CClass) && containsCustomization(fieldOutline, EMBEDDED_ELEMENT_NAME) ) ||
			( (type instanceof CClassInfo) && containsCustomization(((CClassInfo) type), EMBEDDABLE_ELEMENT_NAME) );
	}

	public boolean isFieldOutlineEmbeddedId(Mapping context, FieldOutline fieldOutline)
	{
		final Collection<? extends CTypeInfo> types =
			context.getGetTypes().process(context, fieldOutline.getPropertyInfo());
		assert types.size() == 1;
		final CTypeInfo type = types.iterator().next();
		return ( (type instanceof CClass) && containsCustomization(fieldOutline, EMBEDDED_ID_ELEMENT_NAME) );
	}

	private void todo(String comment)
	{
		String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
		String level = System.getProperty(TODO_LOG_LEVEL);
		if ("DEBUG".equalsIgnoreCase(level)) getPlugin().debug(msg);
		else if ("INFO".equalsIgnoreCase(level)) getPlugin().info(msg);
		else if ("WARN".equalsIgnoreCase(level)) getPlugin().warn(msg);
		else if ("ERROR".equalsIgnoreCase(level)) getPlugin().error(msg);
		else getPlugin().error(msg);
	}
}
