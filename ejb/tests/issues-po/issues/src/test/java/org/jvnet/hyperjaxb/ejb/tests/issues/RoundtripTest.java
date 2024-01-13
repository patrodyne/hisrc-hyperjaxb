package org.jvnet.hyperjaxb.ejb.tests.issues;

import org.junit.jupiter.api.Order;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

@Order(2)
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
	
	@Override
	protected void checkSample(java.io.File sample) throws Exception
	{
		// issues-po/issues/src/test/samples/
		switch ( sample.getName() )
		{
			case "issue108[0].xml":
			case "issue112[0].xml":
			case "issue138.xml":
			case "issue44One.xml":
			case "issue44five.xml":
			case "issue44four.xml":
			case "issue44three.xml":
			case "issue44two.xml":
			case "issue53[0].xml":
			case "issue66.xml":
			case "issue91.xml":
			case "issue93.xml":
			case "issueHJIII26[0].xml":
			case "issueHJIII40[0].xml":
				super.checkSample(sample, SaveType.PERSIST);
				break;
			default:
				super.checkSample(sample, SaveType.MERGE);
				break;
		}
	}
}
