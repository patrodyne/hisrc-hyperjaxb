package org.jvnet.hyperjaxb.ejb.tests.st;

import jakarta.persistence.Column;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

@Order(3)
public class HJIII11IssueTest {

	@Test
	public void testSimpleTypes() {
		final Class<?> theClass = Root123456789012345678901234567890Type.class;

		assertEquals(400, getColumn(theClass, "getLargeMinLength").length());
		assertEquals(4000, getColumn(theClass, "getLargeMaxLength").length());
		assertEquals(255, getColumn(theClass, "getMinLength").length());
		assertEquals(10, getColumn(theClass, "getMaxLength").length());
		assertEquals(8, getColumn(theClass, "getLength").length());
		assertEquals(7, getColumn(theClass, "getDigits").precision());
		assertEquals(2, getColumn(theClass, "getDigits").scale());
		assertEquals(6, getColumn(theClass, "getTotalDigits").precision());
		assertEquals(3, getColumn(theClass, "getTotalDigits").scale());
		assertEquals(12, getColumn(theClass, "getFractionDigits").precision());
		assertEquals(2, getColumn(theClass, "getFractionDigits").scale());
	}

	private Column getColumn(final Class<?> theClass, final String name)
	{
		try
		{
			return theClass.getMethod(name, new Class[0]).getAnnotation(Column.class);
		}
		catch (NoSuchMethodException nsmex)
		{
			throw new AssertionFailedError(nsmex.getMessage());
		}
	}

}
