package org.jvnet.hyperjaxb.ejb.test.tests;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

/**
 * Test using Java reflection to invoke Hibernate's XClass 
 * getDeclaredProperties method.
 */
public class JavaReflectionManagerTest
{
	private static final String HIB_PKG = "org.hibernate.annotations.common.reflection.java";
	
	private Class<?> reflectionManagerClass = null;
	protected Class<?> getReflectionManagerClass()
	{
		try
		{
			reflectionManagerClass = Class.forName(HIB_PKG+".JavaReflectionManager");
		}
		catch (ClassNotFoundException e)
		{
			// OK: Presume Hibernate is not the current ORM provider.
		}
		return reflectionManagerClass;
	}
	
	private Method toXClassMethod = null;
	protected Method getToXClassMethod()
	{
		try
		{
			toXClassMethod = getReflectionManagerClass().getMethod("toXClass", Class.class);
			toXClassMethod.setAccessible(true);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// OK: Presume Hibernate is not the current ORM provider.
		}
		return toXClassMethod;
	}
	
	private Method getDeclaredPropertiesMethod = null;
	protected Method getGetDeclaredPropertiesMethod(Object xClass)
	{
		if ( xClass != null )
		{
			try
			{
				getDeclaredPropertiesMethod = xClass.getClass()
					.getMethod("getDeclaredProperties", String.class);
				getDeclaredPropertiesMethod.setAccessible(true);
			}
			catch (NoSuchMethodException | SecurityException e)
			{
				// OK: Presume Hibernate is not the current ORM provider.
			}
		}
		return getDeclaredPropertiesMethod;
	}

	@Test
	public void testXClass()
		throws Exception
	{
//		final JavaReflectionManager manager = new JavaReflectionManager();
//		final XClass cClass = manager.toXClass(TestMe.class);
//		cClass.getDeclaredProperties(XClass.ACCESS_FIELD);
//		cClass.getDeclaredProperties(XClass.ACCESS_PROPERTY);
		if ( getReflectionManagerClass() != null )
		{
			if ( getToXClassMethod() != null )
			{
				Object rm = getReflectionManagerClass().getConstructor().newInstance();
				Object xClass = getToXClassMethod().invoke(rm, TestMe.class);
				if ( getGetDeclaredPropertiesMethod(xClass) != null )
				{
					getGetDeclaredPropertiesMethod(xClass).invoke(xClass, "field");
					getGetDeclaredPropertiesMethod(xClass).invoke(xClass, "property");
				}
			}
		}
	}

	public static class TestMe
	{
		private Generic<Object[]> e;
		public Generic<Object[]> getE()
		{
			return e;
		}
		public void setE(Generic<Object[]> e)
		{
			this.e = e;
		}

		private Generic<Object> f;
		public Generic<Object> getF()
		{
			return f;
		}
		public void setF(Generic<Object> f)
		{
			this.f = f;
		}
	}

	public static class Generic<T>
	{
	}
}
