package org.jvnet.hyperjaxb.xml.datatype.util;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarUtils {

	public static long getTimeInMillis(XMLGregorianCalendar calendar) {
		// TODO optimize
		return calendar.toGregorianCalendar().getTimeInMillis();
	}
}
