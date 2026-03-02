package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsTime extends AbstractXMLGregorianCalendarAdapter<OffsetTime>
{
	@Override
	public OffsetTime createBoundValue(XMLGregorianCalendar xgc)
	{
		ZonedDateTime zdt = xgc.toGregorianCalendar().toZonedDateTime();
		return zdt.toOffsetDateTime().toOffsetTime();
	}

	@Override
	public void mergeCalendar(OffsetTime ot, XMLGregorianCalendar xgc)
	{
		xgc.setHour(ot.getHour());
		xgc.setMinute(ot.getMinute());
		xgc.setSecond(ot.getSecond());
		xgc.setMillisecond(ot.get(ChronoField.MILLI_OF_SECOND));
	}
}
