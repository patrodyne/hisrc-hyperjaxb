package org.jvnet.hyperjaxb.mojo;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.jvnet.higherjaxb.mojo.AbstractHigherjaxbParmMojo;
import org.jvnet.higherjaxb.mojo.test.RunHigherjaxbMojo;

public class HyperjaxbMojoTest extends RunHigherjaxbMojo {

	@Override
	protected AbstractHigherjaxbParmMojo createMojo() {
		return new HyperjaxbMojo();
	}

	@Override
	protected void configureMojo(AbstractHigherjaxbParmMojo mojo) {
		super.configureMojo(mojo);
		configureHyperjaxbMojo((HyperjaxbMojo) mojo);

	}

	protected void configureHyperjaxbMojo(HyperjaxbMojo mojo) {
		final MavenProject project = new MavenProject();
		mojo.setProject(project);
		mojo.setDebug(true);

		// [WARNING] You are using forceRegenerate=true in your configuration.
		//   This configuration setting is deprecated and not recommended as it causes problems with incremental builds in IDEs.
		//   Please refer to the following link for more information:
		//   https://github.com/highsource/maven-jaxb2-plugin/wiki/Do-Not-Use-forceRegenerate
		//   Consider removing this setting from your plugin configuration.
		//
		// mojo.setForceRegenerate(true);
	}

	@Override
	public File getSchemaDirectory() {
		return new File(getBaseDir(), "src/test/resources");
	}
}
