package org.jvnet.hyperjaxb.ejb.tests.any;

import java.util.ArrayList;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.jvnet.higherjaxb.mojo.testing.SLF4JLogger;
import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
import org.jvnet.hyperjaxb.mojo.ejb.testing.AbstractHyperMojoTest;

@Order(1)
public class RunAnyPluginTest extends AbstractHyperMojoTest
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
		mojo.setSchemaDirectory(fullpath("src/main/resources"));
		mojo.setGenerateDirectory(fullpath("target/generated-sources/xjc")); 
		mojo.setVerbose(true);
		mojo.setDebug(true);
		mojo.setWriteCode(true);
		mojo.setRemoveOldOutput(true);
		mojo.setForceRegenerate(true);
		mojo.setNoFileHeader(true);
		
		mojo.setMaxIdentifierLength(60);
		mojo.setValidateXml(false);
		mojo.setGenerateTransientId(true);
		
		mojo.setExtension(true);
		mojo.setArgs(new ArrayList<>());

		mojo.setVariant(getVariant());
		mojo.setRoundtripTestClassName(getRoundtripTestClassName());
		
		mojo.execute();
	}
}
