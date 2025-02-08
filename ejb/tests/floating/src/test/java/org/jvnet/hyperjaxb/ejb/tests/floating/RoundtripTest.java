package org.jvnet.hyperjaxb.ejb.tests.floating;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Order;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

@Order(2)
public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.floating";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.floating";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
	
	@Override
	protected void checkSample(File sampleFile)
		throws Exception
	{
		setFailFast(true);
		checkSample(sampleFile, SaveType.MERGE);
	}
	
	@Override
	protected void checkObject(Object obj)
	{
		if ( obj instanceof FloatingTypesType )
		{
			FloatingTypesType ftt = (FloatingTypesType) obj;
			double[][] da = ftt.getDoubleArray();
			assertNotNull(da);
			assertTrue(da.length > 0);
			for ( double cells[] : da )
				assertTrue(cells.length > 0);
			ftt.setDoubleArray(da);
		}
	}
}
