package org.jvnet.hyperjaxb.mojo.ejb.test;

import java.io.File;

import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

public class EjbHyperjaxbMojoTest extends RunEjbHyperjaxbMojo
{
	@Override
	public File getSchemaDirectory()
	{
		return new File(getBaseDir(), "src/test/resources");
	}
}
