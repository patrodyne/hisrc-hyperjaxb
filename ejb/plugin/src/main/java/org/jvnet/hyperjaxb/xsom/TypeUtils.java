package org.jvnet.hyperjaxb.xsom;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.Validate;

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
		Validate.notNull(component);
		final SimpleTypeVisitor visitor = new SimpleTypeVisitor();
		component.visit(visitor);
		return visitor.getTypeNames();
	}
}
