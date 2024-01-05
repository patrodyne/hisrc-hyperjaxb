package org.jvnet.hyperjaxb.ejb.tests.po;

import static org.jvnet.basicjaxb.testing.AbstractSamplesTest.getMavenProjectDir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.mojo.HyperjaxbMojo;
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
		final HyperjaxbMojo mojo = new HyperjaxbMojo();
		
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
		mojo.setForceRegenerate(false);
		mojo.setExtension(true);
		mojo.setVariant("ejb");
		mojo.setBeansPropertiesLocator("classpath:/META-INF/beans.properties");
		mojo.setRoundtripTestClassName("org.jvnet.hyperjaxb.ejb.tests.po.RoundtripTest");
		mojo.setResult("annotations"); // annotations | mappingFiles
		
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
			return getMavenProjectDir(RunPoCustomNamingPlugin.class);
		}
		catch (Exception ex)
		{
			throw new AssertionError(ex);
		}
	}
}
