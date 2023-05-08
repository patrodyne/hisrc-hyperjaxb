package org.jvnet.hyperjaxb.ejb.tests.plmxml;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
    @Override
	public String getContextPath()
	{
        return "org.plmxml.schemas.plmxmlschema:org.plmxml.schemas.plmxmlbusinessschema:org.plmxml.schemas.plmxmlclassificationschema:org.plmxml.schemas.plmxmlroutelistschema";
    }

    @Override
	public String getPersistenceUnitName()
	{
        return "org.plmxml.schemas.plmxmlschema:org.plmxml.schemas.plmxmlbusinessschema:org.plmxml.schemas.plmxmlclassificationschema:org.plmxml.schemas.plmxmlroutelistschema";
    }
}
