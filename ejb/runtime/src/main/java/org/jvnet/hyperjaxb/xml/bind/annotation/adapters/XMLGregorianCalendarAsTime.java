package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsTime extends AbstractXMLGregorianCalendarAdapter<Date>
{
	@Override
	public Timestamp createBoundValue(XMLGregorianCalendar calendar)
	{
		final GregorianCalendar gc = calendar.normalize().toGregorianCalendar();
		final Timestamp time = new Timestamp(gc.getTimeInMillis());
		return time;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void createCalendar(Date date, XMLGregorianCalendar calendar)
	{
		calendar.setHour(date.getHours());
		calendar.setMinute(date.getMinutes());
		calendar.setSecond(date.getSeconds());
		calendar.setMillisecond((int) (date.getTime() % 1000));
	}
}
