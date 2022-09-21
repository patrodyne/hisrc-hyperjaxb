package org.jvnet.hyperjaxb3.ejb.tests.po;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb3.maven2.Hyperjaxb3Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run HyperJAXB Mojo to generate PO entities.
 */
public class RunPoCustomNamingPlugin
{
	private Logger log = LoggerFactory.getLogger(RunPoCustomNamingPlugin.class);

	/**
	 * Validate the generation of a java files from src/main/resources/schema.xsd.
	 */
	@Test
	public void testExecute() throws Exception
	{
		final Hyperjaxb3Mojo mojo = new Hyperjaxb3Mojo();
		
		mojo.setProject(new MavenProject());
		mojo.setSchemaDirectory(getDirectory("src/main/resources"));
		mojo.setGenerateDirectory(getDirectory("target/generated-test-sources/xjc"));
		mojo.setSchemaIncludes(new String[] { "*.xsd" });
		mojo.setBindingIncludes(new String[] { "*.xjb" });
		mojo.setArgs(getArgs());
		mojo.setVerbose(true);
		mojo.setDebug(true);
		mojo.setWriteCode(true);
		mojo.setRemoveOldOutput(true);
		mojo.setForceRegenerate(true);
		mojo.setExtension(true);
		mojo.setVariant("ejb");
		
		mojo.execute();
		log.info("Executed " + mojo);
	}
	
	private File getDirectory(String path)
	{
		return new File(getBaseDir(), path);
	}

	private List<String> getArgs()
	{
		List<String> args = new ArrayList<>();
		return args;
	}

	private File getBaseDir()
	{
		try
		{
			return (new File(RunPoCustomNamingPlugin.class.getProtectionDomain()
				.getCodeSource().getLocation().getFile())).getParentFile()
				.getParentFile().getAbsoluteFile();
		}
		catch (Exception ex)
		{
			throw new AssertionError(ex);
		}
	}
}
