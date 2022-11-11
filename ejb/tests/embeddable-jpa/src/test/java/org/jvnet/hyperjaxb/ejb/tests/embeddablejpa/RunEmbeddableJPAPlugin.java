package org.jvnet.hyperjaxb.ejb.tests.embeddablejpa;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunEmbeddableJPAPlugin extends RunEjbHyperjaxbMojo {
	
	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		// TODO Auto-generated method stub
		super.configureHyperjaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
		mojo.setVariant("jpa");
	}

}
