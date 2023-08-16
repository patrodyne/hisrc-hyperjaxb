package org.jvnet.hyperjaxb.ejb.tests.embeddable;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunEmbeddablePlugin extends RunEjbHyperJaxbMojo {
	
	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		// TODO Auto-generated method stub
		super.configureHyperJaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
	}

}
