package org.jvnet.hyperjaxb.ejb.tests.issuesjpa;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunIssuesJPAPlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperJaxbMojo(mojo);
		mojo.setVerbose(true);
		mojo.setVariant("jpa");
		mojo.setRoundtripTestClassName(null);
		mojo.setDebug(false);
	}
}
