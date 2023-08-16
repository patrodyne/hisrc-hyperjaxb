package org.jvnet.hyperjaxb.ejb.tests.po;

import java.io.File;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunPoPlugin extends RunEjbHyperJaxbMojo {

	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperJaxbMojo(mojo);
		
		mojo.setPersistenceXml(new File(getBaseDir(), "src/main/etc/persistence.xml"));
	}
}
