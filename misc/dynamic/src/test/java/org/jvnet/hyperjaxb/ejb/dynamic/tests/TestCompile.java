package org.jvnet.hyperjaxb.ejb.dynamic.tests;

import static org.jvnet.basicjaxb.testing.AbstractSamplesTest.getMavenProjectDir;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

public abstract class TestCompile {

	protected File getBaseDir() {
		try {
			return getMavenProjectDir(getClass());
		} catch (Exception ex) {
			throw new AssertionError(ex);
		}
	}

	public File getSourceDir() {
		return new File(getBaseDir(), "src/test/etc");
	}

	public File getTargetDir() {
		return new File(getBaseDir(), "target/test-etc-classes");
	}

	@Test
	public void testCompile() throws Exception {
		final JavaCompiler systemJavaCompiler = ToolProvider
				.getSystemJavaCompiler();
		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		final StandardJavaFileManager standardFileManager = systemJavaCompiler
				.getStandardFileManager(diagnostics, null, null);

		final Iterable<? extends JavaFileObject> fileObjects = standardFileManager
				.getJavaFileObjects(new File(getSourceDir(),
						"org/jvnet/hyperjaxb/tools/tests/Test.java"));

		final String[] options = new String[] { "-d",
				getTargetDir().getAbsolutePath() };

		getTargetDir().mkdirs();

		systemJavaCompiler.getTask(null, standardFileManager, null,
				Arrays.asList(options), null, fileObjects).call();
		standardFileManager.close();

		final URL[] urls = new URL[] { getTargetDir().toURI().toURL() };
		final URLClassLoader ucl = new URLClassLoader(urls, Thread
				.currentThread().getContextClassLoader());
		final Class clazz = ucl
				.loadClass("org.jvnet.hyperjaxb.tools.tests.Test");
		assertEquals("Wrong value.", "This is a test.", clazz
				.newInstance().toString());

	}
}
