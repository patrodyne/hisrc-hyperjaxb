package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static org.jvnet.hyperjaxb.jpa.Customizations.NAMESPACE_URI;
import static org.jvnet.hyperjaxb.jpa.Customizations.markGenerated;

import java.util.Collection;
import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.Constants;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.GeneratedProperty;
import org.jvnet.hyperjaxb.xjc.model.DefaultTypeUse;
import org.w3c.dom.Element;

import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.xml.xsom.XSComponent;

public abstract class AbstractAdaptPropertyInfo implements CreatePropertyInfos
{
	public abstract TypeUse getPropertyType(ProcessModel context, CPropertyInfo propertyInfo);

	public abstract String getDefaultGeneratedPropertyName(ProcessModel context, CPropertyInfo propertyInfo);

	public abstract PropertyKind getDefaultGeneratedPropertyKind(ProcessModel context, CPropertyInfo propertyInfo);

	public CollectionMode getDefaultGeneratedPropertyCollectionMode(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return CollectionMode.NOT_REPEATED;
	}

	public final QName getDefaultGeneratedPropertyQName(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final String propertyName = getDefaultGeneratedPropertyName(context, propertyInfo);
		return new QName(Constants.NAMESPACE, propertyName);
	}

	public final CPropertyInfo createPropertyInfo(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final GeneratedProperty generatedProperty = context.getCustomizing().getGeneratedProperty(propertyInfo);
		
		final String wrappingPropertyName = ( (generatedProperty == null) || (generatedProperty.getPropertyName() == null) )
			? getDefaultGeneratedPropertyName(context, propertyInfo)
			: generatedProperty.getPropertyName();
		
		final QName wrappingPropertyQName = ( (generatedProperty == null) || (generatedProperty.getPropertyQName() == null) )
			? getDefaultGeneratedPropertyQName(context, propertyInfo)
			: generatedProperty.getPropertyQName();
		
		final CollectionMode wrappingPropertyCollectionMode =
			getDefaultGeneratedPropertyCollectionMode(context, propertyInfo);
		
		final PropertyKind wrappingPropertyKind;
		if ( (generatedProperty == null) || (generatedProperty.getPropertyKind() == null) )
			wrappingPropertyKind = getDefaultGeneratedPropertyKind(context, propertyInfo);
		else if ( "element".equals(generatedProperty.getPropertyKind()) )
			wrappingPropertyKind = PropertyKind.ELEMENT;
		else if ( "attribute".equals(generatedProperty.getPropertyKind()) )
			wrappingPropertyKind = PropertyKind.ATTRIBUTE;
		else
			wrappingPropertyKind = getDefaultGeneratedPropertyKind(context, propertyInfo);
	
		final Collection<CPluginCustomization> cPluginCustomizations = new LinkedList<CPluginCustomization>();
		
		final CCustomizations customizations = CustomizationUtils.getCustomizations(propertyInfo);
		if (customizations != null)
		{
			for (final CPluginCustomization customization : customizations)
			{
				if ( NAMESPACE_URI.equals(customization.element.getNamespaceURI()) )
					cPluginCustomizations.add(customization);
			}
		}
		
		if ( (generatedProperty != null) && !generatedProperty.getAny().isEmpty() )
		{
			for (Element element : generatedProperty.getAny())
				cPluginCustomizations.add(CustomizationUtils.createCustomization(element, propertyInfo.getLocator()));
		}
		
		final CCustomizations wrappingPropertyCustomizations = new CCustomizations(cPluginCustomizations);
		final XSComponent source = getSchemaComponent(context, propertyInfo);
		final TypeUse propertyTypeInfo = getPropertyType(context, propertyInfo);
		final CPropertyInfo newPropertyInfo;
		
		if (PropertyKind.ELEMENT.equals(wrappingPropertyKind))
		{
			newPropertyInfo = createElementPropertyInfo(wrappingPropertyName, source, propertyTypeInfo,
				wrappingPropertyQName, wrappingPropertyCollectionMode, wrappingPropertyCustomizations);
		}
		else if (PropertyKind.ATTRIBUTE.equals(wrappingPropertyKind))
		{
			newPropertyInfo = createAttributePropertyInfo(wrappingPropertyName, source, propertyTypeInfo,
				wrappingPropertyQName, wrappingPropertyCollectionMode, wrappingPropertyCustomizations);
		}
		else
			throw new AssertionError("Unexpected property kind [" + wrappingPropertyKind + "].");
		
		markGenerated(newPropertyInfo);
		
		return newPropertyInfo;
	}

	public XSComponent getSchemaComponent(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return propertyInfo.getSchemaComponent();
	}

	public final CCustomizations createCustomizations(ProcessModel context, CPropertyInfo propertyInfo)
	{
		final CCustomizations customizations = CustomizationUtils.getCustomizations(propertyInfo);
		final CCustomizations newCustomizations = new CCustomizations();
		if (customizations != null)
		{
			for (final CPluginCustomization customization : customizations)
			{
				if (NAMESPACE_URI.equals(customization.element.getNamespaceURI()))
					newCustomizations.add(customization);
			}
		}
		return newCustomizations;
	}

	public CPropertyInfo createAttributePropertyInfo(String propertyName, XSComponent source, TypeUse propertyType,
		QName propertyQName, CollectionMode collectionMode, CCustomizations customizations)
	{
		final TypeUse typeUse = collectionMode.isRepeated()
			? new DefaultTypeUse(propertyType.getInfo(), true, propertyType.idUse(), propertyType.getExpectedMimeType(), propertyType.getAdapterUse())
			: propertyType;
		
		final CAttributePropertyInfo propertyInfo = new CAttributePropertyInfo(propertyName, source, customizations,
			null, propertyQName, typeUse, typeUse.getInfo().getTypeName(), false);
		
		return propertyInfo;
	}

	public CPropertyInfo createElementPropertyInfo(String propertyName, XSComponent source, TypeUse propertyType,
		QName propertyQName, CollectionMode collectionMode, CCustomizations customizations)
	{
		final CNonElement propertyTypeInfo = propertyType.getInfo();
		
		final CElementPropertyInfo propertyInfo = new CElementPropertyInfo(propertyName, collectionMode,
			propertyTypeInfo.idUse(), propertyTypeInfo.getExpectedMimeType(), source, customizations, null, true);
		
		final CTypeRef typeRef = new CTypeRef(propertyTypeInfo, propertyQName, propertyTypeInfo.getTypeName(), false, null);
		
		propertyInfo.setAdapter(propertyType.getAdapterUse());
		propertyInfo.getTypes().add(typeRef);
		
		return propertyInfo;
	}
}
