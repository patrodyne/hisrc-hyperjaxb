package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsDate extends AbstractXMLGregorianCalendarAdapter<Date>
{
	@Override
	public Date createBoundValue(XMLGregorianCalendar calendar)
	{
		return new Timestamp(calendar.normalize().toGregorianCalendar().getTimeInMillis());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void createCalendar(Date date, XMLGregorianCalendar calendar)
	{
		calendar.setYear(date.getYear() + 1900);
		calendar.setMonth(date.getMonth() + 1);
		calendar.setDay(date.getDate());
	}
}
