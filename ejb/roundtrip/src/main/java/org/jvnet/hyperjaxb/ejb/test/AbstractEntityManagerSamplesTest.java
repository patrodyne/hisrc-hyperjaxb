package org.jvnet.hyperjaxb.ejb.test;

import static java.util.Arrays.sort;
import static org.jvnet.basicjaxb.testing.AbstractSamplesTest.getMavenProjectDir;

import java.io.File;
import java.util.Collection;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.basicjaxb.testing.AbstractSamplesTest;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

public abstract class AbstractEntityManagerSamplesTest extends AbstractEntityManagerTest implements ContextPathAware
{
	public static final String DEFAULT_SAMPLES_DIRECTORY_NAME = "src/test/samples";
	private final static IOFileFilter SAMPLE_FILTER = FileFilterUtils.suffixFileFilter(".xml");

	private Boolean failFast = false;
	public Boolean isFailFast() { return failFast; }
	public void setFailFast(Boolean failFast) { this.failFast = failFast; }
	
	private AbstractSamplesTest samplesTest;
	public AbstractSamplesTest getSamplesTest()
	{
		if (samplesTest == null)
			setSamplesTest(createSamplesTest());
		return samplesTest;
	}
	public void setSamplesTest(AbstractSamplesTest samplesTest)
	{
		this.samplesTest = samplesTest;
	}

	@Override
	public String getContextPath()
	{
		return getClass().getPackage().getName();
	}

	@Override
	public String getPersistenceUnitName()
	{
		return getContextPath();
	}

	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		super.setUp();
		setSamplesTest(createSamplesTest());
	}

	@AfterEach
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testSamples()
		throws Exception
	{
		getSamplesTest().testSamples();
	}
	
	protected AbstractSamplesTest createSamplesTest()
	{
		AbstractSamplesTest ast = new AbstractSamplesTest()
		{
			@Override
			protected void checkSample(File sample)
				throws Exception
			{
				AbstractEntityManagerSamplesTest.this.checkSample(sample);
			}

			@Override
			protected String getContextPath()
			{
				return AbstractEntityManagerSamplesTest.this.getContextPath();
			}

			@Override
			protected Class<? extends Object> getTestClass()
			{
				return AbstractEntityManagerSamplesTest.this.getClass();
			}

			@Override
			protected File[] getSampleFiles()
			{
				return AbstractEntityManagerSamplesTest.this.getSampleFiles();
			}

			@Override
			protected File getSamplesDirectory()
			{
				return AbstractEntityManagerSamplesTest.this.getSamplesDirectory();
			}

			@Override
			protected ClassLoader getContextClassLoader()
			{
				return AbstractEntityManagerSamplesTest.this.getContextClassLoader();
			}
		};
		ast.setFailFast(isFailFast());
		return ast;
	}

	protected JAXBContext createContext()
		throws JAXBException
	{
		return getSamplesTest().createContext();
	}

	protected abstract void checkSample(File sample)
		throws Exception;

	protected File getBaseDir()
	{
		try
		{
			return getMavenProjectDir(getClass());
		}
		catch (Exception ex)
		{
			throw new AssertionError(ex);
		}
	}

	protected String getSamplesDirectoryName()
	{
		return DEFAULT_SAMPLES_DIRECTORY_NAME;
	}
	
	protected File getSamplesDirectory()
	{
		return new File(getBaseDir(), getSamplesDirectoryName());
	}

	protected File[] getSampleFiles()
	{
		File samplesDirectory = getSamplesDirectory();
		getLogger().info("Testing directory [" + samplesDirectory.getAbsolutePath() + "].");
		if (samplesDirectory == null || !samplesDirectory.isDirectory())
			return new File[] {};
		else
		{
			final Collection<File> files = FileUtils.listFiles(samplesDirectory, SAMPLE_FILTER, TrueFileFilter.INSTANCE);
			File[] fileArray = files.toArray(new File[files.size()]);
			sort(fileArray);
			return fileArray;
		}
	}

	protected ClassLoader getContextClassLoader()
	{
		return getClass().getClassLoader();
	}
}
