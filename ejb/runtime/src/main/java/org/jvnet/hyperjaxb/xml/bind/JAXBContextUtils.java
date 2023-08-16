package org.jvnet.hyperjaxb.xml.bind;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.ElementAsString;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XmlAdapterUtils;
import org.w3c.dom.Element;

/**
 * Utility class for {@link JAXBContext} methods.
 */
public class JAXBContextUtils
{
	// Seal this utility class for static usage.
	private JAXBContextUtils() { }

	/**
	 * <p>Determine if the given <code>object</code> is an {@link Element}
	 * interface in an JAXB context. Note: The {@link Element} interface
	 * inherits from {@link org.w3c.dom.Node}.</p>
	 * 
	 * @param object The target to examine.
	 * 
	 * @return True, if the given <code>object</code> is an instance of {@link Element}.
	 */
	public static boolean isElement(Object object)
	{
		return object != null && (object instanceof Element);
	}

	/**
     * <p>Introspect on the given <code>object</code> to determine if it is a
     * Jakarta XML Binding element: {@link JAXBElement} or {@link XmlRootElement}.</p>
	 * 
	 * @param contextPath The {@link JAXBContext} path.
	 * @param object The target to introspect.
	 * 
	 * @return True, if <code>object</code> represents a Jakarta XML Binding element.
	 */
	public static boolean isBindingElement(String contextPath, Object object)
	{
		try
		{
			return object != null && createJAXBIntrospector(contextPath).isElement(object);
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Determine if the given <code>object</code> is either an XML schema {@link Element}
	 * or a JAXB XML Binding element.
	 * 
	 * @param contextPath The {@link JAXBContext} path.
	 * @param object The target to inspect.
	 * 
	 * @return True, if the <code>object</code> is an XML {@link Element} or a JAXB XML Binding element.
	 */
	public static boolean isMarshallable(String contextPath, Object object)
	{
		return isElement(object) || isBindingElement(contextPath, object);
	}

	public static String marshalElement(Object object)
	{
		return object != null && (object instanceof Element) ? XmlAdapterUtils.unmarshall(ElementAsString.class, (Element) object) : null;
	}

	public static Object unmarshalElement(String object)
	{
		return object == null ? null : XmlAdapterUtils.marshall(ElementAsString.class, object);
	}

	public static boolean isMarshallableObject(String contextPath, Object object)
	{
		try
		{
			return object != null && (createJAXBIntrospector(contextPath).isElement(object));
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static String marshalObject(String contextPath, Object object)
	{
		if (object != null)
		{
			try
			{
				final Marshaller marshaller = getJAXBContext(contextPath).createMarshaller();
				final StringWriter sw = new StringWriter();
				marshaller.marshal(object, sw);
				return sw.toString();
			}
			catch (JAXBException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		else
			return null;
	}

	public static Object unmarshalObject(String contextPath, String object)
	{
		if (object == null)
			return null;
		else
		{
			final Element element = XmlAdapterUtils.marshall(ElementAsString.class, object);
			try
			{
				final Unmarshaller unmarshaller = getJAXBContext(contextPath).createUnmarshaller();
				return unmarshaller.unmarshal(element);
			}
			catch (JAXBException ex)
			{
				return element;
			}
		}
	}

	public static String marshal(String contextPath, Object object)
	{
		try
		{
			return object == null ? null : marshal(getJAXBContext(contextPath), object);
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static String marshalJAXBElement(String contextPath, JAXBElement<Object> object)
	{
		if (object == null)
			return null;
		else
			return marshal(contextPath, object.getValue());
	}

	public static String marshal(JAXBContext context, Object object)
	{
		if (object == null)
			return null;
		else if (object instanceof Element)
			return XmlAdapterUtils.unmarshall(ElementAsString.class, (Element) object);
		else
		{
			try
			{
				Marshaller marshaller = context.createMarshaller();
				final StringWriter sw = new StringWriter();
				marshaller.marshal(object, sw);
				return sw.toString();
			}
			catch (JAXBException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	public static Object unmarshal(String contextPath, String string)
	{
		try
		{
			return string == null ? null : unmarshal(getJAXBContext(contextPath), string);
		}
		catch (JAXBException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private static Map<String, JAXBContext> contextCache = new HashMap<String, JAXBContext>();

	public static JAXBContext getJAXBContext(String contextPath)
		throws JAXBException
	{
		if (contextCache.containsKey(contextPath))
			return contextCache.get(contextPath);
		else
		{
			final JAXBContext context = JAXBContext.newInstance(contextPath);
			contextCache.put(contextPath, context);
			return context;
		}
	}

	public static JAXBElement<Object> unmarshalJAXBElement(String contextPath, QName name, Class<?> scope,
		String object)
	{
		if (object == null)
			return null;
		else
			return new JAXBElement<Object>(name, Object.class, scope, unmarshal(contextPath, object));
	}

	public static Object unmarshal(JAXBContext context, String object)
	{
		if (object == null)
			return null;
		else
		{
			final Element element = XmlAdapterUtils.marshall(ElementAsString.class, object);
			try
			{
				Unmarshaller unmarshaller = context.createUnmarshaller();
				final Object result = unmarshaller.unmarshal(element);
				if (result instanceof JAXBElement && Object.class.equals(((JAXBElement<?>) result).getDeclaredType()))
					return element;
				else
					return result;
			}
			catch (JAXBException ex)
			{
				return element;
			}
		}
	}

    /**
     * Creates a {@code JAXBIntrospector} object that can be used to
     * introspect Jakarta XML Binding objects.
     * 
	 * @param contextPath The JAXB context path.
     *
     * @return A non-null valid {@code JAXBIntrospector} object.
     * 
	 * @throws JAXBException If an error was encountered creating the {@code JAXBContext}.
     */
	private static JAXBIntrospector createJAXBIntrospector(String contextPath)
		throws JAXBException
	{
		return getJAXBContext(contextPath).createJAXBIntrospector();
	}

	public static String escalateNotNull(Object object, String msg)
	{
		if ( object == null )
			return null;
		else
			throw new RuntimeException(msg + object);
	}

}
