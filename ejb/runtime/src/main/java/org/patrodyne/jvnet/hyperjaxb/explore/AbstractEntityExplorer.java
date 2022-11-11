package org.patrodyne.jvnet.hyperjaxb.explore;

import static java.lang.Integer.toHexString;
import static java.lang.System.identityHashCode;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.filterProperties;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesBaseFile;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesMoreFile;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;

import jakarta.persistence.Cache;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jvnet.hyperjaxb.ejb.util.Transactional.CacheOption;
import org.patrodyne.jvnet.basicjaxb.explore.AbstractExplorer;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputDomResolver;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputStringResolver;
import org.xml.sax.SAXException;

/**
 * An abstract Swing JFrame to support exploration of HiSrc HyperJAXB libraries.
 * This class provides JPanes to:
 * 
 * <ol>
 *   <li>Display HTML documentation.</li>
 *   <li>Organize output streams.</li>
 * </ol>
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
abstract public class AbstractEntityExplorer extends AbstractExplorer
{
	public static final String EXPLORER_HTML = "Explorer.html";
	public static final String HIBERNATE_PUN = "hibernate.ejb.persistenceUnitName";
	public static final String PERSISTENCE_H2_PROPERTIES = "persistence-h2.properties";
	public static final String PERSISTENCE_PG_PROPERTIES = "persistence-pg.properties";
	public static final Random RANDOM = new Random();
	
	private JAXBContext jaxbContext;
	public JAXBContext getJaxbContext() { return jaxbContext; }
	public void setJaxbContext(JAXBContext jaxbContext) { this.jaxbContext = jaxbContext; }

	private Unmarshaller unmarshaller;
	public Unmarshaller getUnmarshaller() { return unmarshaller; }
	public void setUnmarshaller(Unmarshaller unmarshaller) { this.unmarshaller = unmarshaller; }
	
	private Marshaller marshaller;
	public Marshaller getMarshaller() { return marshaller; }
	public void setMarshaller(Marshaller marshaller) { this.marshaller = marshaller; }

	private String persistenceUnitName;
	public String getPersistenceUnitName() { return persistenceUnitName; }
	public void setPersistenceUnitName(String persistenceUnitName) { this.persistenceUnitName = persistenceUnitName; }

	private Map<String,String> entityManagerFactoryProperties;
	public Map<String, String> getEntityManagerFactoryProperties() { return entityManagerFactoryProperties; }
	public void setEntityManagerFactoryProperties(Map<String, String> entityManagerFactoryProperties) { this.entityManagerFactoryProperties = entityManagerFactoryProperties; }

	private EntityManagerFactory entityManagerFactory;
	public EntityManagerFactory getEntityManagerFactory() { return entityManagerFactory; }
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) { this.entityManagerFactory = entityManagerFactory; }

	private EntityManager entityManager;
	public EntityManager getEntityManager() { return entityManager; }
	public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager; }

	private Map<String, Object> externalContextProperties;
	public Map<String, Object> getExternalContextProperties() { return externalContextProperties; }
	public void setExternalContextProperties(Map<String, Object> externalContextProperties) { this.externalContextProperties = externalContextProperties; }

	private Map<String, Object> internalContextProperties;
	public Map<String, Object> getInternalContextProperties() { return internalContextProperties; }
	public void setInternalContextProperties(Map<String, Object> internalContextProperties) { this.internalContextProperties = internalContextProperties; }
	
	/**
	 * Construct application
	 */
	public AbstractEntityExplorer(String htmlName)
	{
		super(htmlName);
	}
	
	public void generateXmlSchemaFromString()
	{
		try
		{
			SchemaOutputStringResolver sosr = new SchemaOutputStringResolver();
			getJaxbContext().generateSchema(sosr);
			println("Xml Schema from String:\n\n" + sosr.getSchemaString());
		}
		catch ( IOException ex )
		{
			errorln(ex);
		}
	}
	
	public void generateXmlSchemaFromDom()
	{
		try
		{
			SchemaOutputDomResolver sodr = new SchemaOutputDomResolver();
			getJaxbContext().generateSchema(sodr);
			println("Xml Schema from DOM:\n\n" + sodr.getSchemaDomNodeString());
		}
		catch ( IOException | TransformerException ex )
		{
			errorln(ex);
		}
	}
	
	public void generateXmlSchemaValidatorFromDom()
	{
		try
		{
			if ( (getMarshaller() != null) && (getUnmarshaller() != null) )
			{
				// Generate a Schema Validator from given the JAXB context.
				SchemaOutputDomResolver sodr = new SchemaOutputDomResolver();
				getJaxbContext().generateSchema(sodr);
				SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
				Schema schemaValidator = schemaFactory.newSchema(sodr.getDomSource());
				
				// Configure Marshaller / unmarshaller to use validator.
				getMarshaller().setSchema(schemaValidator);
				getUnmarshaller().setSchema(schemaValidator);
				
				getValidateButton().setSelected(true);
				println("Schema Validation is ON.");
			}
			else
				errorln("Please create marshaller and unmarshaller!");
		}
		catch ( IOException | SAXException ex )
		{
			errorln(ex);
		}
	}
	
	public void displayEntityManagerFactoryProperties()
	{
		// Properties: JPA Entity Manager Factory
		println("\nPersistence Configuration Properties, External:\n");
		for ( Entry<String, Object> entry : getExternalContextProperties().entrySet() )
			println("  " + entry.getKey() + " = " + entry.getValue());
		
		// Properties: Hibernate Session Factory
		println("\nPersistence Configuration Properties, Internal:\n");
		for ( Entry<String, Object> entry : getInternalContextProperties().entrySet() )
			println("  " + entry.getKey() + " = " + entry.getValue());
	}

	protected String identify(Object object)
	{
		String objectId = toHexString(identityHashCode(object));
		String entityId = toHexString(object.hashCode());
		return "Object Id = " + objectId + " -> Entity Id = " + entityId;
	}

	protected String resolvePerstenceUnitName(Class<?> objectFactoryClass)
	{
		String puName = getEntityManagerFactoryProperties().get(HIBERNATE_PUN);
		if ( puName == null )
			puName = objectFactoryClass.getPackageName();
		return puName;
	}

	protected CacheOption reuseCache()
	{
		return getClean1stLevelCacheButton().isSelected() ? CacheOption.REUSE : CacheOption.CLEAN;
	}
	
	protected Map<String, String> loadEntityManagerFactoryProperties()
	{
		final String propertiesBaseFile = getPersistencePropertiesBaseFile();
		final Properties persistenceProperties = new Properties();
		try ( InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesBaseFile) )
		{
			if ( is != null )
				persistenceProperties.load(is);
			else
				errorln("Load error: " + propertiesBaseFile);
		}
		catch (IOException ex)
		{
			errorln(ex);
		}
		
		final String propertiesMoreFile = getPersistencePropertiesMoreFile(PERSISTENCE_H2_PROPERTIES);
		final Properties persistencePropertiesMore = new Properties();
		try ( InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesMoreFile) )
		{
			if ( is != null )
			{
				persistencePropertiesMore.load(is);
				persistenceProperties.putAll(persistencePropertiesMore);
			}
			else
				println("Not found: " + propertiesMoreFile);
		}
		catch (IOException ex)
		{
			errorln(ex);
		}

		// Convert Properties to Map<String, String>
		return persistenceProperties.entrySet().stream().collect
		(
			Collectors.toMap
			(
				e -> String.valueOf(e.getKey()),
				e -> String.valueOf(e.getValue()),
				(prev, next) -> next, HashMap::new
			)
		);
	}

	// Properties: JPA Entity Manager Factory
	protected Map<String, Object> filterExternalProperties()
	{
		return filterProperties(getEntityManagerFactory());
	}

	@SuppressWarnings("unchecked")
	protected <T> T getContextProperty(String name, T defaultValue)
	{
		Object value = getInternalContextProperties().get(name);
		if ( value != null )
			return (T) value;
		else
			return defaultValue;
	}

	protected EntityManagerFactory createEntityManagerFactory()
	{
		return Persistence.createEntityManagerFactory(
			getPersistenceUnitName(), getEntityManagerFactoryProperties());
	}
	
	protected EntityManager createEntityManager()
	{
		return getEntityManagerFactory().createEntityManager();
	}
	
	protected JAXBContext createJAXBContext(Class<?>[] classesToBeBound)
	{
		JAXBContext jaxbContext = null;
		try
		{
			jaxbContext = JAXBContext.newInstance(classesToBeBound);
		}
		catch ( JAXBException ex)
		{
			errorln(ex);
		}
		return jaxbContext;
	}

	protected Marshaller createMarshaller(JAXBContext jaxbContext)
	{
		Marshaller marshaller = null;
		try
		{
			if ( jaxbContext != null )
			{
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
			}
			else
				errorln("Cannot create marshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			errorln(ex);
		}
		return marshaller;
	}
	
	protected Unmarshaller createUnmarshaller(JAXBContext jaxbContext)
	{
		Unmarshaller unmarshaller = null;
		try
		{
			if ( jaxbContext != null )
			{
				unmarshaller = jaxbContext.createUnmarshaller();
			}
			else
				errorln("Cannot create unmarshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			errorln(ex);
		}
		return unmarshaller;
	}
	
	protected void marshal(String label, Serializable instance)
	{
		String ehc = toHexString(instance.hashCode());
		String ihc = toHexString(identityHashCode(instance));
		String productXml = marshalToString(instance);
		// Entity Hash vs Object Hash
		println(label + " Hash = [ E#=" + ehc + ", O#=" + ihc + " ]\n" +productXml);
	}
	
	protected String marshalToString(Object instance)
	{
		String xml = null;
		if ( instance != null)
		{
			try ( StringWriter writer = new StringWriter() )
			{
				getMarshaller().marshal(instance, writer);
				xml = writer.toString();
			}
			catch (JAXBException | IOException ex)
			{
				errorln(ex);
			}
		}
		return xml;
	}

	@SuppressWarnings("unchecked")
	protected <T> T unmarshalFromFile(File file)
	{
		T instance = null;
		try
		{
			instance = (T) getUnmarshaller().unmarshal(file);
		}
		catch (JAXBException ex)
		{
			errorln(ex);
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T unmarshalFromString(String xml, Class<?> clazz)
	{
		T instance = null;
		try ( StringReader reader = new StringReader(xml) )
		{
			instance = (T) getUnmarshaller().unmarshal(new StreamSource(reader), clazz).getValue();
		}
		catch (JAXBException ex)
		{
			errorln(ex);
		}
		return instance;
	}
	
	/**
	 * Print the management state of a given entity.
	 * 
	 * @param indent An padding string for indentation.
	 * @param entity The entity instance.
	 * @param primaryKey The entity's primary key object.
	 * @param cache2 The second level cache.
	 * @param xin Is the entity initialized.
	 * @param xpr Is the entity a proxy.
	 */
	protected void printEntityState(String indent, Serializable entity, Object primaryKey, Cache cache2, boolean xin, boolean xpr)
	{
		String name = entity.getClass().getSimpleName();
		int hash = System.identityHashCode(entity);
		boolean xc1 = getEntityManager().contains(entity);
		boolean xc2 = cache2.contains(entity.getClass(), primaryKey);
		getPrintStream().format("%s%s %08X: c1=%.1B c2=%.1B in=%.1B pr=%.1B\n", indent, name, hash, xc1, xc2, xin, xpr);
		getPrintStream().flush();
	}
	
	private JToggleButton validateButton;
	public JToggleButton getValidateButton() { return validateButton; }
	public void setValidateButton(JToggleButton validateButton) { this.validateButton = validateButton; }

	private JToggleButton clean1stLevelCacheButton;
	public JToggleButton getClean1stLevelCacheButton() { return clean1stLevelCacheButton; }
	public void setClean1stLevelCacheButton(JToggleButton clean1stLevelCacheButton) { this.clean1stLevelCacheButton = clean1stLevelCacheButton; }

	private JButton clean2ndLevelCacheButton;
	public JButton getClean2ndLevelCacheButton() { return clean2ndLevelCacheButton; }
	public void setClean2ndLevelCacheButton(JButton clean2ndLevelCacheButton) { this.clean2ndLevelCacheButton = clean2ndLevelCacheButton; }

	private JButton reopenEntityManagerButton;
	public JButton getReopenEntityManagerButton() { return reopenEntityManagerButton; }
	public void setReopenEntityManagerButton(JButton reopenEntityManagerButton) { this.reopenEntityManagerButton = reopenEntityManagerButton; }

	private JButton logSummaryStatisticsButton;
	public JButton getLogSummaryStatisticsButton() { return logSummaryStatisticsButton; }
	public void setLogSummaryStatisticsButton(JButton logSummaryStatisticsButton) { this.logSummaryStatisticsButton = logSummaryStatisticsButton; }

	public void modifyToolBar()
	{
		getToolBar().addSeparator();
		
		// Toggle schema validation
		String validateOffPath = OILPATH+"/actions/flag-red.png";
		String validateOnPath = OILPATH+"/actions/flag-green.png";
		setValidateButton(createImageToggleButton(AbstractEntityExplorer.class, validateOffPath, validateOnPath));
		getValidateButton().addActionListener((event) -> toggleValidateSchema(event));
		getValidateButton().setToolTipText("Toggle schema validation");
		getToolBar().add(getValidateButton());
		
		// Toggle clean first level cache
		String clean1stLevelCacheOffPath = OILPATH+"/actions/general-recycling-green.png";
		String clean1stLevelCacheOnPath = OILPATH+"/actions/general-recycling-green.png";
		setClean1stLevelCacheButton(createImageToggleButton(AbstractEntityExplorer.class, clean1stLevelCacheOffPath, clean1stLevelCacheOnPath));
		getClean1stLevelCacheButton().addActionListener((event) -> toggle1stLevelCleanCache(event));
		getClean1stLevelCacheButton().setToolTipText("Reuse 1st level cache");
		getToolBar().add(getClean1stLevelCacheButton());
		
		// Clean second level cache
		String clean2ndLevelCachePath = OILPATH+"/actions/db_remove.png";
		setClean2ndLevelCacheButton(createImageButton(AbstractEntityExplorer.class, clean2ndLevelCachePath));
		getClean2ndLevelCacheButton().addActionListener((event) -> clear2ndLevelCache(event));
		getClean2ndLevelCacheButton().setToolTipText("Clean 2nd level cache");
		getToolBar().add(getClean2ndLevelCacheButton());
		
		// Reopen EnityManager
		String reopenEntityManagerPath = OILPATH+"/actions/view-refresh-5.png";
		setReopenEntityManagerButton(createImageButton(AbstractEntityExplorer.class, reopenEntityManagerPath));
		getReopenEntityManagerButton().addActionListener((event) -> reopenEntityManager(event));
		getReopenEntityManagerButton().setToolTipText("Reopen session and connection");
		getToolBar().add(getReopenEntityManagerButton());

		// Log Summary Statistics
		String logSummaryStatisticsPath = OILPATH+"/actions/utilities-log-viewer.png";
		setLogSummaryStatisticsButton(createImageButton(AbstractEntityExplorer.class, logSummaryStatisticsPath));
		getLogSummaryStatisticsButton().addActionListener((event) -> logSummaryStatistics(event));
		getLogSummaryStatisticsButton().setToolTipText("Log summary statistics");
		getToolBar().add(getLogSummaryStatisticsButton());
	}
	
	private void clear2ndLevelCache(ActionEvent event)
	{
		// Evict only the "JPA cache", which is purely defined as the entity regions.
		// Query cache is not evicted.
		getEntityManagerFactory().getCache().evictAll();
	}
	
	private void toggleValidateSchema(ActionEvent event)
	{
		JToggleButton toggleButton = (JToggleButton) event.getSource();
		if ( toggleButton.isSelected() )
			generateXmlSchemaValidatorFromDom();
		else
		{
			setMarshaller(createMarshaller(getJaxbContext()));
			setUnmarshaller(createUnmarshaller(getJaxbContext()));
			println("Schema Validation is OFF.");
		}
	}

	private void toggle1stLevelCleanCache(ActionEvent event)
	{
		JToggleButton toggleButton = (JToggleButton) event.getSource();
		if ( toggleButton.isSelected() )
		{
			println("Reuse first level entity cache between transactions.");
		}
		else
		{
			println("Clean first level entity cache at start of topmost transaction.");
		}
	}
	
	private void reopenEntityManager(ActionEvent event)
	{
		// JButton button = (JButton) event.getSource();
		getEntityManager().close();
		println("EnityManager: closed");
		setEntityManager(createEntityManager());
		println("EnityManager: created");
	}

	abstract protected void logSummaryStatistics(ActionEvent event);
}
