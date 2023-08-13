
package org.jvnet.hyperjaxb.ejb.tests.idSymbolSpace;

import java.io.File;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    @Override
    public String getContextPath()
    {
        return "org.jvnet.hyperjaxb.ejb.tests.idSymbolSpace";
    }

    @Override
    public String getPersistenceUnitName()
    {
        return "org.jvnet.hyperjaxb.ejb.tests.idSymbolSpace";
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
    	getSamplesTest().setFailFast(true);
    	super.checkSample(sampleFile);
    }
    
}
