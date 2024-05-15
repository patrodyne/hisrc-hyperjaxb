package org.example.root_header;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jvnet.basicjaxb.config.LocatorProperties;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputDomResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * JAXB and JPA context for {@link org.example.root_header.ObjectFactory}
 */
abstract public class Context
{
	public static final String HIBERNATE_PUN = "hibernate.ejb.persistenceUnitName";

	private static Logger logger = LoggerFactory.getLogger(Context.class);
	public static Logger getLogger() { return logger; }
	
	// Configuration
	static
	{
		try
		{
			// Load JVM system properties from the classpath.
			LocatorProperties systemProperties = new LocatorProperties();
			systemProperties.load("classpath:/jvmsystem.properties");
			System.getProperties().putAll(systemProperties);
		}
		catch (IOException ex)
		{
			getLogger().error("", ex);
		}
	}
	
	// JAXB Context
	
	private JAXBContext jaxbContext;
	public JAXBContext getJaxbContext() throws JAXBException
	{
		if ( jaxbContext == null )
			setJaxbContext(JAXBContext.newInstance(ObjectFactory.class));
		return jaxbContext;
	}
	public void setJaxbContext(JAXBContext jaxbContext)
	{
		this.jaxbContext = jaxbContext;
	}

	private Unmarshaller unmarshaller = null;
	protected Unmarshaller getUnmarshaller() throws JAXBException
	{
		if ( unmarshaller == null )
			setUnmarshaller(getJaxbContext().createUnmarshaller());
		return unmarshaller;
	}
	protected void setUnmarshaller(Unmarshaller unmarshaller)
	{
		this.unmarshaller = unmarshaller;
	}

	private Marshaller marshaller = null;
	public Marshaller getMarshaller() throws JAXBException
	{
		if ( marshaller == null )
		{
			setMarshaller(getJaxbContext().createMarshaller());
			getMarshaller().setProperty(JAXB_FORMATTED_OUTPUT, true);
		}
		return marshaller;
	}
	public void setMarshaller(Marshaller marshaller)
	{
		this.marshaller = marshaller;
	}

	// JPA Context
	
	private Map<String, String> entityManagerFactoryProperties = null;
	public Map<String, String> getEntityManagerFactoryProperties() throws IOException
	{
		if ( entityManagerFactoryProperties == null )
		{
			Map<String, String> map = createEntityManagerFactoryProperties(getClass());
			if ( map != null && map.containsKey("jakarta.persistence.jdbc.driver") )
				setEntityManagerFactoryProperties(map);
			else
				throw new IOException("Incomplete EntityManagerFactory properties");
		}
		return entityManagerFactoryProperties;
	}
	public void setEntityManagerFactoryProperties(Map<String, String> entityManagerFactoryProperties)
	{
		this.entityManagerFactoryProperties = entityManagerFactoryProperties;
	}

	private String persistenceUnitName = null;
	public String getPersistenceUnitName() throws IOException
	{
		if ( persistenceUnitName == null )
		{
	        String pun = getEntityManagerFactoryProperties().get(HIBERNATE_PUN);
	        if ( pun == null )
	            pun = ObjectFactory.class.getPackageName();
        	setPersistenceUnitName(pun);
		}
		return persistenceUnitName;
	}
	public void setPersistenceUnitName(String persistenceUnitName)
	{
		this.persistenceUnitName = persistenceUnitName;
	}

	private EntityManagerFactory entityManagerFactory = null;
	public EntityManagerFactory getEntityManagerFactory() throws IOException
	{
		if ( entityManagerFactory == null )
		{
			setEntityManagerFactory(createEntityManagerFactory
			(
				getPersistenceUnitName(),
				getEntityManagerFactoryProperties())
			);
		}
		return entityManagerFactory;
	}
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
	{
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * <p>Create an EntityManager to perform JPA transactions.</p>
	 * 
	 * <p>Note: Always perform EntityManager actions within a transaction!</p>
	 * 
	 * @return An EntityManager instance.
	 * 
	 * @throws IOException When persistence properties cannot be loaded.
	 */
	protected EntityManager createEntityManager() throws IOException
    {
        return getEntityManagerFactory().createEntityManager();
    }
	
	@SuppressWarnings("unchecked")
	protected <T> T unmarshal(String xmlFileName, Class<T> clazz) throws JAXBException
	{
		File xmlFile = new File(xmlFileName);
		return (T) getUnmarshaller().unmarshal(xmlFile);
	}
	
    protected String marshalToString(Object instance) throws JAXBException, IOException
    {
        String xml = null;
        if ( instance != null)
        {
            try ( StringWriter writer = new StringWriter() )
            {
                getMarshaller().marshal(instance, writer);
                xml = writer.toString();
            }
        }
        return xml;
    }

	protected void generateXmlSchemaValidatorFromDom() throws JAXBException, IOException, SAXException
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
		}
		else
			getLogger().error("Please create marshaller and unmarshaller!");
    }
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
