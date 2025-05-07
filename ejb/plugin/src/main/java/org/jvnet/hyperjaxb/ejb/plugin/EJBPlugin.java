package org.jvnet.hyperjaxb.ejb.plugin;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.jvnet.basicjaxb.lang.StringUtils.capitalize;
import static org.jvnet.basicjaxb.plugin.Customizations.IGNORED_ELEMENT_NAME;
import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.basicjaxb.util.CustomizationUtils.createCustomization;
import static org.jvnet.basicjaxb.util.GeneratorContextUtils.generateContextPathAwareClass;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.jpa.Customizations.ONE_TO_MANY_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.ONE_TO_ONE_ELEMENT_NAME;
import static org.jvnet.hyperjaxb_annox.plugin.annotate.AnnotatePlugin.ANNOTATE_PROPERTY_FIELD_QNAME;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.glassfish.jaxb.core.v2.model.core.ID;
import org.jvnet.basicjaxb.plugin.AbstractPlugin;
import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb.ejb.strategy.processor.ModelAndOutlineProcessor;
import org.jvnet.hyperjaxb.ejb.test.RoundtripTest;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.UntypedListFieldRenderer;
import org.w3c.dom.Element;
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
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.LocalScoping;

import jakarta.activation.MimeType;
import jakarta.xml.bind.annotation.XmlTransient;

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

	private boolean naiveInheritanceStrategy = false;
	public boolean isNaiveInheritanceStrategy() { return naiveInheritanceStrategy; }
	public void setNaiveInheritanceStrategy(boolean naiveInheritanceStrategy) { this.naiveInheritanceStrategy = naiveInheritanceStrategy; }

	// Applies only when roundtripTestClassName is set.
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
			if (args[i].length() != 0)
				if (args[i].charAt(0) != '-')
					if (args[i].endsWith(".jar"))
						episodeURLs.add(new File(args[i]).toURI().toURL());
		return result;
	}

	private Map<JDefinedClass, List<FieldOutline>> primaryIdMap;
	public Map<JDefinedClass, List<FieldOutline>> getPrimaryIdMap()
	{
		if ( primaryIdMap == null )
			setPrimaryIdMap(new HashMap<>());
		return primaryIdMap;
	}
	public void setPrimaryIdMap(Map<JDefinedClass, List<FieldOutline>> primaryIdMap)
	{
		this.primaryIdMap = primaryIdMap;
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
		else if ( TRUE.equals(isValidateXml()) )
			warn("Method isValidateXML() not generated because no roundtripTestClassName defined, isValidateXml: {}.", isValidateXml());
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

    /**
     * Performs the post-processing of the {@link Model}.
     *
     * <p>
     * This method is invoked after XJC has internally finished
     * the model construction. This is a chance for a plugin to
     * affect the way code generation is performed.
     * </p>
     *
     * <p>
     * Compared to the {@link #run(Outline, Options, ErrorHandler)}
     * method, this method allows a plugin to work at the higher level
     * conceptually closer to the abstract JAXB model, as opposed to
     * Java syntax level.
     * </p>
     *
     * <p>
     * This 'postProcessModel' method is a call-back method from
     * {@link AbstractPlugin} and that method is responsible for handling
     * all exceptions. It reports any exception to {@link ErrorHandler}
     * for processing by {@link com.sun.tools.xjc.Plugin}.
     * </p>
     *
     * <p>
     * <b>Note:</b> This method is invoked only when a plugin is activated.
     * </p>
     *
     * @param model
     *      The object that represents the classes/properties to
     *      be generated.
     */
	@Override
	public void postProcessModel(Model model)
	{
		setBgmBuilder(Ring.get(BGMBuilder.class));

		// Guard: JPA requires top level entities.
		if (LocalScoping.NESTED.equals(getBgmBuilder().getGlobalBinding().getFlattenClasses()))
			warn("According to the Java Persistence API specification, section 2.1, "
				+ "entities must be top-level classes:\n"
				+ "\"The entity class must be a top-level class.\"\n"
				+ "Your JAXB model is not customized as with top-level local scoping, "
				+ "please use the <jaxb:globalBinding localScoping=\"toplevel\"/> "
				+ "global bindings customization.");

		// Guard: JPA requires serializable entities.
		if (!model.serializable)
			warn("According to the Java Persistence API specification, section 2.1, "
				+ "entities must implement the serializable interface:\n"
				+ "\"If an entity instance is to be passed by value as a detached object\n"
				+ "(e.g., through a remote interface), the entity class must implement\n "
				+ "the Serializable interface.\"\n"
				+ "Your JAXB model is not customized as serializable, please use the "
				+ "<jaxb:serializable/> global bindings customization element to make your model serializable.");

		// Post process CClassInfo(s). Handle 'mappedBy', etc.
		for (final CClassInfo classInfo : model.beans().values())
			postProcessClassInfo(model, classInfo);
	}

	// Post process CClassInfo's properties. Handle 'mappedBy', etc.
	private void postProcessClassInfo(Model model, CClassInfo classInfo)
	{
		for (CPropertyInfo propertyInfo : classInfo.getProperties())
			postProcessPropertyInfo(model, classInfo, propertyInfo);
	}

	// Post process Model to generate the 'mappedBy' target field on the owning side.
	private void postProcessPropertyInfo(Model model, CClassInfo classInfo, CPropertyInfo propertyInfo)
	{
		for ( CPluginCustomization cpc : propertyInfo.getCustomizations() )
		{
			Element elm = cpc.element;
			QName elmQName = new QName(elm.getNamespaceURI(), elm.getLocalName());
			if ( ONE_TO_ONE_ELEMENT_NAME.equals(elmQName) || ONE_TO_MANY_ELEMENT_NAME.equals(elmQName) )
				postProcessOneToProperty(classInfo, propertyInfo, elm, elmQName);
		}
	}

	private static final Set<QName> INVOKE_ANNOTATE_QNAME_SET = org.jvnet.hyperjaxb_annox.plugin.annotate.AnnotatePlugin.CUSTOMIZATION_ELEMENT_QNAMES;

	private void postProcessOneToProperty(CClassInfo mappedBySideClassInfo,
		CPropertyInfo mappedBySidePropertInfo, Element mappedBySideElement,
		QName oneToName)
	{
		// Is this a bidirectional mapping?
		String mappedBy = mappedBySideElement.getAttribute("mapped-by");
		if ( (mappedBy != null) && !mappedBy.isBlank() )
		{
			// Check for an unambiguous type for the owner side.
			if ( mappedBySidePropertInfo.ref().size() == 1)
			{
				// 	Iterate list of TypeInfo(s) that this 'mapped-by' side attribute references.
				Iterator<? extends CTypeInfo> typeInfos = mappedBySidePropertInfo.ref().iterator();
				if ( typeInfos.hasNext() )
				{
					// Get the unambiguous 'owner-side' type.
					CClassInfo ownerSideType = (CClassInfo) typeInfos.next().getType();

					boolean hasInvokeAnnotationXmlTransient = false;
					boolean hasIgnoredElement = false;
					@SuppressWarnings("unused")
					boolean hasToOneElement = false;

					// Check for an existing 'mappedBy' property bound to an element.
					CPropertyInfo ownerSidePropertyInfo = ownerSideType.getProperty(mappedBy);
					CElementPropertyInfo ownerSideElementInfo = (CElementPropertyInfo) ownerSidePropertyInfo;
					if ( ownerSideElementInfo != null )
					{
						for ( CPluginCustomization cpc : ownerSideElementInfo.getCustomizations() )
						{
							Element elm = cpc.element;
							QName elmQName = new QName(elm.getNamespaceURI(), elm.getLocalName());
							if ( INVOKE_ANNOTATE_QNAME_SET.contains(elmQName) )
							{
								String xtName = XmlTransient.class.getName();
								String acName = elm.getAttribute("class");
								String elmText = elm.getTextContent();
								if ( (acName != null) && acName.trim().equals(xtName) )
									hasInvokeAnnotationXmlTransient = true;
								else if ( (elmText != null) && elmText.trim().startsWith("@"+xtName) )
									hasInvokeAnnotationXmlTransient = true;
							}
							else if ( IGNORED_ELEMENT_NAME.equals(elmQName) )
								hasIgnoredElement = true;
							else if ( oneToName.equals(elmQName) )
								hasToOneElement = true;
						}
					}
					else
					{
						// add ownerSideElementInfo to ownerSideType
						MimeType mimeType = null;
						boolean required = false;
						CCustomizations customizations = new CCustomizations();
						ownerSideElementInfo = new CElementPropertyInfo
						(
							capitalize(mappedBy),
							CollectionMode.NOT_REPEATED,
							ID.NONE,
							mimeType,
							mappedBySidePropertInfo.getSchemaComponent(),
							customizations,
							mappedBySidePropertInfo.getLocator(),
							required
						);
						CTypeRef typeRef = new CTypeRef
						(
							mappedBySideClassInfo,
							mappedBySideClassInfo.getElementName(),
							mappedBySideClassInfo.getTypeName(),
							false,
							null
						);
						ownerSideElementInfo.getTypes().add(typeRef);
						ownerSideType.addProperty(ownerSideElementInfo);
					}

					// @XmlTransient avoids cyclic marshalling, etc.
					if ( !hasInvokeAnnotationXmlTransient )
					{
						CPluginCustomization cpcInvokeAnnotation = createCustomization
						(
							ANNOTATE_PROPERTY_FIELD_QNAME,
							ownerSideElementInfo.getLocator()
						);
						cpcInvokeAnnotation.element.setTextContent("@"+XmlTransient.class.getName());
						ownerSideElementInfo.getCustomizations().add(cpcInvokeAnnotation);
					}

					// bas:ignored (http://jvnet.org/basicjaxb/xjc/ignored) avoids cyclic hashing, etc.
					if ( !hasIgnoredElement )
					{
						CPluginCustomization cpcIgnored = createCustomization
						(
							IGNORED_ELEMENT_NAME,
							ownerSideElementInfo.getLocator()
						);
						ownerSideElementInfo.getCustomizations().add(cpcIgnored);
					}
				}
			}
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
			sb.append("\n  NaiveInheritanceStrategy.: " + isNaiveInheritanceStrategy());
			sb.append("\n  MaxIdentifierLength......: " + getMaxIdentifierLength());
			sb.append("\n  PersistenceUnitName......: " + getPersistenceUnitName());
			sb.append("\n  PersistenceXml...........: " + getPersistenceXml());
			sb.append("\n  Result...................: " + getResult());
			sb.append("\n  RoundtripTestClassName...: " + getRoundtripTestClassName());
			if ( getRoundtripTestClassName() != null )
				sb.append("\n    ValidateXml.: " + isValidateXml());
			else
				sb.append("\n    ValidateXml.: " + isValidateXml() + " (ignored)");
			sb.append("\n  TargetDir................: " + getTargetDir());
			sb.append("\n  Verbose..................: " + isVerbose());
			sb.append("\n  Debug....................: " + isDebug());
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

		// Additional CClassInfo processing.
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

			// Generate field declarations, when needed.
			for (final CPropertyInfo propertyInfo : classInfo.getProperties())
				if ( outline.getField(propertyInfo) == null )
					generateFieldDecl(outline, coi,	propertyInfo);

			// Build a map of primary identifier FieldOutlines for
			// each ClassOutlineImpl
			List<FieldOutline> fidList = new ArrayList<>();
			List<CPropertyInfo> pidList = collectIdPropertyInfo(classInfo);
			for ( CPropertyInfo pid : pidList )
			{
				FieldOutline pidField = outline.getField(pid);
				if ( pidField != null )
					fidList.add(pidField);
			}
			getPrimaryIdMap().put(coi.implClass, fidList);
		}

		// Process com.sun.tools.xjc.outline.Outline
		getModelAndOutlineProcessor().process(this, outline);
		generateRoundtripTestClass(outline);
		checkCustomizations(outline);

		return !hadError(outline.getErrorReceiver());
	}

	public static QName JPA_IGNORED_ELEMENT_NAME = Customizations.IGNORED_ELEMENT_NAME;
	public static QName JPA_ID_ELEMENT_NAME = Customizations.ID_ELEMENT_NAME;
	public static QName JPA_EMBEDDED_ID_ELEMENT_NAME = Customizations.EMBEDDED_ID_ELEMENT_NAME;
	public static QName JPA_GENERATED_ID_ELEMENT_NAME = Customizations.GENERATED_ID_ELEMENT_NAME;

	private List<CPropertyInfo> collectIdPropertyInfo(CClassInfo classInfo)
	{
		final List<CPropertyInfo> ids = new LinkedList<CPropertyInfo>();

		if (classInfo.getBaseClass() != null)
			ids.addAll(collectIdPropertyInfo(classInfo.getBaseClass()));

		if ( !containsCustomization(classInfo, JPA_IGNORED_ELEMENT_NAME) )
			for ( CPropertyInfo propertyInfo : classInfo.getProperties() )
				if
				(
					(
						containsCustomization(propertyInfo, JPA_ID_ELEMENT_NAME) ||
						containsCustomization(propertyInfo, JPA_EMBEDDED_ID_ELEMENT_NAME) ||
						containsCustomization(propertyInfo, JPA_GENERATED_ID_ELEMENT_NAME)
					)
					&& !containsCustomization(propertyInfo, JPA_IGNORED_ELEMENT_NAME)
				) ids.add(propertyInfo);
		return ids;
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
				if ( !implClass.fields().containsKey("serialVersionUID") )
				{
					implClass.field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
						codeModel.LONG, "serialVersionUID",
						JExpr.lit(model.serialVersionUID));
					if ( isDebugEnabled() )
						if ( coi.getTarget() instanceof CClassInfo )
						{
							CClassInfo coiTarget = (CClassInfo) coi.getTarget();
							debug("{}, generateClassSerializable; Class={}",
								toLocation(coiTarget), coiTarget.shortName);
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
				if ( fieldParent.getSchemaComponent() != null )
					locator = fieldParent.getSchemaComponent().getLocator();
				else
					locator = fieldParent.getLocator();
			String className = fieldOutline.parent().getImplClass().name();
			String fieldName = fieldInfo.getName(false);
			debug("{}, generateFieldDecl; Class={}, Field={}",
				toLocation(locator), className, fieldName);
		}

		return fieldOutline;
	}
}
