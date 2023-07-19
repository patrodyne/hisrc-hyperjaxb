package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static com.sun.tools.xjc.model.CBuiltinLeafInfo.BASE64_BYTE_ARRAY;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.BIG_DECIMAL;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.BIG_INTEGER;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.BOOLEAN;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.BYTE;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.CALENDAR;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.DOUBLE;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.DURATION;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.FLOAT;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.HEXBIN_BYTE_ARRAY;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.ID;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.INT;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.LONG;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.NORMALIZED_STRING;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.QNAME;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.SHORT;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.STRING;
import static com.sun.tools.xjc.model.CBuiltinLeafInfo.TOKEN;
import static com.sun.tools.xjc.model.TypeUseFactory.adapt;
import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.hyperjaxb.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.AdaptTypeUse;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.xjc.model.CExternalLeafInfo;
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

import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
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
	private static final QName QNAME_HEX_BINARY        = new QName(W3C_XML_SCHEMA_NS_URI, "hexBinary");
	private static final QName QNAME_ID                = new QName(W3C_XML_SCHEMA_NS_URI, "ID");
	private static final QName QNAME_NORMALIZED_STRING = new QName(W3C_XML_SCHEMA_NS_URI, "normalizedString");
	private static final QName QNAME_TOKEN             = new QName(W3C_XML_SCHEMA_NS_URI, "token");
	private static final QName QNAME_DATE_TIME         = new QName(W3C_XML_SCHEMA_NS_URI, "dateTime");
	private static final QName QNAME_ANY_SIMPLE_TYPE   = new QName(W3C_XML_SCHEMA_NS_URI, "anySimpleType");
	private static final QName QNAME_DATE              = new QName(W3C_XML_SCHEMA_NS_URI, "date");
	private static final QName QNAME_TIME              = new QName(W3C_XML_SCHEMA_NS_URI, "time");
	private static final QName QNAME_G_YEAR_MONTH      = new QName(W3C_XML_SCHEMA_NS_URI, "gYearMonth");
	private static final QName QNAME_G_YEAR            = new QName(W3C_XML_SCHEMA_NS_URI, "gYear");
	private static final QName QNAME_G_MONTH_DAY       = new QName(W3C_XML_SCHEMA_NS_URI, "gMonthDay");
	private static final QName QNAME_G_DAY             = new QName(W3C_XML_SCHEMA_NS_URI, "gDay");
	private static final QName QNAME_G_MONTH           = new QName(W3C_XML_SCHEMA_NS_URI, "gMonth");

	private static final CExternalLeafInfo CELI_HEX_BINARY        = new CExternalLeafInfo(byte[].class, QNAME_HEX_BINARY, null);
	private static final CExternalLeafInfo CELI_ID                = new CExternalLeafInfo(String.class, QNAME_ID, null);
	private static final CExternalLeafInfo CELI_NORMALIZED_STRING = new CExternalLeafInfo(String.class, QNAME_NORMALIZED_STRING, null);
	private static final CExternalLeafInfo CELI_TOKEN             = new CExternalLeafInfo(String.class, QNAME_TOKEN, null);
	private static final CExternalLeafInfo CELI_DATETIME          = new CExternalLeafInfo(Date.class, "dateTime", XMLGregorianCalendarAsDateTime.class);
	private static final CExternalLeafInfo CELI_DATE              = new CExternalLeafInfo(Date.class, "date", XMLGregorianCalendarAsDate.class);
	private static final CExternalLeafInfo CELI_TIME              = new CExternalLeafInfo(Date.class, "time", XMLGregorianCalendarAsTime.class);
	private static final CExternalLeafInfo CELI_G_YEAR_MONTH      = new CExternalLeafInfo(Date.class, "gYearMonth", XMLGregorianCalendarAsGYearMonth.class);
	private static final CExternalLeafInfo CELI_G_YEAR            = new CExternalLeafInfo(Date.class, "gYear", XMLGregorianCalendarAsGYear.class);
	private static final CExternalLeafInfo CELI_G_MONTH_DAY       = new CExternalLeafInfo(Date.class, "gMonthDay", XMLGregorianCalendarAsGMonthDay.class);
	private static final CExternalLeafInfo CELI_G_DAY             = new CExternalLeafInfo(Date.class, "gDay", XMLGregorianCalendarAsGDay.class);
	private static final CExternalLeafInfo CELI_G_MONTH           = new CExternalLeafInfo(Date.class, "gMonth", XMLGregorianCalendarAsGMonth.class);

	/**
	 * The map of PropertyType and TypeUse bindings.
	 */
	private Map<PropertyType, TypeUse> adapters = new HashMap<PropertyType, TypeUse>();
	{
		adapters.put(new PropertyType(BASE64_BYTE_ARRAY), BASE64_BYTE_ARRAY);
		// adapters.put(new PropertyType(HEXBIN_BYTE_ARRAY), HEXBIN_BYTE_ARRAY);
		adapters.put(new PropertyType(HEXBIN_BYTE_ARRAY), CELI_HEX_BINARY);
		adapters.put(new PropertyType(BIG_DECIMAL), BIG_DECIMAL);
		adapters.put(new PropertyType(BIG_INTEGER), BIG_INTEGER);
		adapters.put(new PropertyType(BOOLEAN), BOOLEAN);
		adapters.put(new PropertyType(BYTE), BYTE);
		adapters.put(new PropertyType(DOUBLE), DOUBLE);
		adapters.put(new PropertyType(FLOAT), FLOAT);
		adapters.put(new PropertyType(INT), INT);
		adapters.put(new PropertyType(LONG), LONG);
		adapters.put(new PropertyType(SHORT), SHORT);
		adapters.put(new PropertyType(STRING), STRING);
		adapters.put(new PropertyType(ID), CELI_ID);
		adapters.put(new PropertyType(NORMALIZED_STRING), CELI_NORMALIZED_STRING);
		adapters.put(new PropertyType(TOKEN), CELI_TOKEN);
		adapters.put(new PropertyType(QNAME), adapt(STRING, QNameAsString.class, false));
		adapters.put(new PropertyType(DURATION), adapt(STRING, DurationAsString.class, false));
		adapters.put(new PropertyType(CALENDAR), CELI_DATETIME);
		adapters.put(new PropertyType(CALENDAR, QNAME_DATE_TIME), CELI_DATETIME);
		adapters.put(new PropertyType(CALENDAR, QNAME_ANY_SIMPLE_TYPE), CALENDAR);
		adapters.put(new PropertyType(CALENDAR, QNAME_DATE), CELI_DATE);
		adapters.put(new PropertyType(CALENDAR, QNAME_TIME), CELI_TIME);
		adapters.put(new PropertyType(CALENDAR, QNAME_G_YEAR_MONTH), CELI_G_YEAR_MONTH);
		adapters.put(new PropertyType(CALENDAR, QNAME_G_YEAR), CELI_G_YEAR);
		adapters.put(new PropertyType(CALENDAR, QNAME_G_MONTH_DAY), CELI_G_MONTH_DAY);
		adapters.put(new PropertyType(CALENDAR, QNAME_G_DAY), CELI_G_DAY);
		adapters.put(new PropertyType(CALENDAR, QNAME_G_MONTH), CELI_G_MONTH);
	}

	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	/**
	 * Process context and propertyInfo to adapt the TypeUse for a schema
	 * component. This method gets the current TypeUse from the context and the
	 * XSComponent from the propertyInfo. If there is an XSComponent instance,
	 * then a local adapter map is scanned for a matching PropertyType and the
	 * first adapted TypeUse is returned; otherwise, the current TypeUse from
	 * the context is returned.
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
//			return TypeUseUtils.getTypeUse(propertyInfo);
//		else
//		{
//			if (type instanceof CBuiltinLeafInfo)
//			{
//				if (propertyInfo.getAdapter() != null)
//					return TypeUseFactory.adapt((CBuiltinLeafInfo) type, propertyInfo.getAdapter());
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
		 * 
		 * @param typeUse Information about how another type is referenced in
		 *                the XJC model.
		 */
		public PropertyType(TypeUse typeUse)
		{
			super();
			this.typeUse = typeUse;
			this.typeName = null;
		}

		/**
		 * Construct with a TypeUse and QName
		 * 
		 * @param typeUse Information about how another type is referenced in
		 *                the XJC model.
		 * @param typeName Represents a qualified name as defined in the XML
		 *                 specifications.
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
