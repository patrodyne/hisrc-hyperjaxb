package org.jvnet.hyperjaxb.ejb.tests.one;

import org.jvnet.hyperjaxb.mojo.ejb.test.RunEjbHyperjaxbMojo;

/**
 * Run the HyperJAXB Mojo to trigger Maven's build-process behavior.
 */
public class RunOnePlugin extends RunEjbHyperjaxbMojo
{
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
