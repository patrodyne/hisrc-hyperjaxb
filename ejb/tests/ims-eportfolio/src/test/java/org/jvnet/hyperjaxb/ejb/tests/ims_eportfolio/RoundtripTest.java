package org.jvnet.hyperjaxb.ejb.tests.ims_eportfolio;

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
		return "org.imsglobal.services.common.imscommonschema_v1p0:org.imsglobal.services.gms.xsd.imsgroupmandataschema_v1p0:org.imsglobal.xsd.imsassert_v1p0:org.imsglobal.xsd.imslip_v1p0:org.imsglobal.xsd.imsparticipation_v1p0:org.imsglobal.xsd.imsrdceo_rootv1p0:org.imsglobal.xsd.imsreflex_v1p0:org.imsglobal.xsd.imsrubric_v1p0";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.imsglobal.services.common.imscommonschema_v1p0:org.imsglobal.services.gms.xsd.imsgroupmandataschema_v1p0:org.imsglobal.xsd.imsassert_v1p0:org.imsglobal.xsd.imslip_v1p0:org.imsglobal.xsd.imsparticipation_v1p0:org.imsglobal.xsd.imsrdceo_rootv1p0:org.imsglobal.xsd.imsreflex_v1p0:org.imsglobal.xsd.imsrubric_v1p0";
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
			case "Assertion.xml":
			case "Identification.xml":
			case "Reflexion.xml":
			case "Relationship.xml":
			case "minimalIdentification.xml":
			case "uw_stout_rubric.xml":
				super.checkSample(sample, SaveType.PERSIST);
				break;
			default:
				super.checkSample(sample, SaveType.MERGE);
				break;
		}
	}

}
