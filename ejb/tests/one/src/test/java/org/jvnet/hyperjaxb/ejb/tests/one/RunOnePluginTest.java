package org.jvnet.hyperjaxb.ejb.tests.one;

import java.util.ArrayList;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.jvnet.higherjaxb.mojo.testing.SLF4JLogger;
import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.testing.AbstractHyperMojoTest;

/**
 * Run the HyperJAXB Mojo to trigger Maven's build-process behavior.
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
public class RunOnePluginTest extends AbstractHyperMojoTest
{
	@Test
	public void testExecute() throws Exception
	{
		HyperjaxbMojo mojo = new HyperjaxbMojo();
		mojo.setLog(new SLF4JLogger(getLogger()));

		mojo.getRemoteRepos().add(REMOTE_REPOSITORY);
		mojo.setRepoSession(REPOSITORY_SYSTEM_SESSION);
		mojo.setRepoSystem(repositorySystem);
		
		mojo.setProject(createMavenProject());
		mojo.setSchemaDirectory(fullpath("src/main/resources"));
		mojo.setGenerateDirectory(fullpath("target/generated-sources/xjc")); 
		mojo.setVerbose(true);
		mojo.setDebug(true);
		mojo.setWriteCode(true);
		mojo.setRemoveOldOutput(true);
		mojo.setForceRegenerate(true);
		mojo.setNoFileHeader(true);
		mojo.setExtension(true);
		mojo.setArgs(new ArrayList<>());

		mojo.setMaxIdentifierLength(60);
		mojo.setValidateXml(false);
		mojo.setVariant("ejb");
		mojo.setRoundtripTestClassName(getRoundtripTestClassName());
		
		mojo.execute();
	}
	
	/**
	 * Use existing {@link org.jvnet.hyperjaxb.ejb.tests.one.RoundtripTest} on
	 * test class path.
	 * 
	 * @return Return <code>null</code> to turn off auto-generation of RoundtripTest.
	 */
	@Override
	public String getRoundtripTestClassName()
	{
		// Return null to fix this warning:
		//
		// [warn] Found roundtripTestClassName: org.jvnet.hyperjaxb.ejb.tests.one.RoundtripTest
		// [warn] Adding testCompileSourceRoot: hisrc-hyperjaxb/ejb/tests/one/target/generated-test-sources/xjc
		// [warn] MONITORY: Adding a CompileSourceRoot as a TestCompileSourceRoot may cause issues (M2E, etc.)!
		// [warn] GUIDANCE: Move the generated round trip source code to your source test folder and unset roundtripTestClassName.
		return null;
	}
}
