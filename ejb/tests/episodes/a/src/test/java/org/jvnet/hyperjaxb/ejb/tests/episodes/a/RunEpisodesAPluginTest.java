package org.jvnet.hyperjaxb.ejb.tests.episodes.a;

import static com.sun.tools.xjc.Language.XMLSCHEMA;

import java.util.ArrayList;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.jvnet.higherjaxb.mojo.testing.SLF4JLogger;
import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.testing.AbstractHyperMojoTest;

/**
 * Run HyperJAXB Mojo to generate Episode A entities.
 *
 * This test harness executes HyperjaxbMojo which is the main goal of the HyperJAXB Maven plugin.
 *
 * <p>The harness parallels the configuration and actions performed in the Maven build;
 * but, as a unit test. Typically, this harness is run in an IDE for advanced debugging of
 * the HiSrc plugin and libraries</p>
 *
 * <p>As a Maven plugin and as a mojo that supports its own dependencies, it has a complicated
 * classpath structure. For best results, close any other HiSrc projects that you have open in
 * your IDE. This will force the IDE to use the packaged jars; instead of attempted to build a
 * classpath from any open projects. Maven plugins and other jars have resource files that are
 * specially packaged and which the IDE may not pick up.</p>
 */
@Order(1)
public class RunEpisodesAPluginTest extends AbstractHyperMojoTest
{
	@Test
	public void testExecute() throws Exception
	{
		//
		// MOJO Execution
		//
		// HyperjaxbMojo auto-includes:
		//   hisrc-hyperjaxb-ejb-plugin
		//     hisrc-hyperjaxb-annox-plugin
		//     hisrc-basicjaxb-plugins
		//
		
		HyperjaxbMojo mojo = new HyperjaxbMojo();
		mojo.setLog(new SLF4JLogger(getLogger()));

		mojo.getRemoteRepos().add(REMOTE_REPOSITORY);
		mojo.setRepoSession(REPOSITORY_SYSTEM_SESSION);
		mojo.setRepoSystem(repositorySystem);
		
		mojo.setProject(createMavenProject());
		mojo.setSchemaLanguage(XMLSCHEMA.name());
		mojo.setSchemaDirectory(fullpath("src/main/resources"));
		mojo.setSchemaIncludes(new String[] { "*.xsd" });
		mojo.setBindingIncludes(new String[] { "*.xjb" });
		mojo.setGenerateDirectory(fullpath("target/generated-sources/xjc")); 
		mojo.setVerbose(true);
		mojo.setDebug(true);
		mojo.setWriteCode(true);
		mojo.setRemoveOldOutput(true);
		mojo.setForceRegenerate(true);
		mojo.setNoFileHeader(true);
		mojo.setExtension(true);
		mojo.setArgs(new ArrayList<>());
		
		// For episodes in tests-episodes-b: true
		mojo.setNaiveInheritanceStrategy(true);

		mojo.setVariant(getVariant());
		mojo.setRoundtripTestClassName(getRoundtripTestClassName());
		if ( mojo.getRoundtripTestClassName() != null )
			mojo.setValidateXml(false);
		
		mojo.execute();
	}
}
