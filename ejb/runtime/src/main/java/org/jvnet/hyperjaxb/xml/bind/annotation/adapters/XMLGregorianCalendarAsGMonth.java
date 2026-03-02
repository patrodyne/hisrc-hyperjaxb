package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.Month;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsGMonth extends AbstractXMLGregorianCalendarAdapter<Month>
{
	@Override
	public Month createBoundValue(XMLGregorianCalendar xgc)
	{
		// Obtains an instance of {@code Month} from an {@code int} value.
		return Month.of(xgc.getMonth());
	}

	@Override
	public void mergeCalendar(Month month, XMLGregorianCalendar xgc)
	{
		// The month in the year, from 1 to 12.
		xgc.setMonth(month.getValue());
	}
}
