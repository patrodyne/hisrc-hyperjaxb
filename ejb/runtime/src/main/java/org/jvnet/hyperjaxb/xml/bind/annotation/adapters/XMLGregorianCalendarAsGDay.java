package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAsGDay extends AbstractXMLGregorianCalendarAdapter<Integer>
{
	@Override
	public Integer createBoundValue(XMLGregorianCalendar xgc)
	{
		return xgc.toGregorianCalendar().toZonedDateTime().toLocalDate().getDayOfMonth();
	}

	@Override
	public void mergeCalendar(Integer dom, XMLGregorianCalendar xgc)
	{
		// The day of month, from 1 to 31.
		xgc.setDay(dom);
	}
}
