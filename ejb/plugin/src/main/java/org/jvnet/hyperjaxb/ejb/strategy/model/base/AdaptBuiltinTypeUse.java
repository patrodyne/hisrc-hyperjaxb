package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.AdaptTypeUse;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.xjc.model.CExternalLeafInfo;
import org.jvnet.hyperjaxb.xml.XMLConstants;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.DurationAsString;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.QNameAsString;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsDate;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsDateTime;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsGDay;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsGMonth;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsGMonthDay;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsGYear;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsGYearMonth;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsTime;
import org.jvnet.hyperjaxb.xsom.TypeUtils;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;
import com.sun.xml.xsom.XSComponent;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

/**
 * Adapt the TypeUse for a schema component by processing the Model context and
 * the property info. 
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class AdaptBuiltinTypeUse implements AdaptTypeUse
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	/**
	 * Process context and propertyInfo to adapt the TypeUse for a schema component.
	 * This method gets the current TypeUse from the context and the XSComponent
	 * from the propertyInfo. If there is an XSComponent instance, then a local
	 * adapter map is scanned for a matching PropertyType and the first adapted
	 * TypeUse is returned; otherwise, the current TypeUse from the context is
	 * returned.
	 */
	@Override
	public TypeUse process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		setPlugin(context.getPlugin());
		
		// propertyInfo.g
		final TypeUse type = context.getGetTypes().getTypeUse(context, propertyInfo);
		final XSComponent schemaComponent = propertyInfo.getSchemaComponent();
		if (schemaComponent != null)
		{
			final List<QName> typeNames = TypeUtils.getTypeNames(schemaComponent);
			for (QName typeName : typeNames)
			{
				final PropertyType propertyType = new PropertyType(type, typeName);
				if (adapters.containsKey(propertyType))
				{
					final TypeUse typeUse = adapters.get(propertyType);
					return typeUse;
				}
			}
			return adapters.get(new PropertyType(type));
		}
		else
			return adapters.get(new PropertyType(type));
	}
	
//	public TypeUse getType(ProcessModel context, CPropertyInfo propertyInfo)
//	{
//		final CTypeInfo type = propertyInfo.ref().iterator().next();
//		if (propertyInfo instanceof CAttributePropertyInfo || propertyInfo instanceof CValuePropertyInfo)
//		{
//			return TypeUseUtils.getTypeUse(propertyInfo);
//		}
//		else
//		{
//			if (type instanceof CBuiltinLeafInfo)
//			{
//				if (propertyInfo.getAdapter() != null)
//				{
//					return TypeUseFactory.adapt((CBuiltinLeafInfo) type,
//					propertyInfo.getAdapter());
//				}
//				else
//					return (CBuiltinLeafInfo) type;
//			}
//			else if (type instanceof CElementInfo)
//			{
//				final CElementInfo elementInfo = (CElementInfo) type;
//				return getType(context, elementInfo.getProperty());
//			}
//			else
//				throw new AssertionError("Unexpected type.");
//		}
//	}

	/**
	 * The map of PropertyType and TypeUse bindings.
	 */
	private Map<PropertyType, TypeUse> adapters = new HashMap<PropertyType, TypeUse>();
	{
		adapters.put(new PropertyType(CBuiltinLeafInfo.BASE64_BYTE_ARRAY), CBuiltinLeafInfo.BASE64_BYTE_ARRAY);
		// adapters.put(new PropertyType(CBuiltinLeafInfo.HEXBIN_BYTE_ARRAY),
		// CBuiltinLeafInfo.HEXBIN_BYTE_ARRAY);
		adapters.put(new PropertyType(CBuiltinLeafInfo.HEXBIN_BYTE_ARRAY),
			new CExternalLeafInfo(byte[].class, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "hexBinary"), null));
		adapters.put(new PropertyType(CBuiltinLeafInfo.BIG_DECIMAL), CBuiltinLeafInfo.BIG_DECIMAL);
		adapters.put(new PropertyType(CBuiltinLeafInfo.BIG_INTEGER), CBuiltinLeafInfo.BIG_INTEGER);
		adapters.put(new PropertyType(CBuiltinLeafInfo.BOOLEAN), CBuiltinLeafInfo.BOOLEAN);
		adapters.put(new PropertyType(CBuiltinLeafInfo.BYTE), CBuiltinLeafInfo.BYTE);
		adapters.put(new PropertyType(CBuiltinLeafInfo.DOUBLE), CBuiltinLeafInfo.DOUBLE);
		adapters.put(new PropertyType(CBuiltinLeafInfo.FLOAT), CBuiltinLeafInfo.FLOAT);
		adapters.put(new PropertyType(CBuiltinLeafInfo.INT), CBuiltinLeafInfo.INT);
		adapters.put(new PropertyType(CBuiltinLeafInfo.LONG), CBuiltinLeafInfo.LONG);
		adapters.put(new PropertyType(CBuiltinLeafInfo.SHORT), CBuiltinLeafInfo.SHORT);
		adapters.put(new PropertyType(CBuiltinLeafInfo.STRING), CBuiltinLeafInfo.STRING);
		adapters.put(new PropertyType(CBuiltinLeafInfo.ID),
			new CExternalLeafInfo(String.class, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ID"), null));
		adapters.put(new PropertyType(CBuiltinLeafInfo.NORMALIZED_STRING), new CExternalLeafInfo(String.class,
			new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "normalizedString"), null));
		adapters.put(new PropertyType(CBuiltinLeafInfo.TOKEN),
			new CExternalLeafInfo(String.class, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "token"), null));
		adapters.put(new PropertyType(CBuiltinLeafInfo.QNAME),
			TypeUseFactory.adapt(CBuiltinLeafInfo.STRING, QNameAsString.class, false));
		adapters.put(new PropertyType(CBuiltinLeafInfo.DURATION),
			TypeUseFactory.adapt(CBuiltinLeafInfo.STRING, DurationAsString.class, false));
		adapters.put(new PropertyType(CBuiltinLeafInfo.CALENDAR),
			new CExternalLeafInfo(Date.class, "dateTime", XMLGregorianCalendarAsDateTime.class));
		adapters.put(
			new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dateTime")),
			new CExternalLeafInfo(Date.class, "dateTime", XMLGregorianCalendarAsDateTime.class));
		adapters.put(
			new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType")),
			CBuiltinLeafInfo.CALENDAR);
		adapters.put(new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "date")),
			new CExternalLeafInfo(Date.class, "date", XMLGregorianCalendarAsDate.class));
		adapters.put(new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "time")),
			new CExternalLeafInfo(Date.class, "time", XMLGregorianCalendarAsTime.class));
		adapters.put(
			new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYearMonth")),
			new CExternalLeafInfo(Date.class, "gYearMonth", XMLGregorianCalendarAsGYearMonth.class));
		adapters.put(
			new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gYear")),
			new CExternalLeafInfo(Date.class, "gYear", XMLGregorianCalendarAsGYear.class));
		adapters.put(
			new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonthDay")),
			new CExternalLeafInfo(Date.class, "gMonthDay", XMLGregorianCalendarAsGMonthDay.class));
		adapters.put(new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gDay")),
			new CExternalLeafInfo(Date.class, "gDay", XMLGregorianCalendarAsGDay.class));
		adapters.put(
			new PropertyType(CBuiltinLeafInfo.CALENDAR, new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "gMonth")),
			new CExternalLeafInfo(Date.class, "gMonth", XMLGregorianCalendarAsGMonth.class));
	}

	/**
	 * A pojo to couple a QName with its TypeUse.
	 * 
	 * QName represents a qualified name as defined in the XML specifications.
	 * Information about how another type is referenced in the XJC model.
	 */
	private class PropertyType
	{
		private TypeUse typeUse;
		private QName typeName;

		/**
		 * Construct with a TypeUse only.
		 * @param typeUse Information about how another type is referenced in the XJC model.
		 */
		public PropertyType(TypeUse typeUse)
		{
			super();
			this.typeUse = typeUse;
			this.typeName = null;
		}

		/**
		 * Construct with a TypeUse and QName
		 * @param typeUse Information about how another type is referenced in the XJC model.
		 * @param typeName Represents a qualified name as defined in the XML specifications.
		 */
		public PropertyType(TypeUse typeUse, QName typeName)
		{
			super();
			this.typeUse = typeUse;
			this.typeName = typeName;
		}

		@Override
		public int hashCode()
		{
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + ((typeName == null) ? 0 : typeName.hashCode());
			result = PRIME * result + ((typeUse == null) ? 0 : typeUse.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final PropertyType other = (PropertyType) obj;
			if (typeName == null)
			{
				if (other.typeName != null)
					return false;
			}
			else if (!typeName.equals(other.typeName))
				return false;
			if (typeUse == null)
			{
				if (other.typeUse != null)
					return false;
			}
			else if (!typeUse.equals(other.typeUse))
				return false;
			return true;
		}
	}
}
