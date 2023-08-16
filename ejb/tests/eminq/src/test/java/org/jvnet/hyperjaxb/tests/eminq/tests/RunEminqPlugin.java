package org.jvnet.hyperjaxb.tests.eminq.tests;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunEminqPlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperJaxbMojo(mojo);
		mojo.setVerbose(true);
	}
}
