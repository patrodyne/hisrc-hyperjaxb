package org.jvnet.hyperjaxb.ejb.tests.sbml;

import static java.beans.Introspector.getBeanInfo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.jvnet.basicjaxb.lang.ClassUtils.findClasses;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sbml.sbml.level2.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanTest
{
	private Logger logger = LoggerFactory.getLogger(BeanTest.class);
	public Logger getLogger() { return logger; }
	
	@Test
	void testBeans() throws IntrospectionException, IOException
	{
		Set<Class<?>> beanClasses = findClasses(ObjectFactory.class.getPackage());
		beanClasses.remove(ObjectFactory.class);
		for ( Class<?> beanClass : beanClasses )
		{
			BeanInfo beanInfo = getBeanInfo(beanClass);
			BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
			getLogger().debug
			(
				"Bean: name = '{}', description = '{}'",
				beanDescriptor.getName(),
				beanDescriptor.getShortDescription()

			);
			assertNotNull(beanDescriptor.getName(), "name");
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
			{
				String name = propertyDescriptor.getName();
				if ( !("class".equals(name) || "declaringClass".equals(name)
					|| name.startsWith("set") || name.startsWith("unset")) )
				{
					getLogger().debug
					(
						"Bean Property: name = '{}', type = '{}', accessor = '{}', mutator = '{}'",
						name,
						propertyDescriptor.getPropertyType(),
						propertyDescriptor.getReadMethod(),
						propertyDescriptor.getWriteMethod()
					);
					assertNotNull(name, "name");
					assertNotNull(propertyDescriptor.getPropertyType(), "type");
					assertNotNull(propertyDescriptor.getReadMethod(), "accessor");
					assertNotNull(propertyDescriptor.getWriteMethod(), "mutator");
				}
			}
		}
	}
}
