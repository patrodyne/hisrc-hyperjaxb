package org.jvnet.hyperjaxb.customizations.test;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperJaxbMojo;

public class RunEjbHyperjaxbPlugin extends RunEjbHyperJaxbMojo
{
	@Override
	protected void configureHyperJaxbMojo(HyperjaxbMojo mojo)
	{
		super.configureHyperJaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
		mojo.getArgs().add("-Xequals");
		mojo.getArgs().add("-XhashCode");
	}
}
