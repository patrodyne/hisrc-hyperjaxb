package org.jvnet.hyperjaxb.ejb.tests.issues;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunIssuesPlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperJaxbMojo(mojo);
		mojo.setVerbose(true);
	}
}
