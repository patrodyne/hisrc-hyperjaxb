package org.jvnet.hyperjaxb.ejb.plugin;

import static java.lang.String.format;
import static org.jvnet.basicjaxb.util.GeneratorContextUtils.generateContextPathAwareClass;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb.ejb.strategy.processor.ModelAndOutlineProcessor;
import org.jvnet.hyperjaxb.ejb.test.RoundtripTest;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.UntypedListFieldRenderer;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.generator.bean.BeanGenerator;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.generator.bean.field.FieldRendererFactory;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.LocalScoping;

/**
 * An XJC plugin to add EJB annotations to JAXB generated classes.
 */
public class EJBPlugin extends AbstractWeldCDIPlugin
{
	/** Name of Option to enable this plugin. */
	private static final String OPTION_NAME = "Xhyperjaxb-ejb";
	
	/** Description of Option to enable this plugin. */
	private static final String OPTION_DESC = "add EJB annotations to JAXB generated classes";
	
	@Override
	public String getOptionName()
	{
		return OPTION_NAME;
	}

	@Override
	public String getUsage()
	{
		return format(USAGE_FORMAT, OPTION_NAME, OPTION_DESC);
	}

	private Boolean validateXml = null;
	public Boolean isValidateXml() { return validateXml; }
	public void setValidateXml(Boolean validateXml) { this.validateXml = validateXml; }
	
	private Integer maxIdentifierLength = null;
	public Integer getMaxIdentifierLength() { return maxIdentifierLength; }
	public void setMaxIdentifierLength(Integer maxIdentifierLength) { this.maxIdentifierLength = maxIdentifierLength; }

	private String roundtripTestClassName;
	public String getRoundtripTestClassName() { return roundtripTestClassName; } 
	public void setRoundtripTestClassName(String rt) { this.roundtripTestClassName = rt; }

	private String persistenceUnitName;
	public String getPersistenceUnitName() { return persistenceUnitName; }
	public void setPersistenceUnitName(String persistenceUnitName) { this.persistenceUnitName = persistenceUnitName; }

	private File targetDir;
	public File getTargetDir() { return targetDir; }
	public void setTargetDir(File targetDir) { this.targetDir = targetDir; }

	private File persistenceXml;
	public File getPersistenceXml() { return persistenceXml; }
	public void setPersistenceXml(File persistenceXml) { this.persistenceXml = persistenceXml; }
	
	private String result = "annotations";
	public String getResult() { return result; }
	public void setResult(String variant) { this.result = variant; }

	public String getModelAndOutlineProcessorBeanName()
	{
		return getResult();
	}

	private String[] mergePersistenceUnits = new String[0];
	public String[] getMergePersistenceUnits() { return mergePersistenceUnits; }
	public void setMergePersistenceUnits(String[] mergePersistenceUnits) { this.mergePersistenceUnits = mergePersistenceUnits; }

	private Collection<ClassOutline> includedClasses;
	public Collection<ClassOutline> getIncludedClasses() { return includedClasses; }
	public void setIncludedClasses(Collection<ClassOutline> includedClasses) { this.includedClasses = includedClasses; }

	private Collection<CClassInfo> createdClasses = new LinkedList<CClassInfo>();
	public Collection<CClassInfo> getCreatedClasses() { return createdClasses; }
	
	private Map<CPropertyInfo, CClassInfo> createdProperties = new HashMap<CPropertyInfo, CClassInfo>();
	public Map<CPropertyInfo, CClassInfo> getCreatedProperties() { return createdProperties; }

	private ModelAndOutlineProcessor<EJBPlugin> modelAndOutlineProcessor;
	public ModelAndOutlineProcessor<EJBPlugin> getModelAndOutlineProcessor() { return modelAndOutlineProcessor; }
	public void setModelAndOutlineProcessor( ModelAndOutlineProcessor<EJBPlugin> modelAndOutlineProcessor) { this.modelAndOutlineProcessor = modelAndOutlineProcessor; }

	private Mapping mapping;
	public Mapping getMapping() { return mapping; }
	public void setMapping(Mapping mapping) { this.mapping = mapping; }

	private Naming naming;
	public Naming getNaming() { return naming; }
	public void setNaming(Naming naming) { this.naming = naming; }

	@Override
	public List<String> getCustomizationURIs()
	{
		final List<String> customizationURIs = new LinkedList<String>();
		customizationURIs.addAll(super.getCustomizationURIs());
		customizationURIs.addAll(Customizations.NAMESPACES);
		return customizationURIs;
	}

	@Override
	public boolean isCustomizationTagName(String namespace, String localPart)
	{
		return super.isCustomizationTagName(namespace, localPart) ||
			Customizations.NAMESPACES.contains(namespace);
	}

	// Represents the root of the XML Schema binder (Bean Generator Model).
	private BGMBuilder bgmBuilder;
	public BGMBuilder getBgmBuilder() { return bgmBuilder; }
	public void setBgmBuilder(BGMBuilder bgmBuilder) { this.bgmBuilder = bgmBuilder; }

	private List<URL> episodeURLs = new LinkedList<URL>();
	@Override
	public int parseArgument(Options opt, String[] args, int start)
		throws BadCommandLineException, IOException
	{
		final int result = super.parseArgument(opt, args, start);

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].length() != 0)
			{
				if (args[i].charAt(0) != '-')
				{
					if (args[i].endsWith(".jar"))
						episodeURLs.add(new File(args[i]).toURI().toURL());
				}
			}
		}
		return result;
	}
	
	/**
	 * Generate a code model for a context-path aware JDefinedClass and
	 * add methods to configure the persistence unit name and the
	 * schema validation mode.
	 * 
	 * @param outline Captures the generated code for the current model root.
	 */
	private void generateRoundtripTestClass(Outline outline)
	{
		if (getRoundtripTestClassName() != null)
		{
			// Add 'public String getContextPath()' 
			final JDefinedClass roundtripTestClass =
				generateContextPathAwareClass(outline, getRoundtripTestClassName(), RoundtripTest.class);

			// Add 'public String getPersistenceUnitName()' 
			final String persistenceUnitName =
				( getPersistenceUnitName() != null )
				? getPersistenceUnitName()
				: getNaming().getPersistenceUnitName(getMapping(), outline);
			JMethod getPersistenceUnitName = roundtripTestClass.method(
				JMod.PUBLIC, outline.getCodeModel().ref(String.class), "getPersistenceUnitName");
			getPersistenceUnitName.body()._return(JExpr.lit(persistenceUnitName));
			
			// Add 'public boolean isValidateXml()'
			if ( isValidateXml() != null )
			{
				JMethod isValidateXml = roundtripTestClass.method(
					JMod.PUBLIC, outline.getCodeModel().ref(Boolean.class), "isValidateXml");
				isValidateXml.body()._return(JExpr.lit(isValidateXml()));
			}
		}
	}

	private void checkCustomizations(Outline outline)
	{
		for (final CClassInfo classInfo : outline.getModel().beans().values())
		{
			checkCustomizations(classInfo);
			for (final CPropertyInfo propertyInfo : classInfo.getProperties())
				checkCustomizations(classInfo, propertyInfo);
		}
	}

	private void checkCustomizations(CClassInfo classInfo, CPropertyInfo propInfo)
	{
		Map<CPluginCustomization, String> infoMap = CustomizationUtils.getInfo("check", classInfo, propInfo);
		for (CPluginCustomization customization : infoMap.keySet())
		{
			final String namespace = customization.element.getNamespaceURI();
			if (!customization.isAcknowledged())
			{
				if ( Customizations.NAMESPACE_URI.equals(namespace) )
					warn("Unacknowledged customization {}", infoMap.get(customization));
				else
					debug("Unacknowledged customization pending {}", infoMap.get(customization));
				customization.markAsAcknowledged();
			}
		}
	}

	private void checkCustomizations(CClassInfo classInfo)
	{
		Map<CPluginCustomization, String> infoMap = CustomizationUtils.getInfo("check", classInfo);
		for (CPluginCustomization customization : infoMap.keySet())
		{
			final String namespace = customization.element.getNamespaceURI();
			if (!customization.isAcknowledged())
			{
				if ( Customizations.NAMESPACE_URI.equals(namespace) )
					warn("Unacknowledged customization {}", infoMap.get(customization));
				else
					debug("Unacknowledged customization pending {}", infoMap.get(customization));
				customization.markAsAcknowledged();
			}
		}
	}

	@Override
	public void postProcessModel(Model model, ErrorHandler errorHandler)
	{
		setBgmBuilder(Ring.get(BGMBuilder.class));

		if (LocalScoping.NESTED.equals(getBgmBuilder().getGlobalBinding().getFlattenClasses()))
		{
			warn("According to the Java Persistence API specification, section 2.1, "
				+ "entities must be top-level classes:\n"
				+ "\"The entity class must be a top-level class.\"\n"
				+ "Your JAXB model is not customized as with top-level local scoping, "
				+ "please use the <jaxb:globalBinding localScoping=\"toplevel\"/> "
				+ "global bindings customization.");
		}

		final boolean serializable = model.serializable;

		if (!serializable)
		{
			warn("According to the Java Persistence API specification, section 2.1, "
				+ "entities must implement the serializable interface:\n"
				+ "\"If an entity instance is to be passed by value as a detached object\n"
				+ "(e.g., through a remote interface), the entity class must implement\n "
				+ "the Serializable interface.\"\n"
				+ "Your JAXB model is not customized as serializable, please use the "
				+ "<jaxb:serializable/> global bindings customization element to make your model serializable.");
		}
	}

	@Override
	public void onActivated(Options options) throws BadCommandLineException
	{
		Thread.currentThread().setContextClassLoader( getClass().getClassLoader());

		super.onActivated(options);

		final FieldRendererFactory fieldRendererFactory = new FieldRendererFactory()
		{
			@Override
			public FieldRenderer getList(JClass coreList)
			{
				return new UntypedListFieldRenderer(coreList);
			}
		};
		
		options.setFieldRendererFactory(fieldRendererFactory, this);
	}

	@Override
	protected void beforeRun(Outline outline) throws Exception
	{
		// Configure Dependency Injection Context
		super.beforeRun(outline);
		
		if ( isInfoEnabled() )
		{
			StringBuilder sb = new StringBuilder();
			sb.append(LOGGING_START);
			sb.append("\nParameters");
			sb.append("\n  MaxIdentifierLength....: " + getMaxIdentifierLength());
			sb.append("\n  PersistenceUnitName....: " + getPersistenceUnitName());
			sb.append("\n  PersistenceXml.........: " + getPersistenceXml());
			sb.append("\n  Result.................: " + getResult());
			sb.append("\n  RoundtripTestClassName.: " + getRoundtripTestClassName());
			sb.append("\n  TargetDir..............: " + getTargetDir());
			sb.append("\n  ValidateXml............: " + isValidateXml());
			sb.append("\n  Verbose................: " + isVerbose());
			sb.append("\n  Debug..................: " + isDebug());
			info(sb.toString());
		}
		
		// Wire this XJC plugin into the CDI strategy producer.
		getStrategyProducer().setPlugin(this);

		// Log context, if enabled.
		logContext(getStrategyService().getNaming());
		logContext(getStrategyService().getMappingContext());
		logContext(getStrategyService().getModelAndOutlineProcessor());

		// Configure CDI strategy context.
		setNaming(getStrategyService().getNaming());
		setMapping((Mapping) getStrategyService().getMappingContext());
		getMapping().setPlugin(this);
		setModelAndOutlineProcessor(getStrategyService().getModelAndOutlineProcessor());

		// Set target directory, when needed.
		if (getTargetDir() == null)
			setTargetDir(getOptions().targetDir);

		// Configure XJC Plugin properties from XJC parameter(s)
		// and/or from Maven Mojo settings.
		if ( getMaxIdentifierLength() != null )
			getNaming().setMaxIdentifierLength(getMaxIdentifierLength());
	}

	@Override
	protected void afterRun(Outline outline) throws Exception
	{
		super.afterRun(outline);
		if ( isInfoEnabled() )
		{
			StringBuilder sb = new StringBuilder();
			sb.append(LOGGING_FINISH);
			sb.append("\nResults");
			sb.append("\n  HadError.: " + hadError(outline.getErrorReceiver()));
			info(sb.toString());
		}
	}
	
	@Override
	public boolean run(Outline outline) throws Exception
	{
		final Ring ring = Ring.begin();

		// Process com.sun.tools.xjc.model.Model
		try
		{
			Ring.add(getBgmBuilder());
			Ring.add(outline.getModel());
			getModelAndOutlineProcessor().process(this, outline.getModel());
		}
		finally
		{
			Ring.end(ring);
		}

		for (final CClassInfo classInfo : getCreatedClasses())
		{
			final ClassOutline classOutline = outline.getClazz(classInfo);
			
			// Generate class body and class serializable.
			ClassOutlineImpl coi = (ClassOutlineImpl) classOutline;
			
			// Note: org.jvnet.hyperjaxb.ejb.strategy.model.base.CreateIdClass
			//       is marked ignored but needs serialVersionUID, too.
			//       So check every ClassOutlineImpl for it. 
			generateClassSerializable(outline, coi);
			
			if (Customizations.isGenerated(classInfo))
				generateClassBody(outline, coi);

			for (final CPropertyInfo propertyInfo : classInfo.getProperties())
			{
				if (outline.getField(propertyInfo) == null)
					generateFieldDecl(outline, (ClassOutlineImpl) classOutline,	propertyInfo);
			}
		}

		// Process com.sun.tools.xjc.outline.Outline
		getModelAndOutlineProcessor().process(this, outline);
		generateRoundtripTestClass(outline);
		checkCustomizations(outline);
		
		return !hadError(outline.getErrorReceiver());
	}
	
	// if serialization support is turned on, generate
	// [RESULT]
	// class ... implements Serializable
	// {
	//     private static final long serialVersionUID = <id>;
	//     ....
	// }
	private void generateClassSerializable(Outline outline, ClassOutlineImpl coi)
	{
		final JCodeModel codeModel = outline.getCodeModel();
		final Model model = outline.getModel();
		final JDefinedClass implClass = coi.implClass;
		
		if (model.serializable)
		{
			implClass._implements(Serializable.class);
			if (model.serialVersionUID != null)
			{
				if ( !implClass.fields().containsKey("serialVersionUID") )
				{
					implClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
						codeModel.LONG, "serialVersionUID",
						JExpr.lit(model.serialVersionUID));
					if ( isDebugEnabled() )
					{
						if ( coi.getTarget() instanceof CClassInfo )
						{
							CClassInfo coiTarget = (CClassInfo) coi.getTarget();
							debug("{}, generateClassSerializable; Class={}",
								toLocation(coiTarget), coiTarget.shortName);
						}
					}
				}
			}
		}
	}

	private void generateClassBody(Outline outline, ClassOutlineImpl coi)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		CClassInfo target = coi.target;
		
		for (CPropertyInfo prop : target.getProperties())
			generateFieldDecl(outline, coi, prop);

		assert !target.declaresAttributeWildcard();
		
		debug("{}, generateClassBody; Class={}", toLocation(target), target.shortName);
	}
	
	// Represents a generate field declaration method that is initialized in an
	// instance initializer block.
	private final Method generateFieldDecl;
	{
		try
		{
			generateFieldDecl = BeanGenerator.class.getDeclaredMethod("generateFieldDecl",
				new Class[] { ClassOutlineImpl.class, CPropertyInfo.class });
			generateFieldDecl.setAccessible(true);
		}
		catch (Exception ex)
		{
			throw new ExceptionInInitializerError(ex);

		}
	}
	
	private FieldOutline generateFieldDecl(Outline outline, ClassOutlineImpl cc, CPropertyInfo propInfo)
		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		FieldOutline fieldOutline = (FieldOutline) generateFieldDecl
			.invoke(outline, new Object[] { cc, propInfo });
		
		if ( isDebugEnabled() )
		{
			CPropertyInfo fieldInfo = fieldOutline.getPropertyInfo();
			CTypeInfo fieldParent = fieldInfo.parent();
			Locator locator = fieldInfo.getLocator();
			if ( locator == null )
			{
				if ( fieldParent.getSchemaComponent() != null )
					locator = fieldParent.getSchemaComponent().getLocator();
				else
					locator = fieldParent.getLocator();
			}
			String className = fieldOutline.parent().getImplClass().name();
			String fieldName = fieldInfo.getName(false);
			debug("{}, generateFieldDecl; Class={}, Field={}",
				toLocation(locator), className, fieldName);
		}
		
		return fieldOutline;
	}
}
