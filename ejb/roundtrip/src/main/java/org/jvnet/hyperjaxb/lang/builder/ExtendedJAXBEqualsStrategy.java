package org.jvnet.hyperjaxb.lang.builder;

import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.dom.DOMSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jvnet.hyperjaxb.xml.datatype.util.XMLGregorianCalendarUtils;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.w3c.dom.Node;

public class ExtendedJAXBEqualsStrategy extends org.jvnet.basicjaxb.lang.ExtendedJAXBEqualsStrategy
{
	@Override
	protected boolean equalsInternal(ObjectLocator leftLocator, ObjectLocator rightLocator, Node lhs, Node rhs)
	{
		final Diff diff = new Diff(new DOMSource(lhs), new DOMSource(rhs))
		{
			@Override
			public int differenceFound(Difference difference)
			{
				if (difference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID)
					return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
				else
					return super.differenceFound(difference);
			}
		};
		return diff.identical();
	}

	@Override
	protected boolean equalsInternal(ObjectLocator leftLocator, ObjectLocator rightLocator,
		XMLGregorianCalendar left, XMLGregorianCalendar right)
	{
		return equals(leftLocator, rightLocator, XMLGregorianCalendarUtils.getTimeInMillis(left),
			XMLGregorianCalendarUtils.getTimeInMillis(right));
	}

	protected boolean equalsInternal(ObjectLocator leftLocator, ObjectLocator rightLocator,
		Comparable<Object> left, Comparable<Object> right)
	{
		return ((Comparable<Object>) left).compareTo(right) == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean equalsInternal(ObjectLocator leftLocator, ObjectLocator rightLocator, Object lhs, Object rhs)
	{
		if (lhs instanceof Comparable<?> && rhs instanceof Comparable<?> && Objects.equals(lhs.getClass(), rhs.getClass()))
			return equalsInternal(leftLocator, rightLocator, (Comparable<Object>) lhs, (Comparable<Object>) rhs);
		else
			return super.equalsInternal(leftLocator, rightLocator, lhs, rhs);
	}
}
