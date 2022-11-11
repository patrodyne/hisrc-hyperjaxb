package org.jvnet.hyperjaxb.customizations.test;

import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class RunEjbHyperjaxbPlugin extends RunEjbHyperjaxbMojo
{
	@Override
	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo)
	{
		super.configureHyperjaxbMojo(mojo);
		mojo.getArgs().add("-Xannotate");
		mojo.getArgs().add("-Xequals");
		mojo.getArgs().add("-XhashCode");
	}
}
