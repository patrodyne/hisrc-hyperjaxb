package org.jvnet.hyperjaxb.ejb.tests.equalsbuilder;

import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;

public class HelloWorldEqualsStrategy extends JAXBEqualsStrategy {

	@Override
	public boolean equals(ObjectLocator leftLocator,
			ObjectLocator rightLocator, Object lhs, Object rhs) {
		System.out.println("Hello world!");
		return super.equals(leftLocator, rightLocator, lhs, rhs);
	}

}
