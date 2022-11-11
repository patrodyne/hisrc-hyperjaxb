package org.jvnet.hyperjaxb.ejb.tests.cda;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunCDAPlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperjaxbMojo(mojo);
		mojo.setSchemaIncludes(new String[]{"CDASchemas/cda/Schemas/CDA.xsd"});
	}

}
