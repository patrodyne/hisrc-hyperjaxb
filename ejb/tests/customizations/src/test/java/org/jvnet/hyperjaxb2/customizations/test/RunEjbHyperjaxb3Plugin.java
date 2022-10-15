package org.jvnet.hyperjaxb2.customizations.test;

import org.jvnet.hyperjaxb3.maven2.Hyperjaxb3Mojo;
import org.jvnet.hyperjaxb3.maven2.ejb.test.RunEjbHyperjaxb3Mojo;

public class RunEjbHyperjaxb3Plugin extends RunEjbHyperjaxb3Mojo
{
	@Override
	protected void configureHyperjaxb3Mojo(Hyperjaxb3Mojo mojo)
	{
		super.configureHyperjaxb3Mojo(mojo);
		mojo.getArgs().add("-Xannotate");
		mojo.getArgs().add("-Xequals");
		mojo.getArgs().add("-XhashCode");
	}
}
