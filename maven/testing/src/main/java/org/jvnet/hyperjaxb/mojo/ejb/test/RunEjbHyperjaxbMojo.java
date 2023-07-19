package org.jvnet.hyperjaxb.mojo.ejb.test;

import org.apache.maven.project.MavenProject;
import org.jvnet.higherjaxb.mojo.AbstractHigherjaxbParmMojo;
import org.jvnet.higherjaxb.mojo.test.RunHigherjaxbMojo;
import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;

/**
 * Run the HyperJAXB Mojo to trigger Maven's build-process behavior.
 */
public class RunEjbHyperjaxbMojo extends RunHigherjaxbMojo
{
	@Override
	protected AbstractHigherjaxbParmMojo<?> createMojo()
	{
		return new HyperjaxbMojo();
	}

	@Override
	protected void configureMojo(AbstractHigherjaxbParmMojo<?> mojo)
	{
		super.configureMojo(mojo);
		configureHyperjaxbMojo((HyperjaxbMojo) mojo);
	}

	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo)
	{
		final MavenProject project = new MavenProject();
		mojo.setProject(project);
		mojo.setExtension(isExtension());
		mojo.setVariant(getVariant());
		mojo.setRoundtripTestClassName(getRoundtripTestClassName());
		
		// [WARNING] You are using forceRegenerate=true in your configuration.
		//   This configuration setting is deprecated and not recommended as it causes problems with incremental builds in IDEs.
		//   Please refer to the following link for more information:
		//   https://github.com/highsource/maven-jaxb2-plugin/wiki/Do-Not-Use-forceRegenerate
		//   Consider removing this setting from your plugin configuration.
		// mojo.setForceRegenerate(true);
		mojo.setRemoveOldOutput(true);
		mojo.setForceRegenerate(false);
	}
	
	public boolean isExtension()
	{
		return true;
	}

	public String getVariant()
	{
		return "ejb";
	}

	public String getRoundtripTestClassName()
	{
		return getClass().getPackage().getName() + ".RoundtripTest";
	}
}
