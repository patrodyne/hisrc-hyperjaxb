package org.jvnet.hyperjaxb.ejb.tests.po;

import java.io.File;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunPoPlugin extends RunEjbHyperjaxbMojo {

	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		super.configureHyperjaxbMojo(mojo);
		
		mojo.setPersistenceXml(new File(getBaseDir(), "src/main/etc/persistence.xml"));
	}
}
