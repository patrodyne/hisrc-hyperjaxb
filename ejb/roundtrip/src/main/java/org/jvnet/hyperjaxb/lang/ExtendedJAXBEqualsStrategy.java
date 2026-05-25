package org.jvnet.hyperjaxb.lang;

import javax.xml.transform.dom.DOMSource;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.w3c.dom.Node;

/**
 * An extension of {@link JAXBEqualsStrategy} to override the DOM {@link Node}
 * object.
 */
public class ExtendedJAXBEqualsStrategy extends JAXBEqualsStrategy
{
	/**
	 * Extend the {@link JAXBEqualsStrategy} {@link Object} method to dispatch strategies for
	 * {@link Comparable} and dispatch other types to the super method.
	 */
	@Override
	protected boolean equalsInternal(ObjectLocator lhsLocator, ObjectLocator rhsLocator, Object lhs, Object rhs)
	{
		if (lhs instanceof Node && rhs instanceof Node)
			return equalsInternal(lhsLocator, rhsLocator, (Node) lhs, (Node) rhs);
		else
			return super.equalsInternal(lhsLocator, rhsLocator, lhs, rhs);
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
