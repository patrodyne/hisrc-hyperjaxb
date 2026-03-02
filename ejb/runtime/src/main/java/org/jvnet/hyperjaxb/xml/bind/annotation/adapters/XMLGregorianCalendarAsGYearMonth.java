package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.YearMonth;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsGYearMonth extends AbstractXMLGregorianCalendarAdapter<YearMonth>
{
	@Override
	public YearMonth createBoundValue(XMLGregorianCalendar xgc)
	{
		return YearMonth.of(xgc.getYear(), xgc.getMonth());
	}

	@Override
	public void mergeCalendar(YearMonth yearMonth, XMLGregorianCalendar xgc)
	{
		xgc.setYear(yearMonth.getYear());
		xgc.setMonth(yearMonth.getMonthValue());
	}
}
