package org.jvnet.hyperjaxb.ejb.tests.ims_eportfolio;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	public String getContextPath()
	{
		return "org.imsglobal.services.common.imscommonschema_v1p0:org.imsglobal.services.gms.xsd.imsgroupmandataschema_v1p0:org.imsglobal.xsd.imsassert_v1p0:org.imsglobal.xsd.imslip_v1p0:org.imsglobal.xsd.imsparticipation_v1p0:org.imsglobal.xsd.imsrdceo_rootv1p0:org.imsglobal.xsd.imsreflex_v1p0:org.imsglobal.xsd.imsrubric_v1p0";
	}

	public String getPersistenceUnitName()
	{
		return "org.imsglobal.services.common.imscommonschema_v1p0:org.imsglobal.services.gms.xsd.imsgroupmandataschema_v1p0:org.imsglobal.xsd.imsassert_v1p0:org.imsglobal.xsd.imslip_v1p0:org.imsglobal.xsd.imsparticipation_v1p0:org.imsglobal.xsd.imsrdceo_rootv1p0:org.imsglobal.xsd.imsreflex_v1p0:org.imsglobal.xsd.imsrubric_v1p0";
	}

	public Boolean isValidateXml()
	{
		return false;
	}
}
