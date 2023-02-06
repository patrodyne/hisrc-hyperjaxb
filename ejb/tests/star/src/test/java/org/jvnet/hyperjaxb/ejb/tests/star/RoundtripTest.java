package org.jvnet.hyperjaxb.ejb.tests.star;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    public String getContextPath()
	{
        return "org.openapplications.oagis._9:org.openapplications.oagis._9.currencycode._54217._2001:org.openapplications.oagis._9.ianamimemediatypes._2003:org.openapplications.oagis._9.languagecode._5639._1988:org.openapplications.oagis._9.unitcode._66411._2001:org.openapplications.oagis._9.unqualifieddatatypes._1:org.starstandard.star._5:org.starstandard.star._5.qualifieddatatypes._1:org.starstandard.star._5.codelists:org.nmma.codelists:org.openapplications.oagis._9.codelists:com.xfront.unitsofmeasure";
    }

    public String getPersistenceUnitName()
	{
        return "org.openapplications.oagis._9:org.openapplications.oagis._9.currencycode._54217._2001:org.openapplications.oagis._9.ianamimemediatypes._2003:org.openapplications.oagis._9.languagecode._5639._1988:org.openapplications.oagis._9.unitcode._66411._2001:org.openapplications.oagis._9.unqualifieddatatypes._1:org.starstandard.star._5:org.starstandard.star._5.qualifieddatatypes._1:org.starstandard.star._5.codelists:org.nmma.codelists:org.openapplications.oagis._9.codelists:com.xfront.unitsofmeasure";
    }
}
