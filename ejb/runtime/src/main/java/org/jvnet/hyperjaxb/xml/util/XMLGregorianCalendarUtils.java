package org.jvnet.hyperjaxb.xml.util;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Utility methods for XMLGregorianCalendar.
 */
public class XMLGregorianCalendarUtils
{
	/**
	 * Get the time in milliseconds for a given {@link XMLGregorianCalendar}.
	 * 
	 * @param calendar The {@link XMLGregorianCalendar} instance.
	 * 
	 * @return The time in milliseconds.
	 */
	public static long getTimeInMillis(XMLGregorianCalendar calendar)
	{
		return calendar.toGregorianCalendar().getTimeInMillis();
	}
}
