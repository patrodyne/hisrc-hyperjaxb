package org.jvnet.hyperjaxb.ejb.tests.cuone;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunCuonePlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
//		mojo.result = "mappingFiles";
		super.configureHyperJaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
	}

}
