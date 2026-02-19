package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsDateTime extends AbstractXMLGregorianCalendarAdapter<Date>
{
	@Override
	public Timestamp createBoundValue(XMLGregorianCalendar calendar)
	{
		final GregorianCalendar gcal = calendar.normalize().toGregorianCalendar();
		final Timestamp timestamp = new Timestamp(gcal.getTimeInMillis());
		return timestamp;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void createCalendar(Date date, XMLGregorianCalendar calendar)
	{
		calendar.setYear(date.getYear() + 1900);
		calendar.setMonth(date.getMonth() + 1);
		calendar.setDay(date.getDate());
		calendar.setHour(date.getHours());
		calendar.setMinute(date.getMinutes());
		calendar.setSecond(date.getSeconds());
		calendar.setMillisecond((int) (date.getTime() % 1000));
	}
}
