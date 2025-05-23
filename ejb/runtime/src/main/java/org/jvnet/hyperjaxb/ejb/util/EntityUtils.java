package org.jvnet.hyperjaxb.ejb.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.PersistenceUnitUtil;

public class EntityUtils
{
	private EntityUtils()
	{
	}

	public static Object getId(Object entity)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		return getId(null, entity);
	}

	public static Object getId(EntityManager entityManager, Object entity)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Object id = null;
		// New approach
		if ( entityManager != null )
		{
			EntityManagerFactory emf = entityManager.getEntityManagerFactory();
			PersistenceUnitUtil puu = emf.getPersistenceUnitUtil();
			id = puu.getIdentifier(entity);
		}
		if ( id != null )
			return id;
		else
		{
			// Old approach
			// Try annotations
			{
				final Collection<Field> atEmbeddedIdFields =
					getAnnotatedElements(entity.getClass().getFields(), EmbeddedId.class);
				
				final Collection<Method> atEmbeddedIdMethods =
					getAnnotatedElements(entity.getClass().getMethods(), EmbeddedId.class);
				
				final Collection<Field> atIdFields =
					getAnnotatedElements(entity.getClass().getFields(), Id.class);
				
				final Collection<Method> atIdMethods =
					getAnnotatedElements(getNonBridgedMethods(entity.getClass()), Id.class);
				
				if (atEmbeddedIdFields.size() > 0)
				{
					assert atEmbeddedIdFields.size() == 1 : "More than one field is annotated with @EmbeddedId.";
					final Field field = atEmbeddedIdFields.iterator().next();
					return get(entity, field);
				}
				else if (atEmbeddedIdMethods.size() > 0)
				{
					assert atEmbeddedIdMethods.size() == 1 : "More than one method is annotated with @EmbeddedId.";
					final Method method = atEmbeddedIdMethods.iterator().next();
					return get(entity, method);
				}
				else if (atIdFields.size() == 1)
				{
					return get(entity, atIdFields.iterator().next());
				}
				else if (atIdMethods.size() == 1)
				{
					return get(entity, atIdMethods.iterator().next());
				}
			}
			
			if (entityManager != null)
			{
				// Try Hibernate org.hibernate.ejb.AbstractEntityManagerImpl
				try
				{
					return getIdWithHibernate(entityManager, entity);
				}
				catch (Exception ignored)
				{
					// Well, it wasn't Hibernate
				}
			}
			
			throw new IllegalArgumentException("No id could be found.");
		}
	}

	// There may be more than one method with a particular name and parameter
	// types in a class because while the Java language forbids a class to
	// declare multiple methods with the same signature but different return
	// types, the Java virtual machine does not. All methods from the base
	// type that are extended/implemented by derived types using covariant
	// return types are marked as a "bridge" method.
	//
	// Note: Covariant methods are created when generics are used.
	//
	private static Method[] getNonBridgedMethods(Class<? extends Object> clazz)
	{
		ArrayList<Method> nbm = new ArrayList<>();
		for (Method method : clazz.getMethods())
		{
			if (!method.isBridge())
				nbm.add(method);
		}
		return nbm.toArray(new Method[nbm.size()]);
	}

	private static Object getIdWithHibernate(EntityManager entityManager, Object entity)
		throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		final Class<?> hibernateEntityManagerClass = Class.forName("org.hibernate.ejb.AbstractEntityManagerImpl");
		final Class<?> hibernateSessionClass = Class.forName("org.hibernate.Session");
		final Method getSession = hibernateEntityManagerClass.getMethod("getSession");
		final Method getIdentifier = hibernateSessionClass.getMethod("getIdentifier", Object.class);
		return getIdentifier.invoke(getSession.invoke(entityManager), entity);
	}

	private static <T extends AnnotatedElement> Collection<T> getAnnotatedElements(T[] elements,
		Class<? extends Annotation> annotationType)
	{
		final Collection<T> annotatedElements = new LinkedList<T>();
		for (final T element : elements)
		{
			if (element.isAnnotationPresent(annotationType))
			{
				annotatedElements.add(element);
			}
		}
		return annotatedElements;
	}

	private static Object get(Object target, Field field)
		throws IllegalArgumentException, IllegalAccessException
	{
		return field.get(target);
	}

	private static Object get(Object target, Method method)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		return method.invoke(target, (Object[]) null);
	}
}
