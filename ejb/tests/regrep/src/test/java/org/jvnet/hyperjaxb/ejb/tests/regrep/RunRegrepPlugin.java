package org.jvnet.hyperjaxb.ejb.tests.regrep;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunRegrepPlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperJaxbMojo(mojo);
		mojo.setRoundtripTestClassName("org.freebxml.omar.jaxb.bindings.RoundtripTest");
	}
}
