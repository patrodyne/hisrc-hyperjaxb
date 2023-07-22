package org.jvnet.hyperjaxb.ejb.tests.issues;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
	extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
	implements ContextPathAware
{
//	protected ClassLoader getContextClassLoader()
//	{
//		Class<ObjectFactory> poObjectFactory = org.jvnet.hyperjaxb.ejb.tests.po.ObjectFactory.class;
//		return poObjectFactory.getClassLoader();
//	}
	
	@Override
	public String getContextPath()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.issuesignored:org.jvnet.hyperjaxb.ejb.tests.issues:org.jvnet.hyperjaxb.ejb.tests.po";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.issues";
	}

	@Override
	public Boolean isValidateXml()
	{
		return false;
	}
	
//	@Override
//	protected void checkSample(File sample) throws Exception
//	{
//		// issues-po/issues/src/test/samples/issue112[0].xml
//		if ( "issue112[0].xml".equals(sample.getName()) )
//		{
//			super.checkSample(sample);
//		}
//	}
}
