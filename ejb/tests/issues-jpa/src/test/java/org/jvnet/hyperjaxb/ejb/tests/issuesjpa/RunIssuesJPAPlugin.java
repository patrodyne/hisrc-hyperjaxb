package org.jvnet.hyperjaxb.ejb.tests.issuesjpa;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunIssuesJPAPlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperjaxbMojo(mojo);
		mojo.setVerbose(true);
		mojo.setVariant("jpa");
		mojo.setRoundtripTestClassName(null);
		mojo.setDebug(false);
	}
}
