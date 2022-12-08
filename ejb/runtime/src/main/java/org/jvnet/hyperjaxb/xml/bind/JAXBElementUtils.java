package org.jvnet.hyperjaxb.xml.bind;

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

/**
 * Utility class of static methods for JAXBElement objects and/or their
 * QName and value properties.
 */
public class JAXBElementUtils
{
	/**
	 * Static methods, no constructor.
	 */
	private JAXBElementUtils() { }

	/**
	 * Downcast an element to its generic base type.
	 * 
	 * @param <T> The element's generic type.
	 * @param element A reference JAXBElement instance.
	 * 
	 * @return A downcast of element.
	 */
	private static <T> JAXBElement<T> elementBase(JAXBElement<? extends T> element)
	{
		@SuppressWarnings("unchecked")
		JAXBElement<T> elementBase = ((JAXBElement<T>) element);
		return elementBase;
	}
	
	/**
	 * Return a generically typed class for the value and its generic type.
	 * 
	 * @param <T> The value's generic type.
	 * @param value A value to be wrapped.
	 * 
	 * @return The generically typed class for the value and its generic type.
	 */
	@SuppressWarnings("unchecked")
	private static <T> Class<T> valueClass(T value)
	{
		return (Class<T>) value.getClass();
	}

	/**
	 * Determine when a value should be wrapped in a JAXBElenent.
	 * 
	 * @param <T> The element's generic type.
	 * @param element A reference JAXBElement instance.
	 * @param name A QName for the value.
	 * @param value The object to be wrapped.
	 * 
	 * @return True when the value and its name do not match the reference element.
	 */
	public static <T> boolean shouldBeWrapped(JAXBElement<? extends T> element, String name, T value)
	{
		if (element != null)
			return ( (element.getValue() != value) || !element.getName().toString().equals(name) );
		else
			return value != null;
	}

	/**
	 * Wrap a value in a JAXBElement. If the value is null then return null.
	 * If the reference element is null then wrap the value in a new JAXBElement
	 * with a "temp" QName; otherwise, update the reference element's value with
	 * the given value.
	 * 
	 * @param <T> The element's generic type.
	 * @param element A reference JAXBElement instance.
	 * @param value The object to be wrapped.
	 *
	 * @return A new JAXBElement with the given value or the reference with updated value.
	 */
	public static <T> JAXBElement<T> wrap(JAXBElement<? extends T> element, T value)
	{
		if (value == null)
			return null;
		else
		{
			if (element == null)
				return new JAXBElement<T>(new QName("temp"), valueClass(value), value);
			else
			{
				JAXBElement<T> elementBase = elementBase(element);
				elementBase.setValue(value);
				return elementBase;
			}
		}
	}

	/**
	 * Return a JAXBElement with the given declared type and reference value.
	 * If name is null return null. If the reference element is null then return
	 * a new element with the given name, type and null value. If the given name
	 * matches the reference name then return the down-casted element; otherwise,
	 * return a new element with the given name and the reference element's parameters.
	 * 
	 * @param <T> The element's generic type.
	 * @param element A reference JAXBElement instance.
	 * @param name A QName for the value.
	 * @param declaredType A declared type for the JAXBElement.
	 * 
	 * @return A JAXBElement with the given declared type and reference value.
	 */
	public static <T> JAXBElement<T> wrap(JAXBElement<? extends T> element, String name, Class<T> declaredType)
	{
		QName qName = QName.valueOf(name);
		if (name == null)
			return null;
		else
		{
			if (element == null)
				return new JAXBElement<T>(qName, declaredType, null);
			else if (element.getName().equals(qName))
				return elementBase(element);
			else
			{
				JAXBElement<T> elementBase = elementBase(element);
				return new JAXBElement<T>(qName, elementBase.getDeclaredType(), elementBase.getValue());
			}
		}
	}

	/**
	 * Return a JAXBElement for the given reference element, name and value.
	 * If the name or value is null return null; otherwise, if the reference element
	 * is not null and its parameters match the given name and value then return the 
	 * reference element with the updated value; otherwise, or if the reference element
	 * is null return a new JAXBElement with the given parameters.  
	 * 
	 * @param <T> The element's generic type.
	 * @param element A reference JAXBElement instance.
	 * @param name A QName for the value.
	 * @param value The object to be wrapped.
	 * 
	 * @return a JAXBElement for the given reference element, name and value.
	 */
	public static <T> JAXBElement<T> wrap(JAXBElement<? extends T> element, String name, T value)
	{
		if (name == null || value == null)
			return null;
		else
		{
			QName qName = QName.valueOf(name);
			Class<T> valueClass = valueClass(value);
			if (element != null)
			{
				if (element.getName().equals(qName) && element.getDeclaredType() == valueClass)
				{
					JAXBElement<T> elementBase = elementBase(element);
					elementBase.setValue(value);
					return elementBase;
				}
				else
					return new JAXBElement<T>(qName, valueClass, value);
			}
			else
				return new JAXBElement<T>(qName, valueClass, value);
		}
	}

	/**
	 * Return the QName of the item as a String.
	 * 
	 * @param <T> The generic declared type.
	 * @param item The item to act on.
	 * 
	 * @return The QName of the item as a String.
	 */
	public static <T> String getName(JAXBElement<? extends T> item)
	{
		if (item == null)
			return null;
		else
			return item.getName().toString();
	}
	
	/**
	 * Return the QName of the source as a String.
	 * This method handles the unchecked type conversion centrally, here.
	 * 
	 * @param <T> The generic declared type.
	 * @param declaredClass The declared type.
	 * @param source The item to act on.
	 * 
	 * @return The QName of the source as a String.
	 */
	@SuppressWarnings("unchecked")
	public static <T> String getName(Class<T> declaredClass, Object source)
	{
		String name = null;
		if ( (declaredClass != null) && (source != null)  )
		{
			if ( source instanceof JAXBElement )
				name = getName((JAXBElement<T>) source);
		}
		return name;
	}

	/**
	 * Return the value of the item as a declared generic type.
	 * 
	 * @param <T> The generic declared type.
	 * @param item The item to act on.
	 * 
	 * @return The value of the item as a declared generic type.
	 */
	public static <T> T getValue(JAXBElement<? extends T> item)
	{
		if (item == null)
			return null;
		else
			return item.getValue();
	}
	
	/**
	 * Return the value of the source as a declared generic type.
	 * This method handles the unchecked type conversion centrally, here.
	 * 
	 * @param <T> The generic declared type.
	 * @param declaredClass The declared type.
	 * @param source The item to act on.
	 * 
	 * @return The value of the source as a declared generic type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Class<T> declaredClass, Object source)
	{
		T value = null;
		if ( (declaredClass != null) && (source != null)  )
		{
			if ( source instanceof JAXBElement )
				value = getValue((JAXBElement<T>) source);
		}
		return value;
	}

}
