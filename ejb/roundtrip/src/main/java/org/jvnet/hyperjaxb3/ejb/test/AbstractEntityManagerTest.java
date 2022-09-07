package org.jvnet.hyperjaxb3.ejb.test;

import static org.jvnet.hyperjaxb3.ejb.Constants.PERSISTENCE_PROPERTIES_BASE_FILE;
import static org.jvnet.hyperjaxb3.ejb.Constants.PERSISTENCE_PROPERTIES_MORE_FILE;
import static org.jvnet.hyperjaxb3.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesBaseFile;
import static org.jvnet.hyperjaxb3.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesMoreFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Roundtrip test case.
 * 
 * @author Aleksei Valikov
 */
public abstract class AbstractEntityManagerTest {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  protected Class lastTestClass;

  protected EntityManagerFactory entityManagerFactory;

  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
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

  public String getPersistenceUnitName() {
    final Package _package = getClass().getPackage();
    final String name = _package.getName();
    if (name == null) {
      return "root";
    }
    else {
      return name;
    }
  }

  public Map getEntityManagerProperties() {
    return null;
  }

  public EntityManagerFactory createEntityManagerFactory() {

    try {
      final Enumeration<URL> resources = getClass().getClassLoader().getResources(
          "META-INF/persistence.xml");
      while (resources.hasMoreElements()) {
        final URL resource = resources.nextElement();
        logger.info("EMF: Detected [" + resource + "].");
      }

    }
    catch (IOException ignored) {

    }

    final Map properties = getEntityManagerFactoryProperties();

    if (properties == null) {
      return Persistence.createEntityManagerFactory(getPersistenceUnitName());
    }
    else {
      return Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
    }
  }

  private Map entityManagerFactoryProperties;
  public Map getEntityManagerFactoryProperties()
  {
	if ( entityManagerFactoryProperties == null )
		setEntityManagerFactoryProperties(createEntityManagerFactoryProperties());
    return entityManagerFactoryProperties;
  }
  public void setEntityManagerFactoryProperties(Map entityManagerFactoryProperties)
  {
    this.entityManagerFactoryProperties = entityManagerFactoryProperties;
  }

	public Map createEntityManagerFactoryProperties()
	{
		try
		{
			final Enumeration<URL> resourcesBase =
				getClass().getClassLoader().getResources(getPersistencePropertiesBaseFile());
			if (!resourcesBase.hasMoreElements())
			{
				logger.debug("Entity manager factory properties are not set.");
				return null;
			}
			else
			{
				logger.debug("Loading entity manager factory properties.");
				Properties properties = new Properties();
				properties = loadResources(properties, resourcesBase);
				if ( properties != null )
				{
					final Enumeration<URL> resourcesMore =
						getClass().getClassLoader().getResources(getPersistencePropertiesMoreFile());
					if (resourcesMore.hasMoreElements())
					{
						logger.debug("Loading more entity manager factory properties.");
						properties = loadResources(properties, resourcesMore);
					}
				}
				return properties;
			}
		}
		catch (IOException ex)
		{
			return null;
		}
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
				try ( InputStream is = resource.openStream() )
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

  public EntityManager createEntityManager() {
    final Map properties = getEntityManagerProperties();
    final EntityManagerFactory emf = getEntityManagerFactory();
    if (properties == null) {
      return emf.createEntityManager();
    }
    else {
      return emf.createEntityManager(properties);
    }
  }
}