package org.jvnet.hyperjaxb.ejb.tests.componentjpa.tests;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestHyperJaxb
{
	@Test
	public void testMapping()
		throws Exception
	{
		// Hibernate configuration
		Map<String, String> hibernateProperties = new HashMap<String, String>();
		hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");
		hibernateProperties.put("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");
		hibernateProperties.put("hibernate.connection.url", "jdbc:derby:target/test-database/database;create=true");
		hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
		// initialise Hibernate
		EntityManagerFactory emf = createEntityManagerFactory(hibernateProperties);// ,
																					// hibernateProperties);
		EntityManager em = emf.createEntityManager();
		// deserialize test XML document
		JobStream jaxbElement = (JobStream) JAXBContextUtils.unmarshal(
			"org.jvnet.hyperjaxb.ejb.tests.componentjpa.tests", readFileAsString("src/test/resources/tests.xml"));
		// JobStream mails = (JobStream) JAXBElementUtils.getValue(jaxbElement);
		// persist object
		em.getTransaction().begin();
		em.persist(jaxbElement);
		em.getTransaction().commit();
		// retrieve persisted object
		JobStream persistedMails = em.find(JobStream.class, 1L);
		System.out.println("persistedObjects = " + persistedMails);
		em.close();
		emf.close();
	}

	private String readFileAsString(String filePath)
		throws IOException
	{
		File f = new File(filePath);
		FileInputStream fin = new FileInputStream(f);
		byte[] buffer = new byte[(int) f.length()];
		new DataInputStream(fin).readFully(buffer);
		fin.close();
		return new String(buffer, "UTF-8");
	}

	protected EntityManagerFactory createEntityManagerFactory(Map<String, String> properties)
	{
		try
		{
			final Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/persistence.xml");
			while (resources.hasMoreElements())
			{
				final URL resource = resources.nextElement();
				System.out.println("Detected [" + resource.getPath() + "].");
			}
		}
		catch (IOException ignored)
		{
		}
		
		// final Map properties = getEntityManagerFactoryProperties();
		if (properties == null)
			return Persistence.createEntityManagerFactory(getPersistenceUnitName());
		else
			return Persistence.createEntityManagerFactory(getPersistenceUnitName(), properties);
	}

	private String getPersistenceUnitName()
	{
		return "org.jvnet.hyperjaxb.ejb.tests.componentjpa.tests";
	}
}
