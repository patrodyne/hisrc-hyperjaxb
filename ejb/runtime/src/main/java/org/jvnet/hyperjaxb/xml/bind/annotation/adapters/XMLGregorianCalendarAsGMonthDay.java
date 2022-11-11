package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsGMonthDay extends XMLGregorianCalendarAsDate {

	@SuppressWarnings("deprecation")
	@Override
	public void createCalendar(Date date, XMLGregorianCalendar calendar) {
		calendar.setMonth(date.getMonth() + 1);
		calendar.setDay(date.getDate());
	}
	
}
