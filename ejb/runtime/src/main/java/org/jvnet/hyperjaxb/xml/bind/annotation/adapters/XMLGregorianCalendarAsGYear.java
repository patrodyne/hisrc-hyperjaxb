package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.Year;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsGYear extends AbstractXMLGregorianCalendarAdapter<Year>
{
	@Override
	public Year createBoundValue(XMLGregorianCalendar xgc)
	{
		return Year.of(xgc.getYear());
	}

	@Override
	public void mergeCalendar(Year year, XMLGregorianCalendar xgc)
	{
		xgc.setYear(year.getValue());
	}
}
