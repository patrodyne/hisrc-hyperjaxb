package org.jvnet.hyperjaxb.ejb.tests.issuesjpa;

import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public class RoundtripTest extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
		implements ContextPathAware {

	public String getContextPath() {
		return "org.jvnet.hyperjaxb.ejb.tests.issuesjpa";
	}

	public String getPersistenceUnitName() {
		return "org.jvnet.hyperjaxb.ejb.tests.issuesjpa";
	}

    public Boolean isValidateXml() {
        return false;
    }

}
