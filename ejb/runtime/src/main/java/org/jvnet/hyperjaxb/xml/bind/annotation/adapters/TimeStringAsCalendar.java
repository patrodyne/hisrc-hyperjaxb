package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * In the context of the Hyperjaxb3 project, Issue 129 (originally tracked on the Java.net JIRA as HYPERJAXB3-129)
 * relates to a specific bug where the Calendar type (and potentially XMLGregorianCalendar) was not being correctly
 * handled or mapped in certain JPA scenarios.
 *
 * <p>Users reported issues where fields typed as java.util.Calendar or derived from xsd:dateTime were not correctly
 * mapped to persistent properties. the default mapping strategy for date/time types failed to apply the necessary
 * {@code @Temporal} annotations or conversion logic required by Hibernate/JPA for Calendar objects.</p>
 *
 * <p>Calendar is considered legacy and requires the deprecated TemporalType for JPA.
 * For modern applications (Java 8+), it is highly recommended to use the Java Time
 * API (e.g., LocalDate, LocalDateTime), without needing {@code @Temporal}.</p>
 */
public class TimeStringAsCalendar extends XmlAdapter<String, Calendar>
{
	private static final DatatypeFactory datatypeFactory;
	static
	{
		try
		{
			datatypeFactory = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException e)
		{
			throw new Error(e);
		}
	}

	@Override
	public String marshal(Calendar time)
		throws Exception
	{
		if ( time == null )
			return null;
		else
			return DatatypeConverter.printTime(time);
	}

	@Override
	public Calendar unmarshal(String time)
		throws Exception
	{
		if ( time == null )
			return null;
		else
		{
			final XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(time);
			final TimeZone timeZone;
			timeZone = xmlGregorianCalendar.getTimeZone(0);
			final Calendar calendar = xmlGregorianCalendar.toGregorianCalendar(timeZone, Locale.getDefault(), null);
			return calendar;
		}
	}
}
