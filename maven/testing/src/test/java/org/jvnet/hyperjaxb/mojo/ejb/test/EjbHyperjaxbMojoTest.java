package org.jvnet.hyperjaxb.mojo.ejb.test;

import java.io.File;

public class EjbHyperjaxbMojoTest extends RunEjbHyperJaxbMojo
{
	@Override
	public File getSchemaDirectory()
	{
		return new File(getBaseDir(), "src/test/resources");
	}
}
