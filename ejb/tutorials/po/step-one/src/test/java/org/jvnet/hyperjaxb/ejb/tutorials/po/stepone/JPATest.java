package org.jvnet.hyperjaxb.ejb.tutorials.po.stepone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesBaseFile;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesMoreFile;

import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import generated.ObjectFactory;
import generated.PurchaseOrderType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPATest {

	private static Logger log = LoggerFactory.getLogger(JPATest.class);

	private ObjectFactory objectFactory;

	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	public void setUp() throws Exception {

		objectFactory = new ObjectFactory();

		final Properties persistenceProperties = new Properties();
		try ( InputStream is = getClass().getClassLoader().getResourceAsStream(getPersistencePropertiesBaseFile()) )
		{
			persistenceProperties.load(is);
		}

		final Properties persistencePropertiesMore = new Properties();
		try ( InputStream is = getClass().getClassLoader().getResourceAsStream(getPersistencePropertiesMoreFile()) )
		{
			if ( is != null )
			{
				persistencePropertiesMore.load(is);
				persistenceProperties.putAll(persistencePropertiesMore);
			}
		}

		entityManagerFactory = Persistence.createEntityManagerFactory(
				"generated", persistenceProperties);
	}

	@Test
	public void testSaveAndLoad() {
		final PurchaseOrderType alpha = objectFactory.createPurchaseOrderType();
		alpha.setShipTo(objectFactory.createUSAddress());
		alpha.getShipTo().setCity("Sacramento");

		final EntityManager saveManager = entityManagerFactory
				.createEntityManager();
		saveManager.getTransaction().begin();
		saveManager.persist(alpha);
		saveManager.getTransaction().commit();
		saveManager.close();

		final Long id = alpha.getHjid();

		final EntityManager loadManager = entityManagerFactory
				.createEntityManager();
		final PurchaseOrderType beta = loadManager.find(
				PurchaseOrderType.class, id);
		loadManager.close();
		// Check that we're still shipping to Sacramento
		assertEquals("Sacramento", beta.getShipTo().getCity());
		log.info("JAXB shipTo equals JPA shipTo.");

	}

}
