package org.jvnet.hyperjaxb.lang;

import static org.jvnet.hyperjaxb.xml.util.XMLGregorianCalendarUtils.getTimeInMillis;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.dom.DOMSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jvnet.basicjaxb.lang.DefaultEqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.w3c.dom.Node;

/**
 * An extension of {@link JAXBEqualsStrategy} to override the {@link Object}
 * strategy and to provides additional strategies for {@link OffsetTime},
 * {@link OffsetDateTime}, {@link XMLGregorianCalendar}, DOM {@link Node}
 * and {@link Comparable} objects.
 *
 * TODO: Consider moving these methods to {@link DefaultEqualsStrategy}.
 * Note: I believe the original intent was to isolate these more lenient
 * methods to the round trip test phase, only. But, as a practical matter,
 * temporal objects should match by instant, methinks. And nodes should not
 * care about NS prefix differences.
 */
public class ExtendedJAXBEqualsStrategy extends JAXBEqualsStrategy
{
	/**
	 * Extend the {@link JAXBEqualsStrategy} {@link Object} method to dispatch strategies for
	 * {@link Comparable} and dispatch other types to the super method.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator, Object lhs, Object rhs)
	{
		if (lhs instanceof OffsetTime && rhs instanceof OffsetTime)
			return equalsInternal(lhsLocator, rhsLocator, (OffsetTime) lhs, (OffsetTime) rhs);
		else if (lhs instanceof OffsetDateTime && rhs instanceof OffsetDateTime)
			return equalsInternal(lhsLocator, rhsLocator, (OffsetDateTime) lhs, (OffsetDateTime) rhs);
		else if (lhs instanceof Comparable<?> && rhs instanceof Comparable<?> && Objects.equals(lhs.getClass(), rhs.getClass()))
			return equalsInternal(lhsLocator, rhsLocator, (Comparable<Object>) lhs, (Comparable<Object>) rhs);
		else if (lhs instanceof Node && rhs instanceof Node)
			return equalsInternal(lhsLocator, rhsLocator, (Node) lhs, (Node) rhs);
		else if (lhs instanceof XMLGregorianCalendar && rhs instanceof XMLGregorianCalendar)
			return equalsInternal(lhsLocator, rhsLocator, (XMLGregorianCalendar) lhs, (XMLGregorianCalendar) rhs);
		else
			return super.equalsInternal(lhsLocator, rhsLocator, lhs, rhs);
	}

	/**
	 * Provide a strategy to equate {@link Comparable} objects using their <em>compareTo</em>
	 * method.
	 *
	 * @param lhsLocator The left hand side object locator.
	 * @param rhsLocator The right hand side object locator.
	 * @param lhs The left hand side {@link Comparable} object.
	 * @param rhs The right hand side {@link Comparable} object.
	 *
	 * @return True when objects are equal by comparison; otherwise, false.
	 */
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator,
		Comparable<Object> lhs, Comparable<Object> rhs)
	{
		return observe(lhsLocator, rhsLocator, lhs, rhs, (lhs == null) ? (rhs == null) : (lhs.compareTo(rhs) == 0));
	}

	/**
	 * Provide a strategy to equate {@link OffsetTime} pairs equating instances.
	 *
	 * @param lhsLocator The left hand side object locator.
	 * @param rhsLocator The right hand side object locator.
	 * @param lhs The left hand side {@link OffsetTime}.
	 * @param rhs The right hand side {@link OffsetTime}.
	 *
	 * @return True when the {@code OffsetTime} values are equal by {@code Instance}; otherwise, false.
	 */
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator,
		OffsetTime lhs, OffsetTime rhs)
	{
		return observe(lhsLocator, rhsLocator, lhs, rhs, lhs.isEqual(rhs));
	}

	/**
	 * Provide a strategy to equate {@link OffsetDateTime} pairs equating instances.
	 *
	 * @param lhsLocator The left hand side object locator.
	 * @param rhsLocator The right hand side object locator.
	 * @param lhs The left hand side {@link OffsetDateTime}.
	 * @param rhs The right hand side {@link OffsetDateTime}.
	 *
	 * @return True when the {@code OffsetDateTime} values are equal by {@code Instance}; otherwise, false.
	 */
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator,
		OffsetDateTime lhs, OffsetDateTime rhs)
	{
		return observe(lhsLocator, rhsLocator, lhs, rhs, lhs.isEqual(rhs));
	}

	/**
	 * Provide a strategy to equate {@link XMLGregorianCalendar} pairs equating by time in milliseconds.
	 *
	 * @param lhsLocator The left hand side object locator.
	 * @param rhsLocator The right hand side object locator.
	 * @param lhs The left hand side {@link XMLGregorianCalendar}.
	 * @param rhs The right hand side {@link XMLGregorianCalendar}.
	 *
	 * @return True when the UTC time in milliseconds is the same for both instances; otherwise, false.
	 */
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator,
		XMLGregorianCalendar lhs, XMLGregorianCalendar rhs)
	{
		long lhsMilliseconds = getTimeInMillis(lhs);
		long rhsMilliseconds = getTimeInMillis(rhs);
		return equals(lhsLocator, rhsLocator, lhsMilliseconds, rhsMilliseconds);
	}

	/**
	 * Provide a strategy to equate DOM {@link Node} pairs using the {@link Diff} utility
	 * from XMLUnit to detect differences, ignoring namespace prefixes.
	 *
	 * @param lhsLocator The left hand side object locator.
	 * @param rhsLocator The right hand side object locator.
	 * @param lhs The left hand side {@link Node}.
	 * @param rhs The right hand side {@link Node}.
	 *
	 * @return True when both nodes contain the same elements and attributes in the same order; otherwise, false.
	 */
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator, Node lhs, Node rhs)
	{
		final Diff diff = new Diff(new DOMSource(lhs), new DOMSource(rhs))
		{
			@Override
			public int differenceFound(Difference difference)
			{
				if (difference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID)
				{
					// Ignore differences in namespace prefixes
					return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
				}
				else
					return super.differenceFound(difference);
			}
		};
		return diff.identical();
	}
}
