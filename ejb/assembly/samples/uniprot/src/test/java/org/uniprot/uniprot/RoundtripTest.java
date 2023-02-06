package org.uniprot.uniprot;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.uniprot.uniprot";
	}

	public String getPersistenceUnitName()
	{
		return "org.uniprot.uniprot";
	}
}
