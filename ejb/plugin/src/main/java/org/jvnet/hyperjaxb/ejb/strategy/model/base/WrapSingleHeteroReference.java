package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static com.sun.tools.xjc.model.CBuiltinLeafInfo.STRING;
import static com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode.NOT_REPEATED;
import static com.sun.tools.xjc.model.TypeUseFactory.adapt;
import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.glassfish.jaxb.core.v2.model.core.ID.NONE;
import static org.jvnet.basicjaxb.util.CustomizationUtils.addCustomization;
import static org.jvnet.basicjaxb.util.CustomizationUtils.getCustomizations;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.jpa.Customizations.BASIC_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.getContext;
import static org.jvnet.hyperjaxb.jpa.Customizations.markGenerated;
import static org.jvnet.hyperjaxb.jpa.Customizations.markIgnored;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.xml.namespace.QName;

import org.jvnet.basicjaxb.util.OutlineUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Basic;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.jpa.JaxbContext;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.ElementField;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleWrappingClassInfoField;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleWrappingReferenceElementInfoField;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleWrappingReferenceObjectField;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.ElementAsString;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassRef;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Lob;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapSingleHeteroReference implements CreatePropertyInfos
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		setPlugin(context.getPlugin());
		
		assert propertyInfo instanceof CReferencePropertyInfo;
		final CReferencePropertyInfo referencePropertyInfo = (CReferencePropertyInfo) propertyInfo;
		assert !referencePropertyInfo.isMixed();
		// if (referencePropertyInfo.getElements().isEmpty()) {
		final CPropertyInfo elementProperty = createElementProperty(referencePropertyInfo);
		final CPropertyInfo objectProperty = createObjectProperty(context, referencePropertyInfo);
		final Collection<CPropertyInfo> newPropertyInfos = new ArrayList<CPropertyInfo>(
			context.getGetTypes().getElements(context, referencePropertyInfo).size() + 3);
		if (elementProperty != null)
			newPropertyInfos.add(elementProperty);
		if (objectProperty != null)
			newPropertyInfos.add(objectProperty);
		final Collection<CPropertyInfo> properties = createElementProperties(context,
			(CReferencePropertyInfo) propertyInfo);
		if (properties != null)
			newPropertyInfos.addAll(properties);
		markIgnored(propertyInfo);
		return newPropertyInfos;
	}

	protected CPropertyInfo createObjectProperty(final ProcessModel context,
		final CReferencePropertyInfo referencePropertyInfo)
	{
		final CPropertyInfo objectProperty;
		if (referencePropertyInfo.getWildcard() != null && referencePropertyInfo.getWildcard().allowTypedObject)
		{
			objectProperty = new CAttributePropertyInfo
				(
					referencePropertyInfo.getName(true) + "Object",
					referencePropertyInfo.getSchemaComponent(),
					new CCustomizations(),
					referencePropertyInfo.getLocator(),
					new QName(referencePropertyInfo.getName(true) + "Object"),
					STRING,
					STRING.getTypeName(),
					false
				);
			objectProperty.realization = new FieldRenderer()
			{
				@Override
				public FieldOutline generate(ClassOutlineImpl classOutline, CPropertyInfo prop)
				{
					final JaxbContext jaxbContext = context.getCustomizing().getJaxbContext(prop);
					final String contextPath = (jaxbContext == null	|| jaxbContext.getContextPath() == null)
						? OutlineUtils.getContextPath(classOutline.parent())
						: jaxbContext.getContextPath();
					final boolean _final = (jaxbContext == null || jaxbContext.getField() == null || jaxbContext.getField().isFinal() == null)
						? true
						: jaxbContext.getField().isFinal();
					final SingleWrappingReferenceObjectField fieldOutline = new SingleWrappingReferenceObjectField(
						classOutline, prop, referencePropertyInfo, contextPath, _final);
					fieldOutline.generateAccessors();
					return fieldOutline;
				}
			};
			final Basic basic = new Basic();
			basic.setLob(new Lob());
			addCustomization(objectProperty, Customizations.getContext(), BASIC_ELEMENT_NAME, basic);
			markGenerated(objectProperty);
		}
		else
			objectProperty = null;
		return objectProperty;
	}

	protected CPropertyInfo createElementProperty(final CReferencePropertyInfo referencePropertyInfo)
	{
		final CAttributePropertyInfo elementProperty;
		if (referencePropertyInfo.getWildcard() != null && referencePropertyInfo.getWildcard().allowDom)
		{
			elementProperty = new CAttributePropertyInfo
				(
					referencePropertyInfo.getName(true) + "Element",
					referencePropertyInfo.getSchemaComponent(),
					new CCustomizations(),
					referencePropertyInfo.getLocator(),
					new QName(referencePropertyInfo.getName(true) + "Element"),
					adapt(STRING, ElementAsString.class, false),
					STRING.getTypeName(),
					false
				);
			elementProperty.realization = new FieldRenderer()
			{
				@Override
				public FieldOutline generate(ClassOutlineImpl context, CPropertyInfo prop)
				{
					ElementField fieldOutline = new ElementField(context, prop, referencePropertyInfo);
					fieldOutline.generateAccessors();
					return fieldOutline;
				}
			};
			final Basic basic = new Basic();
			basic.setLob(new Lob());
			addCustomization(elementProperty, getContext(), BASIC_ELEMENT_NAME, basic);
			markGenerated(elementProperty);
		}
		else
			elementProperty = null;
		return elementProperty;
	}

	protected Collection<CPropertyInfo> createElementProperties(ProcessModel context,
		final CReferencePropertyInfo propInfo)
	{
		final CClassInfo propClassInfo = (CClassInfo) propInfo.parent();
		Set<CElement> elements = context.getGetTypes().getElements(context, propInfo);
		final Collection<CPropertyInfo> properties = new ArrayList<CPropertyInfo>(elements.size());
		for (CElement element : elements)
		{
			final CElementPropertyInfo itemPropertyInfo = new CElementPropertyInfo
				(
					propInfo.getName(true) + propClassInfo.model.getNameConverter()
						.toPropertyName(element.getElementName().getLocalPart()),
					NOT_REPEATED,
					NONE,
					propInfo.getExpectedMimeType(),
					propInfo.getSchemaComponent(),
					new CCustomizations(getCustomizations(propInfo)),
					propInfo.getLocator(),
					false
				);
			if (element instanceof CElementInfo)
			{
				final CElementInfo elementInfo = (CElementInfo) element;
				if (!elementInfo.getSubstitutionMembers().isEmpty())
				{
					getPlugin().error("{}, createElementProperties: class={}, prop={};"
						+ " single hetero reference containing element [{}]"
						+ " which is a substitution group head. See issue #95.",
						toLocation(propInfo, propClassInfo), propClassInfo.shortName, propInfo.getName(false),
						elementInfo.getSqueezedName());
				}
				else
				{
					itemPropertyInfo.getTypes()
						.addAll(context.getGetTypes().getTypes(context, ((CElementInfo) element).getProperty()));
					itemPropertyInfo.realization = new FieldRenderer()
					{
						@Override
						public FieldOutline generate(ClassOutlineImpl classOutline, CPropertyInfo p)
						{
							SingleWrappingReferenceElementInfoField field =
								new SingleWrappingReferenceElementInfoField(classOutline, p, propInfo, elementInfo);
							field.generateAccessors();
							return field;
						}
					};
					Customizations.markGenerated(itemPropertyInfo);
					properties.add(itemPropertyInfo);
				}
			}
			else if (element instanceof CClassInfo)
			{
				final CClassInfo elemClassInfo = (CClassInfo) element;
				final QName elementName = elemClassInfo.getElementName();
				final QName typeName = elemClassInfo.getTypeName();
				final CTypeRef typeRef = new CTypeRef(elemClassInfo, elementName, typeName, false, null);
				itemPropertyInfo.realization = new FieldRenderer()
				{
					@Override
					public FieldOutline generate(ClassOutlineImpl classOutline, CPropertyInfo p)
					{
						SingleWrappingClassInfoField field =
							new SingleWrappingClassInfoField(classOutline, p, propInfo, elemClassInfo);
						field.generateAccessors();
						return field;
					}
				};
				itemPropertyInfo.getTypes().add(typeRef);
				markGenerated(itemPropertyInfo);
				properties.add(itemPropertyInfo);
			}
			else if (element instanceof CClassRef)
			{
				final CClassRef classRef = (CClassRef) element;
				getPlugin().error("{}, createElementProperties: class={}, prop={};"
					+ " single hetero reference containing unsupported CClassRef element [{}]"
					+ " See issue #94.",
					toLocation(propInfo, propClassInfo), propClassInfo.shortName, propInfo.getName(false),
					classRef.fullName());
			}
		}
		return properties;
	}
}
