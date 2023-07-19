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
	private static final Logger log = LoggerFactory.getLogger(JPATest.class);
	private static final String PUN = "generated";
	
	private ObjectFactory objectFactory;
	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	public void setUp()
		throws Exception
	{
		objectFactory = new ObjectFactory();
		Map<String, String> properties = createEntityManagerFactoryProperties(getClass());
		entityManagerFactory = createEntityManagerFactory(PUN, properties);
	}

	@Test
	public void testSaveAndLoad()
	{
		final PurchaseOrderType alpha = objectFactory.createPurchaseOrderType();
		alpha.setShipTo(objectFactory.createUSAddress());
		alpha.getShipTo().setCity("Sacramento");
		
		try ( final EntityManager saveManager = entityManagerFactory.createEntityManager() )
		{
			saveManager.getTransaction().begin();
			saveManager.persist(alpha);
			saveManager.getTransaction().commit();
		}
		
		PurchaseOrderType beta = null;
		final Long id = alpha.getHjid();
		try ( final EntityManager loadManager = entityManagerFactory.createEntityManager() )
		{
			beta = loadManager.find(PurchaseOrderType.class, id);
		}
		
		// Check that we're still shipping to Sacramento
		assertEquals("Sacramento", beta.getShipTo().getCity());
		log.info("JAXB shipTo equals JPA shipTo.");
	}
}
