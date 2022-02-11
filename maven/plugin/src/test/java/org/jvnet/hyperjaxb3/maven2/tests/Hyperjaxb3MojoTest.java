package org.jvnet.hyperjaxb3.maven2.tests;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.jvnet.hyperjaxb3.maven2.Hyperjaxb3Mojo;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

public class Hyperjaxb3MojoTest extends RunXJC2Mojo {

	@Override
	protected AbstractXJC2Mojo createMojo() {
		return new Hyperjaxb3Mojo();
	}

	@Override
	protected void configureMojo(AbstractXJC2Mojo mojo) {
		super.configureMojo(mojo);
		configureHyperjaxb3Mojo((Hyperjaxb3Mojo) mojo);

	}

	protected void configureHyperjaxb3Mojo(Hyperjaxb3Mojo mojo) {
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
