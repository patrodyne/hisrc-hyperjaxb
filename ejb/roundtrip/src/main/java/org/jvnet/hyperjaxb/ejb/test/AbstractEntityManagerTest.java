package org.jvnet.hyperjaxb.ejb.test;

import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesBaseFile;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesMoreFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.BeforeEach;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Roundtrip test case.
 * 
 * @author Aleksei Valikov
 */
public abstract class AbstractEntityManagerTest
{
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected Class<? extends AbstractEntityManagerTest> lastTestClass;
	
	private EntityManagerFactory entityManagerFactory;
	public EntityManagerFactory getEntityManagerFactory()
	{
		return entityManagerFactory;
	}
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
	{
		this.entityManagerFactory = entityManagerFactory;
	}

	public AbstractEntityManagerTest()
	{
		super();
	}

	public AbstractEntityManagerTest(EntityManagerFactory emf)
	{
		super();
		setEntityManagerFactory(emf);
	}

	@BeforeEach
	public void setUp()
		throws Exception
	{
		final EntityManagerFactory emf = getEntityManagerFactory();
		if (emf == null || !emf.isOpen() || lastTestClass != getClass())
		{
			setEntityManagerFactory(createEntityManagerFactory());
			lastTestClass = getClass();
		}
	}

	public String getPersistenceUnitName()
	{
		final Package _package = getClass().getPackage();
		final String name = _package.getName();
		if (name == null)
			return "root";
		else
			return name;
	}

	public Map<String, String> getEntityManagerProperties()
	{
		return null;
	}

	public EntityManagerFactory createEntityManagerFactory()
	{
		String resourceName = "META-INF/persistence.xml";
		try
		{
			final Enumeration<URL> resources = getClass().getClassLoader().getResources(resourceName);
			while (resources.hasMoreElements())
			{
				final URL resource = resources.nextElement();
				logger.info("EMF: Detected [" + resource + "].");
			}
		}
		catch (IOException ioex)
		{
			logger.warn("EMF: Not Detected [" + resourceName + "].");
		}
		
		final Map<String, String> properties = getEntityManagerFactoryProperties();
		if (properties == null)
			return Persistence.createEntityManagerFactory(getPersistenceUnitName());
		else
			return Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
	}

	private Map<String, String> entityManagerFactoryProperties;
	public Map<String, String> getEntityManagerFactoryProperties()
	{
		if (entityManagerFactoryProperties == null)
			setEntityManagerFactoryProperties(createEntityManagerFactoryProperties());
		return entityManagerFactoryProperties;
	}

	public void setEntityManagerFactoryProperties(Map<String, String> entityManagerFactoryProperties)
	{
		this.entityManagerFactoryProperties = entityManagerFactoryProperties;
	}

	public Map<String, String> createEntityManagerFactoryProperties()
	{
		try
		{
			final Enumeration<URL> resourcesBase =
				getClass().getClassLoader().getResources(getPersistencePropertiesBaseFile());
			if (resourcesBase.hasMoreElements())
			{
				logger.debug("Loading entity manager factory properties.");
				Properties properties = new Properties();
				properties = loadResources(properties, resourcesBase);
				if (properties != null)
				{
					final Enumeration<URL> resourcesMore =
						getClass().getClassLoader().getResources(getPersistencePropertiesMoreFile());
					if (resourcesMore.hasMoreElements())
					{
						logger.debug("Loading more entity manager factory properties.");
						properties = loadResources(properties, resourcesMore);
					}
				}
				return toMap(properties);
			}
			else
			{
				logger.debug("Entity manager factory properties are not set.");
				return null;
			}
		}
		catch (IOException ex)
		{
			return null;
		}
	}
	
	private Map<String, String> toMap(Properties prop)
	{
		return prop.entrySet().stream().collect(
			Collectors.toMap(
				e -> String.valueOf(e.getKey()),
				e -> String.valueOf(e.getValue()),
				(prev, next) -> next, HashMap::new
			));
	}
	
	private Properties loadResources(Properties properties, final Enumeration<URL> resourcesBase)
	{
		while (resourcesBase.hasMoreElements())
		{
			final URL resource = resourcesBase.nextElement();
			logger.info("EMF: Loading [" + resource + "].");
			if (resource == null)
			{
				properties = null;
				break;
			}
			else
			{
				try (InputStream is = resource.openStream())
				{
					properties.load(is);
				}
				catch (IOException ex)
				{
					properties = null;
					break;
				}
			}
		}
		return properties;
	}

	public EntityManager createEntityManager()
	{
		final Map<String, String> properties = getEntityManagerProperties();
		final EntityManagerFactory emf = getEntityManagerFactory();
		if (properties == null)
			return emf.createEntityManager();
		else
			return emf.createEntityManager(properties);
	}
}