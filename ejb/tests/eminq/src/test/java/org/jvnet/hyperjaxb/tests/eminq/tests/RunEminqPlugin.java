package org.jvnet.hyperjaxb.tests.eminq.tests;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunEminqPlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperjaxbMojo(mojo);
		mojo.setVerbose(true);
	}
}
