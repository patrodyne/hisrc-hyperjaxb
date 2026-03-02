package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.Instant;
import java.util.Calendar;
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
	public void mergeCalendar(Instant instant, XMLGregorianCalendar calendar)
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

	protected void setDay(Calendar source, XMLGregorianCalendar target)
	{
		target.setDay(source.get(Calendar.DAY_OF_MONTH));
	}

	protected void setMonth(Calendar source, XMLGregorianCalendar target)
	{
		target.setMonth(source.get(Calendar.MONTH) + 1);
	}

	protected void setYear(Calendar source, XMLGregorianCalendar target)
	{
		target.setYear(source.get(Calendar.YEAR));
	}

	protected void setHour(Calendar source, XMLGregorianCalendar target)
	{
		target.setHour(source.get(Calendar.HOUR_OF_DAY));
	}

	protected void setMinute(Calendar source, XMLGregorianCalendar target)
	{
		target.setMinute(source.get(Calendar.MINUTE));
	}

	protected void setSecond(Calendar source, XMLGregorianCalendar target)
	{
		target.setSecond(source.get(Calendar.SECOND));
	}

	protected void setMillisecond(Calendar source, XMLGregorianCalendar target)
	{
		final int millisecond = source.get(Calendar.MILLISECOND);
		if (millisecond != 0)
			target.setMillisecond(millisecond);
	}

}
