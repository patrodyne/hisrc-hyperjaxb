package org.jvnet.hyperjaxb.ejb.tests.embeddablejpa;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunEmbeddableJPAPlugin extends RunEjbHyperJaxbMojo {
	
	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		// TODO Auto-generated method stub
		super.configureHyperJaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
		mojo.setVariant("jpa");
	}

}
