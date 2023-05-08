package org.jvnet.hyperjaxb.ejb.tests.fpml_pretrade;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    @Override
	public String getContextPath()
	{
        return "org.fpml.fpml_5_0.pretrade:org.w3._2000._09.xmldsig_";
    }

    @Override
	public String getPersistenceUnitName()
	{
        return "org.fpml.fpml_5_0.pretrade:org.w3._2000._09.xmldsig_";
    }
}
