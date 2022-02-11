package org.jvnet.hyperjaxb3.maven2.ejb.test;

import org.apache.maven.project.MavenProject;
import org.jvnet.hyperjaxb3.maven2.Hyperjaxb3Mojo;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.jaxb2.maven2.test.RunXJC2Mojo;

public class RunEjbHyperjaxb3Mojo extends RunXJC2Mojo {

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
		mojo.setExtension(true);
		mojo.setDebug(true);
		// mojo.setBvariant = "ejb";
		mojo.roundtripTestClassName = getClass().getPackage().getName()
				+ ".RoundtripTest";
		// [WARNING] You are using forceRegenerate=true in your configuration.
		//   This configuration setting is deprecated and not recommended as it causes problems with incremental builds in IDEs.
		//   Please refer to the following link for more information:
		//   https://github.com/highsource/maven-jaxb2-plugin/wiki/Do-Not-Use-forceRegenerate
		//   Consider removing this setting from your plugin configuration.
		// mojo.setForceRegenerate(true);
	}

}
