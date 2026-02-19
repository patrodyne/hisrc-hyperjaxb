package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.lang.reflect.InvocationTargetException;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb.item.Converter;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class XmlAdapterUtils
{
	public static <I, O> Converter<I, O> getConverter(Class<? extends XmlAdapter<O, I>> xmlAdapterClass)
	{
		return asConverter(getXmlAdapter(xmlAdapterClass));
	}

	public static <I, O> Converter<I, O> asConverter(XmlAdapter<O, I> adapter)
	{
		return new XmlAdapterConverter<I, O>(adapter);
	}

	public static <ValueType, BoundType> ValueType marshall(
		Class<? extends XmlAdapter<ValueType, BoundType>> xmlAdapterClass, BoundType v)
	{
		try
		{
			final XmlAdapter<ValueType, BoundType> xmlAdapter = getXmlAdapter(xmlAdapterClass);
			return xmlAdapter.marshal(v);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static <ValueType, BoundType> BoundType unmarshall(
		Class<? extends XmlAdapter<ValueType, BoundType>> xmlAdapterClass, ValueType v)
	{
		try
		{
			final XmlAdapter<ValueType, BoundType> xmlAdapter = getXmlAdapter(xmlAdapterClass);
			return xmlAdapter.unmarshal(v);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static <ValueType, BoundType> XmlAdapter<ValueType, BoundType> getXmlAdapter(
		Class<? extends XmlAdapter<ValueType, BoundType>> xmlAdapterClass)
	{
		try
		{
			final XmlAdapter<ValueType, BoundType> xmlAdapter = xmlAdapterClass.getDeclaredConstructor().newInstance();
			return xmlAdapter;
		}
		catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | SecurityException iaex)
		{
			throw new RuntimeException(iaex);
		}
	}

	public static <ValueType, BoundType> ValueType unmarshallJAXBElement(
		Class<? extends XmlAdapter<BoundType, ValueType>> xmlAdapterClass, JAXBElement<? extends BoundType> v)
	{
		try
		{
			if (v == null)
				return null;
			else
			{
				final XmlAdapter<BoundType, ValueType> xmlAdapter = getXmlAdapter(xmlAdapterClass);
				return xmlAdapter.unmarshal(v.getValue());
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static <BoundType> BoundType unmarshallJAXBElement(JAXBElement<? extends BoundType> v)
	{
		if (v == null)
			return null;
		else
			return v.getValue();
	}

	public static <ValueType, BoundType> ValueType unmarshallSource(Class<? extends XmlAdapter<BoundType, ValueType>> xmlAdapterClass,
		Class<BoundType> declaredType, Object source)
	{
		ValueType declaredInstance = null;
		try
		{
			if ( (declaredType != null) && (source != null) )
			{
				XmlAdapter<BoundType, ValueType> xmlAdapter = getXmlAdapter(xmlAdapterClass);
	    		if ( JAXBElement.class.isInstance(source) )
	    		{
	    			@SuppressWarnings("unchecked")
					JAXBElement<BoundType> element = (JAXBElement<BoundType>) source;
	    			if ( declaredType.isInstance(element.getValue()) )
	    			{
						declaredInstance = xmlAdapter.unmarshal(element.getValue());
	    			}
	    		}
	    		else if ( declaredType.isInstance(source) )
	    			declaredInstance = xmlAdapter.unmarshal(declaredType.cast(source));
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
    	return declaredInstance;
	}

	public static <BoundType> BoundType unmarshallSource(Class<BoundType> declaredType, Object source)
	{
		BoundType declaredInstance = null;
    	if ( (declaredType != null) && (source != null) )
    	{
    		if ( JAXBElement.class.isInstance(source) )
    		{
    			@SuppressWarnings("unchecked")
				JAXBElement<BoundType> element = (JAXBElement<BoundType>) source;
        		if ( declaredType.isInstance(element.getValue()) )
        			declaredInstance = declaredType.cast(element.getValue());
    		}
    		else if ( declaredType.isInstance(source) )
    			declaredInstance = declaredType.cast(source);
    	}
    	return declaredInstance;
	}

	public static <BoundType> BoundType unmarshallSource
	(
		Class<BoundType> declaredType,
		@SuppressWarnings("rawtypes") JAXBElement<? extends Comparable> source
	)
	{
		BoundType declaredInstance = null;
		if ( (declaredType != null) && (source != null) )
		{
			if ( declaredType.isInstance(source.getValue()) )
				declaredInstance = declaredType.cast(source.getValue());
		}
		return declaredInstance;
	}

	public static <BoundType> boolean isJAXBElement(Class<BoundType> declaredType, QName name, Class<?> scope,
		Object value)
	{
		if (value == null)
			return false;
		else if (value instanceof JAXBElement)
		{
			final JAXBElement<?> element = (JAXBElement<?>) value;
			return element.getName().equals(name) && declaredType.isAssignableFrom(element.getDeclaredType());
		}
		else
			return false;
	}

	public static <ValueType, BoundType> JAXBElement<BoundType> marshallJAXBElement
	(
		Class<? extends XmlAdapter<BoundType, ValueType>> xmlAdapterClass,
		Class<BoundType> declaredType,
		QName name,
		Class<?> scope,
		ValueType v
	)
	{
		try
		{
			if (v == null)
				return null;
			else
			{
				final XmlAdapter<BoundType, ValueType> xmlAdapter = getXmlAdapter(xmlAdapterClass);
				return new JAXBElement<BoundType>(name, declaredType, scope, xmlAdapter.marshal(v));
			}
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static <BoundType> JAXBElement<BoundType> marshallJAXBElement(Class<BoundType> declaredType, QName name,
		Class<?> scope, BoundType v)
	{
		if (v == null)
			return null;
		else
		{
			return new JAXBElement<BoundType>(name, declaredType, scope, v);
		}
	}
}
