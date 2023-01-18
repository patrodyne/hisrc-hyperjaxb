package org.patrodyne.jvnet.hyperjaxb.ex001;

import org.junit.jupiter.api.Disabled;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

/**
 * Test roundtrip from XML instance to database and back.
 * 
 * Use instead of hisrc-hyperjaxb-maven-plugin, roundtripTestClassName
 * in pom.xml.
 * 
 * @author Rick O'Sullivan
 */
@Disabled("XML Schema contains default value(s).")
public class RoundtripTest
	extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
	implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.patrodyne.jvnet.hyperjaxb.ex001.model";
	}

	public String getPersistenceUnitName()
	{
		return "org.patrodyne.jvnet.hyperjaxb.ex001.model";
	}
	
	protected String getSamplesDirectoryName()
	{
		return "src/test/samples";
	}
}
