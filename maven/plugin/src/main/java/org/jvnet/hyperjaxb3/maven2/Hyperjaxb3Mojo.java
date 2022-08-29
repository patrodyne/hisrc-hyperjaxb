package org.jvnet.hyperjaxb3.maven2;

import static org.jvnet.hyperjaxb3.ejb.Constants.TODO_LOG_LEVEL;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.jvnet.mjiip.v_2.XJC2Mojo;

import com.sun.tools.xjc.Options;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class Hyperjaxb3Mojo extends XJC2Mojo {

	/**
	 * Target directory for the generated mappings. If left empty, mappings are
	 * generated together with sources.
	 */
	@Parameter(property = "maven.hj3.target")
	public File target;

	/**
	 * Name of the roundtrip test case. If omitted, no roundtrip test case is
	 * generated.
	 */
	@Parameter(property = "maven.hj3.roundtripTestClassName")
	public String roundtripTestClassName;

	/**
	 * Patterns for files to be included as resources.
	 */
	@Parameter
	public String[] resourceIncludes = new String[] { "**/*.hbm.xml",
			"**/*.orm.xml", "**/*.cfg.xml", "META-INF/persistence.xml" };

	/**
	 * Persistence variant. Switches between various persistence
	 * implementations. Possible values are "hibernate" and "ejb-hibernate".
	 */
	@Parameter(property = "maven.hj3.variant", defaultValue = "ejb")
	public String variant = "ejb";

	/**
	 * Persistence unit name (EJB3 specific).
	 */
	@Parameter(property = "maven.hj3.persistenceUnitName")
	public String persistenceUnitName;

	/**
	 * Persistence unit name (EJB3 specific).
	 */
	@Parameter(property = "maven.hj3.persistenceXml")
	public File persistenceXml;

	/**
	 * Whether to add or remove arbitrary annotations to/from generated sources.
	 */
	@Parameter(property = "maven.hj3.generateAnnotation", defaultValue = "true")
	public boolean generateAnnotation = true;

	/**
	 * Whether schema-derived classes extend certain class or implement certain interfaces.
	 */
	@Parameter(property = "maven.hj3.generateInheritance", defaultValue = "true")
	public boolean generateInheritance = true;

	/**
	 * Whether the <code>hashCode()</code> method should be generated.
	 */
	@Parameter(property = "maven.hj3.generateHashCode", defaultValue = "true")
	public boolean generateHashCode = true;

	/**
	 * Whether the <code>equals(...)</code> methods should be generated.
	 */
	@Parameter(property = "maven.hj3.generateEquals", defaultValue = "true")
	public boolean generateEquals = true;

	/**
	 * Whether the <code>toString()</code> methods should be generated.
	 */
	@Parameter(property = "maven.hj3.generateToString", defaultValue = "true")
	public boolean generateToString = true;

	/**
	 * Override HashCode, Equals, ToString. Possible values are "strategic", "simple[123]".
	 */
	@Parameter(property = "maven.hj3.overrideHET", defaultValue = "strategic")
	public String overrideHET = "strategic";

	/**
	 * Whether the generated id property must be transient.
	 */
	@Parameter(property = "maven.hj3.generateTransientId", defaultValue = "false")
	public boolean generateTransientId = false;

	/**
	 * Generation result. Possible values are "annotations", "mappingFiles".
	 */
	@Parameter(property = "maven.hj3.result", defaultValue = "annotations")
	public String result = "annotations";

	@Parameter
	public String[] preArgs = new String[0];

	@Parameter
	public String[] postArgs = new String[0];

	/**
	 * Sets up the verbose and debug mode depending on mvn logging level, and
	 * sets up hyperjaxb logging.
	 */
	protected void setupLogging()
	{
		super.setupLogging();
		// final Logger logger = LoggerFactory.getLogger("org.jvnet.hyperjaxb3");
		final Log log = getLog();
		if (this.getDebug())
		{
			if ( !log.isDebugEnabled() )
				todo(log, "Logger level not set to [debug] because not supported.");
		}
		else if (this.getVerbose())
		{
			if ( !log.isInfoEnabled() )
				todo(log, "Logger level not set to [info] because not supported.");
		}
	}

	/**
	 * Logs options defined directly as mojo parameters.
	 */
	protected void logConfiguration() throws MojoExecutionException {
		super.logConfiguration();

		getLog().info("Configuration: target:" + target);
		getLog().info("Configuration: roundtripTestClassName:" + roundtripTestClassName);
		getLog().info("Configuration: resourceIncludes:" + resourceIncludes);
		getLog().info("Configuration: variant:" + variant);
		getLog().info("Configuration: persistenceUnitName:" + persistenceUnitName);
		getLog().info("Configuration: persistenceXml:" + persistenceXml);
		getLog().info("Configuration: generateInheritance:" + generateInheritance);
		getLog().info("Configuration: generateAnnotation:" + generateAnnotation);
		getLog().info("Configuration: generateHashCode:" + generateHashCode);
		getLog().info("Configuration: generateEquals:" + generateEquals);
		getLog().info("Configuration: generateToString:" + generateToString);
		getLog().info("Configuration: generateHET:" + overrideHET);
		getLog().info("Configuration: generateTransientId:" + generateTransientId);
		getLog().info("Configuration: result:" + result);
		getLog().info("Configuration: preArgs:" + Arrays.toString(preArgs));
		getLog().info("Configuration: postArgs:" + Arrays.toString(postArgs));
		try {
			getLog().info(
					"Configuration: XJC loaded from:"
							+ Options.class.getResource("Options.class")
									.toURI().toURL().toExternalForm());
		} catch (IOException ignored) {
		} catch (URISyntaxException ignored) {
		}

	}

	protected List<String> getArguments() {
		final List<String> arguments = new ArrayList<String>();

		if (this.preArgs != null) {
			addAll(arguments,Arrays.asList(this.preArgs));
		}
		
		addAll(arguments,super.getArguments());

		if (generateInheritance)
			add(arguments,"-Xinheritance");

		if (generateAnnotation)
			add(arguments,"-Xannotate");

		if ("jpa3".equals(variant)) {
			add(arguments,"-Xhyperjaxb3-jpa3");

			if (result != null) {
				add(arguments,"-Xhyperjaxb3-jpa3-result=" + result);
			}

			if (roundtripTestClassName != null) {
				add(arguments,"-Xhyperjaxb3-jpa3-roundtripTestClassName="
						+ roundtripTestClassName);
			}
			if (persistenceUnitName != null) {
				add(arguments,"-Xhyperjaxb3-jpa3-persistenceUnitName="
						+ persistenceUnitName);
			}
			if (persistenceXml != null) {
				add(arguments,"-Xhyperjaxb3-jpa3-persistenceXml="
						+ persistenceXml.getAbsolutePath());
			}
		}
		else
		{
			if (!"ejb".equals(variant))
			{
				getLog().warn("Variant " + variant + " is obsolete, using 'variant=ejb' instead.");
				variant = "ejb";
			}

			add(arguments,"-Xhyperjaxb3-ejb");

			if (result != null) {
				add(arguments,"-Xhyperjaxb3-ejb-result=" + result);
			}

			if (roundtripTestClassName != null) {
				arguments.add("-Xhyperjaxb3-ejb-roundtripTestClassName="
						+ roundtripTestClassName);
			}
			if (persistenceUnitName != null) {
				add(arguments,"-Xhyperjaxb3-ejb-persistenceUnitName="
						+ persistenceUnitName);
			}
			if (persistenceXml != null) {
				add(arguments,"-Xhyperjaxb3-ejb-persistenceXml="
						+ persistenceXml.getAbsolutePath());
			}

			if (generateTransientId) {
				add(arguments,"-Xhyperjaxb3-ejb-generateTransientId=true");
			}
		}

		if (generateEquals)
		{
			switch (overrideHET)
			{
				case "strategic":
					add(arguments,"-Xequals");
					add(arguments,"-XhashCode");
					break;
				case "simple":
				case "simple1":
				case "simple2":
				case "simple3":
					add(arguments,"-XsimpleEquals");
					add(arguments,"-XsimpleHashCode");
					break;
			}
		}
		else if (generateHashCode)
		{
			switch (overrideHET)
			{
				case "strategic":
					add(arguments,"-XhashCode");
					break;
				case "simple":
				case "simple1":
				case "simple2":
				case "simple3":
					add(arguments,"-XsimpleHashCode");
					break;
			}
		}
		if (generateToString)
		{
			switch (overrideHET)
			{
				case "strategic":
					add(arguments,"-XtoString");
					break;
				case "simple":
					add(arguments,"-XsimpleToString");
					break;
				case "simple1":
					add(arguments,"-XsimpleToString");
					add(arguments,"-XsimpleToString-showChildItems=true");
					break;
				case "simple2":
					add(arguments,"-XsimpleToString");
					add(arguments,"-XsimpleToString-showChildItems=true");
					add(arguments,"-XsimpleToString-showFieldNames=true");
					break;
				case "simple3":
					add(arguments,"-XsimpleToString");
					add(arguments,"-XsimpleToString-showChildItems=true");
					add(arguments,"-XsimpleToString-showFieldNames=true");
					add(arguments,"-XsimpleToString-fullClassName=true");
					break;
			}
		}

		if (this.postArgs != null) {
			addAll(arguments,Arrays.asList(this.postArgs));
		}

		return arguments;
	}
	
	/**
	 * Add an item to the list, if not present.
	 * 
	 * @param list The list to receive the item.
	 * @param item The item to be added.
	 * 
	 * @return True when the item is added or is present; otherwise, false.
	 */
	private boolean add(List<String> list, String item)
	{
		boolean present = true;
		if ( !list.contains(item) )
			present = list.add(item);
		return present;
	}

	private void addAll(List<String> list, List<String> items)
	{
		for ( String item : items )
			add(list, item);
	}

	/**
	 * Updates XJC's compilePath and resources and update hyperjaxb2's
	 * resources, that is, *.hbm.xml files and hibernate.config.xml file.
	 * 
	 * @throws MojoExecutionException
	 */
	protected void setupMavenPaths() {
		super.setupMavenPaths();

		final Resource resource = new Resource();
		resource.setDirectory(getGenerateDirectory().getPath());
		for (String resourceInclude : resourceIncludes) {
			resource.addInclude(resourceInclude);
		}
		getProject().addResource(resource);

		// If a round trip class name is configured then
		// the XJC generation path is added as a test source root.
		// See also Maven's "testSourceDirectory".
		if (this.roundtripTestClassName != null) {
			getProject().addTestCompileSourceRoot(
					getGenerateDirectory().getPath());
		}
	}

    private void todo(Log logger, String comment) {
        String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
        String level = System.getProperty(TODO_LOG_LEVEL);
        if ( "DEBUG".equalsIgnoreCase(level) ) logger.debug(msg);
        else if ( "INFO".equalsIgnoreCase(level) ) logger.info(msg);
        else if ( "WARN".equalsIgnoreCase(level) ) logger.warn(msg);
        else if ( "ERROR".equalsIgnoreCase(level) ) logger.error(msg);
        else logger.error(msg);
    }
}
