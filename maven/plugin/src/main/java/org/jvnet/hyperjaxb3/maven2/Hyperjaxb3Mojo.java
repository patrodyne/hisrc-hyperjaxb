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
public class Hyperjaxb3Mojo extends XJC2Mojo
{
	public static final String HYPERJAXB_MOJO_PREFIX = "org.jvnet.hyperjaxb.mojo.xjc";

	/**
	 * Target directory for the generated mappings. If left empty, mappings are
	 * generated together with sources.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".target")
	private File target;
	public File getTarget() { return target; }
	public void setTarget(File target) { this.target = target; }

	/**
	 * Name of the roundtrip test case. If omitted, no roundtrip test case is
	 * generated.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".roundtripTestClassName")
	private String roundtripTestClassName;
	public String getRoundtripTestClassName() { return roundtripTestClassName; }
	public void setRoundtripTestClassName(String roundtripTestClassName) { this.roundtripTestClassName = roundtripTestClassName; }

	/**
	 * Patterns for files to be included as resources.
	 */
	@Parameter
	private String[] resourceIncludes = new String[]
		{
		 	"**/*.hbm.xml",
			"**/*.orm.xml",
			"**/*.cfg.xml",
			"META-INF/persistence.xml"
		};
	public String[] getResourceIncludes() { return resourceIncludes; }
	public void setResourceIncludes(String[] resourceIncludes) { this.resourceIncludes = resourceIncludes; }

	/**
	 * Persistence variant. Switches between various persistence
	 * implementations. Possible values are "hibernate" and "ejb-hibernate".
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".variant", defaultValue = "ejb")
	private String variant = "ejb";
	public String getVariant() { return variant; }
	public void setVariant(String variant) { this.variant = variant; }

	/**
	 * Persistence unit name (EJB3 specific).
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".persistenceUnitName")
	private String persistenceUnitName;
	public String getPersistenceUnitName() { return persistenceUnitName; }
	public void setPersistenceUnitName(String persistenceUnitName) { this.persistenceUnitName = persistenceUnitName; }

	/**
	 * Persistence unit name (EJB3 specific).
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".persistenceXml")
	private File persistenceXml;
	public File getPersistenceXml() { return persistenceXml; }
	public void setPersistenceXml(File persistenceXml) { this.persistenceXml = persistenceXml; }

	/**
	 * Whether to add or remove arbitrary annotations to/from generated sources.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".generateAnnotation", defaultValue = "true")
	private boolean generateAnnotation = true;
	public boolean isGenerateAnnotation() { return generateAnnotation; }
	public void setGenerateAnnotation(boolean generateAnnotation) { this.generateAnnotation = generateAnnotation; }

	/**
	 * Whether schema-derived classes extend certain class or implement certain interfaces.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".generateInheritance", defaultValue = "true")
	private boolean generateInheritance = true;
	public boolean isGenerateInheritance() { return generateInheritance; }
	public void setGenerateInheritance(boolean generateInheritance) { this.generateInheritance = generateInheritance; }

	/**
	 * Whether the <code>hashCode()</code> method should be generated.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".generateHashCode", defaultValue = "true")
	private boolean generateHashCode = true;
	public boolean isGenerateHashCode() { return generateHashCode; }
	public void setGenerateHashCode(boolean generateHashCode) { this.generateHashCode = generateHashCode; }

	/**
	 * Whether the <code>equals(...)</code> methods should be generated.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".generateEquals", defaultValue = "true")
	private boolean generateEquals = true;
	public boolean isGenerateEquals() { return generateEquals; }
	public void setGenerateEquals(boolean generateEquals) { this.generateEquals = generateEquals; }

	/**
	 * Whether the <code>toString()</code> methods should be generated.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".generateToString", defaultValue = "true")
	private boolean generateToString = true;
	public boolean isGenerateToString() { return generateToString; }
	public void setGenerateToString(boolean generateToString) { this.generateToString = generateToString; }

	/**
	 * Override HashCode, Equals, ToString. Possible values are "strategic", "simple[123]".
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".overrideHET", defaultValue = "strategic")
	private String overrideHET = "strategic";
	public String getOverrideHET() { return overrideHET; }
	public void setOverrideHET(String overrideHET) { this.overrideHET = overrideHET; }

	/**
	 * Whether the generated id property must be transient.
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".generateTransientId", defaultValue = "false")
	private boolean generateTransientId = false;
	public boolean isGenerateTransientId() { return generateTransientId; }
	public void setGenerateTransientId(boolean generateTransientId) { this.generateTransientId = generateTransientId; }

	/**
	 * Generation result. Possible values are "annotations", "mappingFiles".
	 */
	@Parameter(property = HYPERJAXB_MOJO_PREFIX + ".result", defaultValue = "annotations")
	private String result = "annotations";
	public String getResult() { return result; }
	public void setResult(String result) { this.result = result; }

	@Parameter
	public String[] preArgs = new String[0];
	public String[] getPreArgs() { return preArgs; }
	public void setPreArgs(String[] preArgs) { this.preArgs = preArgs; }

	@Parameter
	public String[] postArgs = new String[0];
	public String[] getPostArgs() { return postArgs; }
	public void setPostArgs(String[] postArgs) { this.postArgs = postArgs; }
	
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
	protected void logConfiguration() throws MojoExecutionException
	{
		super.logConfiguration();

		getLog().info("Configuration: target:" + getTarget());
		getLog().info("Configuration: roundtripTestClassName:" + getRoundtripTestClassName());
		getLog().info("Configuration: resourceIncludes:" + getResourceIncludes());
		getLog().info("Configuration: variant:" + getVariant());
		getLog().info("Configuration: persistenceUnitName:" + getPersistenceUnitName());
		getLog().info("Configuration: persistenceXml:" + getPersistenceXml());
		getLog().info("Configuration: generateInheritance:" + isGenerateInheritance());
		getLog().info("Configuration: generateAnnotation:" + isGenerateAnnotation());
		getLog().info("Configuration: generateHashCode:" + isGenerateHashCode());
		getLog().info("Configuration: generateEquals:" + isGenerateEquals());
		getLog().info("Configuration: generateToString:" + isGenerateToString());
		getLog().info("Configuration: generateHET:" + getOverrideHET());
		getLog().info("Configuration: generateTransientId:" + isGenerateTransientId());
		getLog().info("Configuration: result:" + getResult());
		getLog().info("Configuration: preArgs:" + Arrays.toString(getPreArgs()));
		getLog().info("Configuration: postArgs:" + Arrays.toString(getPostArgs()));
		try
		{
			getLog().info("Configuration: XJC loaded from:"
				+ Options.class.getResource("Options.class").toURI().toURL().toExternalForm());
		}
		catch (IOException ioex)
		{
			getLog().warn(ioex.getClass().getSimpleName()+": "+ioex.getMessage());
		}
		catch (URISyntaxException uriex)
		{
			getLog().warn(uriex.getClass().getSimpleName()+": "+uriex.getMessage());
		}
	}

	protected List<String> getArguments()
	{
		final List<String> arguments = new ArrayList<String>();

		if (getPreArgs() != null)
			addAll(arguments,Arrays.asList(getPreArgs()));
		
		addAll(arguments,super.getArguments());

		if (isGenerateInheritance())
			add(arguments,"-Xinheritance");
		if (isGenerateAnnotation())
			add(arguments,"-Xannotate");

		if ("jpa3".equals(getVariant()))
		{
			add(arguments,"-Xhyperjaxb3-jpa3");
			if (getResult() != null)
				add(arguments,"-Xhyperjaxb3-jpa3-result=" + getResult());
			if (getRoundtripTestClassName() != null)
				add(arguments,"-Xhyperjaxb3-jpa3-roundtripTestClassName=" + getRoundtripTestClassName());
			if (getPersistenceUnitName() != null)
				add(arguments,"-Xhyperjaxb3-jpa3-persistenceUnitName=" + getPersistenceUnitName());
			if (getPersistenceXml() != null)
				add(arguments,"-Xhyperjaxb3-jpa3-persistenceXml=" + getPersistenceXml().getAbsolutePath());
		}
		else
		{
			if (!"ejb".equals(getVariant()))
			{
				getLog().warn("Variant " + getVariant() + " is obsolete, using 'variant=ejb' instead.");
				setVariant("ejb");
			}

			add(arguments,"-Xhyperjaxb3-ejb");
			if (getResult() != null)
				add(arguments,"-Xhyperjaxb3-ejb-result=" + getResult());
			if (getRoundtripTestClassName() != null)
				arguments.add("-Xhyperjaxb3-ejb-roundtripTestClassName=" + getRoundtripTestClassName());
			if (getPersistenceUnitName() != null)
				add(arguments,"-Xhyperjaxb3-ejb-persistenceUnitName=" + getPersistenceUnitName());
			if (getPersistenceXml() != null)
				add(arguments,"-Xhyperjaxb3-ejb-persistenceXml=" + getPersistenceXml().getAbsolutePath());
			if (isGenerateTransientId())
				add(arguments,"-Xhyperjaxb3-ejb-generateTransientId=true");
		}

		if (isGenerateEquals())
		{
			switch (getOverrideHET())
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
		else if (isGenerateHashCode())
		{
			switch (getOverrideHET())
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
		if (isGenerateToString())
		{
			switch (getOverrideHET())
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

		if (getPostArgs() != null)
			addAll(arguments,Arrays.asList(getPostArgs()));

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
	protected void setupMavenPaths()
	{
		super.setupMavenPaths();

		final Resource resource = new Resource();
		resource.setDirectory(getGenerateDirectory().getPath());
		for (String resourceInclude : getResourceIncludes())
		{
			resource.addInclude(resourceInclude);
		}
		getProject().addResource(resource);

		// If a round trip class name is configured then
		// the XJC generation path is added as a test source root.
		// See also Maven's "testSourceDirectory".
		if (getRoundtripTestClassName() != null)
			getProject().addTestCompileSourceRoot(getGenerateDirectory().getPath());
	}

    private void todo(Log logger, String comment)
    {
        String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
        String level = System.getProperty(TODO_LOG_LEVEL);
        if ( "DEBUG".equalsIgnoreCase(level) ) logger.debug(msg);
        else if ( "INFO".equalsIgnoreCase(level) ) logger.info(msg);
        else if ( "WARN".equalsIgnoreCase(level) ) logger.warn(msg);
        else if ( "ERROR".equalsIgnoreCase(level) ) logger.error(msg);
        else logger.error(msg);
    }
}
