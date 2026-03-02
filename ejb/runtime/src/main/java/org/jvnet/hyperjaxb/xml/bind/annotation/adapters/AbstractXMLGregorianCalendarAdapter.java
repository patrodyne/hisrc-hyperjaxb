package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Abstract {@code XMLGregorianCalendar} {@link XmlAdapter} with methods
 * to null-check bound values and create {@code XMLGregorianCalendar} instances.
 *
 * @param <BoundType> The type of the value to be bound.
 */
public abstract class AbstractXMLGregorianCalendarAdapter<BoundType>
	extends XmlAdapter<XMLGregorianCalendar, BoundType>
{
	private final static DatatypeFactory DATA_TYPE_FACTORY;
	static
	{
		try
		{
			DATA_TYPE_FACTORY = DatatypeFactory.newInstance();
		}
		catch (DatatypeConfigurationException dce)
		{
			throw new RuntimeException(dce);
		}
	}

	@Override
	public final BoundType unmarshal(XMLGregorianCalendar calendar)
		throws Exception
	{
		if (calendar == null)
			return null;
		else
			return createBoundValue(calendar);
	}

	/**
	 * Create the value to be bound from the given calendar.
	 * This method is called by {@link #unmarshal(XMLGregorianCalendar)} after
	 * null values are filtered out.
	 *
	 * @param calendar The given calendar.
	 *
	 * @return The value to be bound.
	 */
	protected abstract BoundType createBoundValue(XMLGregorianCalendar calendar);

	@Override
	public final XMLGregorianCalendar marshal(BoundType boundValue)
		throws Exception
	{
		if (boundValue == null)
			return null;
		else
		{
			final XMLGregorianCalendar target = DATA_TYPE_FACTORY.newXMLGregorianCalendar();
			mergeCalendar(boundValue, target);
			return target;
		}
	}

	/**
	 * Merge the bound value into the given {@code XMLGregorianCalendar} instance.
	 * This method is called by {@link #marshal(Object)} after
	 * null values are filtered out and a new target calendar is created.
	 *
	 * @param boundValue The bound value.
	 * @param xgc The calendar instance.
	 */
	protected abstract void mergeCalendar(BoundType boundValue, XMLGregorianCalendar xgc);
}
