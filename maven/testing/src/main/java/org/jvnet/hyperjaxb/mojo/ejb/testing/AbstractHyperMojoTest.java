package org.jvnet.hyperjaxb.mojo.ejb.testing;

import org.jvnet.higherjaxb.mojo.testing.AbstractMojoTest;

/**
 * Run the HyperJAXB Mojo to trigger Maven's build-process behavior.
 */
abstract public class AbstractHyperMojoTest extends AbstractMojoTest
{
	public String getVariant()
	{
		return "jpa";
	}

    /**
     * Use existing RoundtripTest on test class (null) path or return
     * a RoundtripTest class name to be generated.
     * 
     * @return Return <code>null</code> to turn off auto-generation of RoundtripTest.
     */
    public String getRoundtripTestClassName()
    {
    	// return getClass().getPackage().getName() + ".RoundtripTest";
    	//
        // Return null to fix this warning:
        //
        // [warn] Found roundtripTestClassName: org.jvnet.hyperjaxb.ejb.tests.one.RoundtripTest
        // [warn] Adding testCompileSourceRoot: hisrc-hyperjaxb/ejb/tests/one/target/generated-test-sources/xjc
        // [warn] MONITORY: Adding a CompileSourceRoot as a TestCompileSourceRoot may cause issues (M2E, etc.)!
        // [warn] GUIDANCE: Move the generated round trip source code to your source test folder and unset roundtripTestClassName.
        return null;
    }
}
