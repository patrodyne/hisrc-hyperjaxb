package org.patrodyne.jvnet.hyperjaxb.ex001;

import jakarta.persistence.EntityManagerFactory;

import org.junit.Ignore;
import org.jvnet.jaxb2_commons.xml.bind.ContextPathAware;

/**
 * Test roundtrip from XML instance to database and back.
 * 
 * Use instead of hisrc-hyperjaxb-maven-plugin, roundtripTestClassName
 * in pom.xml.
 * 
 * @author Rick O'Sullivan
 */
@Ignore("XML Schema contains default value(s).")
public class RoundtripTest
	extends org.jvnet.hyperjaxb3.ejb.test.RoundtripTest
	implements ContextPathAware
{
	public RoundtripTest()
	{
		super();
	}

	public RoundtripTest(EntityManagerFactory emf)
	{
		super();
		setEntityManagerFactory(emf);
	}
	  
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
