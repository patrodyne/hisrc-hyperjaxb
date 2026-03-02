package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.time.MonthDay;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsGMonthDay extends AbstractXMLGregorianCalendarAdapter<MonthDay>
{
	@Override
	public MonthDay createBoundValue(XMLGregorianCalendar xgc)
	{
		return MonthDay.of(xgc.getMonth(), xgc.getDay());
	}

	@Override
	public void mergeCalendar(MonthDay monthDay, XMLGregorianCalendar xgc)
	{
		xgc.setMonth(monthDay.getMonthValue());
		xgc.setDay(monthDay.getDayOfMonth());
	}
}
