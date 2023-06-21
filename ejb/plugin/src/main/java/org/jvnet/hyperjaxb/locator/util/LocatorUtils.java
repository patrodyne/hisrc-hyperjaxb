package org.jvnet.hyperjaxb.locator.util;

import org.xml.sax.Locator;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;

public class LocatorUtils
{
	private LocatorUtils() {  }
	
	public static String getLocation(Object metadata)
	{
		return org.jvnet.basicjaxb.locator.util.LocatorUtils.getLocation(metadata);
	}
	
	public static String getLocation(Locator locator)
	{
		return org.jvnet.basicjaxb.locator.util.LocatorUtils.getLocation(locator);
	}
	
	public static String getLocation(ClassOutline classOutline)
	{
		return org.jvnet.basicjaxb.locator.util.LocatorUtils.getLocation(classOutline.getTarget().getLocator());
	}
	
	public static String getLocation(CClassInfo ci)
	{
		return org.jvnet.basicjaxb.locator.util.LocatorUtils.getLocation(ci.getSchemaComponent().getLocator());
	}

	public static String getLocation(FieldOutline fieldOutline)
	{
		CPropertyInfo fieldInfo = fieldOutline.getPropertyInfo();
		CTypeInfo fieldParent = fieldInfo.parent();
		Locator locator = fieldInfo.getLocator();
		if ( locator == null )
		{
			if ( fieldParent.getSchemaComponent() != null )
				locator = fieldParent.getSchemaComponent().getLocator();
			else
				locator = fieldParent.getLocator();
		}
		return org.jvnet.basicjaxb.locator.util.LocatorUtils.getLocation(locator);
	}
	
	public static String getLocation(CPropertyInfo pi)
	{
		return getLocation(pi, null);
	}
	
	public static String getLocation(CPropertyInfo pi, CClassInfo ci)
	{
		Locator locator = pi.getLocator();
		if ( locator == null )
		{
			if ( pi.getSchemaComponent() != null )
				locator = pi.getSchemaComponent().getLocator();
			else
			{
				if ( pi.parent().getLocator() != null )
					locator = pi.parent().getLocator();
				else if ( ci != null )
					locator = ci.getSchemaComponent().getLocator();
			}
		}
		return org.jvnet.basicjaxb.locator.util.LocatorUtils.getLocation(locator);
	}

}
