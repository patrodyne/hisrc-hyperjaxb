package org.jvnet.hyperjaxb.ejb.test;

import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Roundtrip test case.
 * 
 * @author Aleksei Valikov
 */
public abstract class AbstractEntityManagerTest
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	public Logger getLogger()
	{
		return logger;
	}

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

	/**
	 * Get the persistence unit name for testing the EMF.
	 * 
	 * Note: Sub-classes must override this method for custom PUNs.
	 * 
	 * @return A persistence unit name derived from this class/sub-class.
	 */
	public String getPersistenceUnitName()
	{
		final Package _package = getClass().getPackage();
		final String name = _package.getName();
		if (name == null)
		{
			getLogger().debug("PUN, default to root.");
			return "root";
		}
		else
		{
			getLogger().debug("PUN: derive from current class: {}", name);
			return name;
		}
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
				getLogger().info("EMF: Detected [" + resource + "].");
			}
		}
		catch (IOException ioex)
		{
			getLogger().warn("EMF: Not Detected [" + resourceName + "].");
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
			setEntityManagerFactoryProperties(createEntityManagerFactoryProperties(getClass()));
		return entityManagerFactoryProperties;
	}

	public void setEntityManagerFactoryProperties(Map<String, String> entityManagerFactoryProperties)
	{
		this.entityManagerFactoryProperties = entityManagerFactoryProperties;
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
