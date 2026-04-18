package org.example.jpa21;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.io.IOException;
import java.util.Map;

import org.example.jpa21.model.Employee;
import org.jvnet.basicjaxb.config.LocatorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * JPA context for {@link org.example.jpa21.model.Employee}
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
				pun = Employee.class.getPackageName();
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

	/**
	 * A factory to generate an id of the form "ABCDE".
	 */
	protected class IdFactory
	{
		private char[] counter;

		protected IdFactory()
		{
			this("AAAA@");
		}

		protected IdFactory(String seed)
		{
			counter = seed.toUpperCase().toCharArray();
		}

		protected String nextId()
		{
			return incrementCounter(counter.length-1);
		}

		private String incrementCounter(int pos)
		{
			if ( counter[pos] != 'Z')
				++counter[pos];
			else if ( pos > 0 )
			{
				counter[pos] = 'A';
				incrementCounter(--pos);
			}
			return new String(counter);
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
