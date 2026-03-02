package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.LocalDate;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsDate extends AbstractXMLGregorianCalendarAdapter<LocalDate>
{
	@Override
	public LocalDate createBoundValue(XMLGregorianCalendar xgc)
	{
		return xgc.toGregorianCalendar().toZonedDateTime().toLocalDate();
	}

	@Override
	public void mergeCalendar(LocalDate ld, XMLGregorianCalendar xgc)
	{
		xgc.setYear(ld.getYear());
		xgc.setMonth(ld.getMonthValue());
		xgc.setDay(ld.getDayOfMonth());
	}
}
