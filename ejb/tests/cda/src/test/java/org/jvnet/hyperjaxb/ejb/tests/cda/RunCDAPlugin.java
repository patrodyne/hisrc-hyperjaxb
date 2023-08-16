package org.jvnet.hyperjaxb.ejb.tests.cda;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunCDAPlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperJaxbMojo(mojo);
		mojo.setSchemaIncludes(new String[]{"CDASchemas/cda/Schemas/CDA.xsd"});
	}

}
