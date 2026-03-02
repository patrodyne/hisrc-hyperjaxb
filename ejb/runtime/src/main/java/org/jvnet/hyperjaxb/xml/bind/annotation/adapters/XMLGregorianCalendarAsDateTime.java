package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.OffsetDateTime;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsDateTime extends AbstractXMLGregorianCalendarAdapter<OffsetDateTime>
{
	@Override
	public OffsetDateTime createBoundValue(XMLGregorianCalendar xgc)
	{
		return xgc.toGregorianCalendar().toZonedDateTime().toOffsetDateTime();
	}

	@Override
	public void mergeCalendar(OffsetDateTime odt, XMLGregorianCalendar xgc)
	{
		xgc.setYear(odt.getYear());
		xgc.setMonth(odt.getMonthValue());
		xgc.setDay(odt.getDayOfMonth());
		xgc.setHour(odt.getHour());
		xgc.setMinute(odt.getMinute());
		xgc.setSecond(odt.getSecond());
		// Convert nanos to milliseconds
		xgc.setMillisecond(odt.getNano() / 1_000_000);
		// TZ Offset in minutes
		xgc.setTimezone(odt.getOffset().getTotalSeconds() / 60);
	}
}
