package org.jvnet.hyperjaxb.ejb.tests.embeddable;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunEmbeddablePlugin extends RunEjbHyperjaxbMojo {
	
	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		// TODO Auto-generated method stub
		super.configureHyperjaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
	}

}
