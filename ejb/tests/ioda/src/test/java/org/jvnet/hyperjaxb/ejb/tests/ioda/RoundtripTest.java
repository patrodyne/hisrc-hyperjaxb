package org.jvnet.hyperjaxb.ejb.tests.ioda;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "com.iodalliance.schema.ioda_standard_export_v1_16:com.iodalliance.schema.iso_3166_1_alpha_2:com.iodalliance.schema.ioda_styles";
	}

	public String getPersistenceUnitName()
	{
		return "com.iodalliance.schema.ioda_standard_export_v1_16:com.iodalliance.schema.iso_3166_1_alpha_2:com.iodalliance.schema.ioda_styles";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
