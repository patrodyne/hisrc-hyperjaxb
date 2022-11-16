package org.jvnet.hyperjaxb.ejb.test.tests;

import java.io.File;

public class RoundtripTest extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
{
	
	@Override
	protected void checkSample(File sample)
		throws Exception
	{
		super.checkSample(sample);
	}

	@Override
	public Boolean isValidateXml()
	{
		// Intentional? 'src/test/samples/sample1.xml' contains an invalid "<A/>" element.
		return false;
	}
}
