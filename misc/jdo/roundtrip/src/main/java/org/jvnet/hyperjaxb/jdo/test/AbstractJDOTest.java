package org.jvnet.hyperjaxb.jdo.test;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractJDOTest {
	
  protected Logger logger = LoggerFactory.getLogger(getClass());

  protected Class lastTestClass;

  protected PersistenceManagerFactory entityManagerFactory;

  public PersistenceManagerFactory getPersistenceManagerFactory() {
    return entityManagerFactory;
  }

  public void setPersistenceManagerFactory(PersistenceManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    final PersistenceManagerFactory emf = getPersistenceManagerFactory();
    if (emf == null || emf.isClosed() || lastTestClass != getClass()) {
      setPersistenceManagerFactory(createPersistenceManagerFactory());
      lastTestClass = getClass();
    }
  }

  protected PersistenceManagerFactory createPersistenceManagerFactory() {

    final Map properties = getPersistenceManagerFactoryProperties();

    return JDOHelper.getPersistenceManagerFactory(properties);
  }

  public PersistenceManager createPersistenceManager() {
    final PersistenceManagerFactory emf = getPersistenceManagerFactory();
    return emf.getPersistenceManager();
  }

  public Map getPersistenceManagerFactoryProperties() {

    try {
      final Enumeration<URL> resources = getClass().getClassLoader().getResources(
          getPersistenceManagerFactoryPropertiesResourceName());

      if (!resources.hasMoreElements()) {
        logger.debug("Entity manager factory properties are not set.");
        return null;

      }
      else {
        logger.debug("Loading entity manager factory properties.");
        final Properties properties = new Properties();
        while (resources.hasMoreElements()) {
          final URL resource = resources.nextElement();
          logger.debug("Loading entity manager factory properties from [" + resource + "].");

          if (resource == null) {
            return null;
          }
          else {
            InputStream is = null;
            try {
              is = resource.openStream();
              properties.load(is);
              return properties;
            }
            catch (IOException ex) {
              return null;
            }
            finally {
              if (is != null) {
                try {
                  is.close();
                }
                catch (IOException ex) {
                  // Ignore
                }
              }
            }
          }
        }
        return properties;
      }
    }
    catch (IOException ex) {
      return null;
    }
  }

  public String getPersistenceManagerFactoryPropertiesResourceName() {
    return "jpox.properties";
  }

}
