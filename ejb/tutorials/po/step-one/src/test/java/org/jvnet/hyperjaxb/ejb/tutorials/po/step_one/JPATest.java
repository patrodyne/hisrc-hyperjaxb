package org.jvnet.hyperjaxb.ejb.tutorials.po.step_one;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import generated.ObjectFactory;
import generated.PurchaseOrderType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class JPATest
{
	private static Logger log = LoggerFactory.getLogger(JPATest.class);
	private ObjectFactory objectFactory;
	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	public void setUp()
		throws Exception
	{
		objectFactory = new ObjectFactory();
		Map<String, String> properties = createEntityManagerFactoryProperties(getClass());
		entityManagerFactory = createEntityManagerFactory("generated", properties);
	}

	@Test
	public void testSaveAndLoad()
	{
		final PurchaseOrderType alpha = objectFactory.createPurchaseOrderType();
		alpha.setShipTo(objectFactory.createUSAddress());
		alpha.getShipTo().setCity("Sacramento");
		final EntityManager saveManager = entityManagerFactory.createEntityManager();
		saveManager.getTransaction().begin();
		saveManager.persist(alpha);
		saveManager.getTransaction().commit();
		saveManager.close();
		final Long id = alpha.getHjid();
		final EntityManager loadManager = entityManagerFactory.createEntityManager();
		final PurchaseOrderType beta = loadManager.find(PurchaseOrderType.class, id);
		loadManager.close();
		// Check that we're still shipping to Sacramento
		assertEquals("Sacramento", beta.getShipTo().getCity());
		log.info("JAXB shipTo equals JPA shipTo.");
	}
}
