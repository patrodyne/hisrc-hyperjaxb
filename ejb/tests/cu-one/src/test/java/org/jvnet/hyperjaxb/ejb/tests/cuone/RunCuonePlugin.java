package org.jvnet.hyperjaxb.ejb.tests.cuone;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunCuonePlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
//		mojo.result = "mappingFiles";
		super.configureHyperjaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
	}

}
