package org.jvnet.hyperjaxb.xsom;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javax.xml.namespace.QName;

import com.sun.xml.xsom.XSComponent;

/**
 * <p>Utility to visit a {@link com.sun.xml.xsom.XSComponent} and get type name.</p>
 * 
 * <p>XML Schema Object Model (XSOM) is a Java library that allows applications
 * to parse XML Schema documents and inspect information in them.</p>
 */
public class TypeUtils
{
	public static List<QName> getTypeNames(XSComponent component)
	{
		requireNonNull(component);
		final SimpleTypeVisitor visitor = new SimpleTypeVisitor();
		component.visit(visitor);
		return visitor.getTypeNames();
	}
}
