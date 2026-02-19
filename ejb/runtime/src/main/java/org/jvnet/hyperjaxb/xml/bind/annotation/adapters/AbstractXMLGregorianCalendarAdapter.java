package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.util.Calendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractXMLGregorianCalendarAdapter<BoundType>
	extends XmlAdapter<XMLGregorianCalendar, BoundType>
{
	@Override
	public final BoundType unmarshal(XMLGregorianCalendar calendar)
		throws Exception
	{
		if (calendar == null)
			return null;
		else
		{
			// TODO Optimize
			// final java.util.TimeZone timeZone = XMLGregorianCalendarUtils.TIMEZONE_UTC;
			// return date.normalize().toGregorianCalendar(timeZone, Locale.getDefault(), null).getTime();
			return createBoundValue(calendar);
		}
	}

	public abstract BoundType createBoundValue(XMLGregorianCalendar calendar);

	@Override
	public final XMLGregorianCalendar marshal(BoundType date)
		throws Exception
	{
		if (date == null)
			return null;
		else
		{
			final XMLGregorianCalendar target = DatatypeFactory.newInstance().newXMLGregorianCalendar();
			createCalendar(date, target);
			return target;
		}
	}

	public abstract void createCalendar(BoundType date, XMLGregorianCalendar calendar);

	// public abstract void setFields(Calendar source, XMLGregorianCalendar target);

	public void setDay(Calendar source, XMLGregorianCalendar target)
	{
		target.setDay(source.get(Calendar.DAY_OF_MONTH));
	}

	public void setMonth(Calendar source, XMLGregorianCalendar target)
	{
		target.setMonth(source.get(Calendar.MONTH) + 1);
	}

	public void setYear(Calendar source, XMLGregorianCalendar target)
	{
		target.setYear(source.get(Calendar.YEAR));
	}

	public void setHour(Calendar source, XMLGregorianCalendar target)
	{
		target.setHour(source.get(Calendar.HOUR_OF_DAY));
	}

	public void setMinute(Calendar source, XMLGregorianCalendar target)
	{
		target.setMinute(source.get(Calendar.MINUTE));
	}

	public void setSecond(Calendar source, XMLGregorianCalendar target)
	{
		target.setSecond(source.get(Calendar.SECOND));
	}

	public void setMillisecond(Calendar source, XMLGregorianCalendar target)
	{
		final int millisecond = source.get(Calendar.MILLISECOND);
		if (millisecond != 0)
			target.setMillisecond(millisecond);
	}
}
