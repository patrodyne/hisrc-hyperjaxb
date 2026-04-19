package org.jvnet.hyperjaxb.annotation.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import org.jvnet.basicjaxb_annox.model.XAnnotation;
import org.jvnet.basicjaxb_annox.model.annotation.field.XAnnotationField;
import org.jvnet.basicjaxb_annox.model.annotation.field.XArrayAnnotationField;
import org.jvnet.basicjaxb_annox.model.annotation.field.XSingleAnnotationField;
import org.jvnet.basicjaxb_annox.model.annotation.value.XBooleanAnnotationValue;
import org.jvnet.basicjaxb_annox.model.annotation.value.XClassByNameAnnotationValue;
import org.jvnet.basicjaxb_annox.model.annotation.value.XEnumAnnotationValue;
import org.jvnet.basicjaxb_annox.model.annotation.value.XIntAnnotationValue;
import org.jvnet.basicjaxb_annox.model.annotation.value.XStringAnnotationValue;
import org.jvnet.basicjaxb_annox.model.annotation.value.XXAnnotationAnnotationValue;

public class AnnotationUtils
{
	public static <A extends Annotation> XAnnotationField<A> create(final String name, final XAnnotation<A> value)
	{
		if ( value == null )
			return null;
		else
		{
			return new XSingleAnnotationField<A>(name, value.getAnnotationClass(),
				new XXAnnotationAnnotationValue<A>(value));
		}
	}

	public static XAnnotationField<String> create(final String name, final String value)
	{
		if ( value == null )
			return null;
		else
			return new XSingleAnnotationField<String>(name, String.class, new XStringAnnotationValue(value));
	}

	public static XAnnotationField<Boolean> create(final String name, final Boolean value)
	{
		if ( value == null )
			return null;
		else
			return new XSingleAnnotationField<Boolean>(name, Boolean.class, new XBooleanAnnotationValue(value));
	}

	public static XAnnotationField<Integer> create(final String name, final Integer value)
	{
		if ( value == null )
			return null;
		else
			return new XSingleAnnotationField<Integer>(name, Integer.class, new XIntAnnotationValue(value));
	}

	public static <E extends Enum<E>> XAnnotationField<E> create(final String name, final E value)
	{
		if ( value == null )
			return null;
		else
			return new XSingleAnnotationField<E>(name, value.getClass(), new XEnumAnnotationValue<E>(value));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Annotation> XAnnotationField<Annotation[]> create(final String name,
		final XAnnotation<?>[] value, Class<T> annotationClass)
	{
		if ( (value == null) || (value.length == 0) )
			return null;
		else
		{
			final XXAnnotationAnnotationValue<Annotation>[] values = new XXAnnotationAnnotationValue[value.length];
			for ( int index = 0; index < value.length; index++ )
				values[index] = new XXAnnotationAnnotationValue(value[index]);
			return new XArrayAnnotationField<Annotation>(name, Array.newInstance(annotationClass, 0).getClass(),
				values);
		}
	}

	public static <E extends Enum<E>> XAnnotationField<E[]> create(final String name, final E[] value)
	{
		if ( (value == null) || (value.length == 0) )
			return null;
		else
		{
			@SuppressWarnings("unchecked")
			final XEnumAnnotationValue<E>[] values = new XEnumAnnotationValue[value.length];
			for ( int index = 0; index < value.length; index++ )
				values[index] = new XEnumAnnotationValue<E>(value[index]);
			return new XArrayAnnotationField<E>(name, value.getClass(), values);
		}
	}

	public static XAnnotationField<String[]> create(final String name, final String[] value)
	{
		if ( (value == null) || (value.length == 0) )
			return null;
		else
		{
			final XStringAnnotationValue[] values = new XStringAnnotationValue[value.length];
			for ( int index = 0; index < value.length; index++ )
				values[index] = new XStringAnnotationValue(value[index]);
			return new XArrayAnnotationField<String>(name, String[].class, values);
		}
	}

	public static XSingleAnnotationField<Class<Object>> createClass(String name, String value)
	{
		return value == null ? null :
			new XSingleAnnotationField<Class<Object>>(name, Class.class,
				new XClassByNameAnnotationValue<Object>(value));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static XArrayAnnotationField<Class<Object>> createClass(String name, String[] value)
	{
		if ( (value == null) || (value.length == 0) )
			return null;
		else
		{
			final XClassByNameAnnotationValue[] values =
				new XClassByNameAnnotationValue[value.length];
			for (int index = 0; index < value.length; index++)
				values[index] = new XClassByNameAnnotationValue(value[index]);
			return new XArrayAnnotationField<Class<Object>>(name, Class[].class, values);
		}
	}

	public static XArrayAnnotationField<Class<Object>> createClass(String name, List<String> value)
	{
		if ( (value == null) || value.isEmpty() )
			return null;
		else
			return createClass(name, value.toArray(new String[value.size()]));
	}
}
