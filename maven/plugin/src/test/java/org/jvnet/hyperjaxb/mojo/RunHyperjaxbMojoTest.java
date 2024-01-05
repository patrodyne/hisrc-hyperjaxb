package org.jvnet.hyperjaxb.mojo;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.jvnet.higherjaxb.mojo.testing.SLF4JLogger;
import org.jvnet.hyperjaxb.mojo.ejb.testing.AbstractHyperMojoTest;

public class RunHyperjaxbMojoTest extends AbstractHyperMojoTest
{
	@Test
	public void testExecute() throws Exception
	{
		//
		// MOJO Execution
		//

		HyperjaxbMojo mojo = new HyperjaxbMojo();
		mojo.setLog(new SLF4JLogger(getLogger()));

		mojo.getRemoteRepos().add(REMOTE_REPOSITORY);
		mojo.setRepoSession(REPOSITORY_SYSTEM_SESSION);
		mojo.setRepoSystem(repositorySystem);
		
		mojo.setProject(createMavenProject());
		mojo.setSchemaDirectory(fullpath("src/test/resources"));
		mojo.setGenerateDirectory(fullpath("target/generated-test-sources/xjc")); 
		mojo.setVerbose(true);
		mojo.setDebug(true);
		mojo.setWriteCode(true);
		mojo.setRemoveOldOutput(true);
		mojo.setForceRegenerate(false);
		mojo.setNoFileHeader(true);
		mojo.setExtension(true);
		mojo.setArgs(new ArrayList<>());

		mojo.setVariant(getVariant());
		mojo.setRoundtripTestClassName(getRoundtripTestClassName());
		
		mojo.execute();
	}
}
