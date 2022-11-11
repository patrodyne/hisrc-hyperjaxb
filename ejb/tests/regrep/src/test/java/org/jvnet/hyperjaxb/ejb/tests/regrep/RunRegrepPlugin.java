package org.jvnet.hyperjaxb.ejb.tests.regrep;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunRegrepPlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperjaxbMojo(mojo);
		mojo.setRoundtripTestClassName("org.freebxml.omar.jaxb.bindings.RoundtripTest");
	}
}
