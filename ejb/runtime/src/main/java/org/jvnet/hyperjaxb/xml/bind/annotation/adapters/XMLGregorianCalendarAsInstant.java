package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.Instant;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsInstant extends AbstractXMLGregorianCalendarAdapter<Instant>
{
	@Override
	public Instant createBoundValue(XMLGregorianCalendar calendar)
	{
		final GregorianCalendar gcal = calendar.normalize().toGregorianCalendar();
		return Instant.ofEpochMilli(gcal.getTimeInMillis());
	}

	@Override
	public void createCalendar(Instant instant, XMLGregorianCalendar calendar)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(instant.toEpochMilli());
		setYear(gc, calendar);
		setMonth(gc, calendar);
		setDay(gc, calendar);
		setHour(gc, calendar);
		setMinute(gc, calendar);
		setSecond(gc, calendar);
		setMillisecond(gc, calendar);
	}
}
