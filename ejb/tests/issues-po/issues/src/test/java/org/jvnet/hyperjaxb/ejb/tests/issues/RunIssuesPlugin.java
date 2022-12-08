package org.jvnet.hyperjaxb.ejb.tests.issues;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunIssuesPlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperjaxbMojo(mojo);
		mojo.setVerbose(true);
	}
}
