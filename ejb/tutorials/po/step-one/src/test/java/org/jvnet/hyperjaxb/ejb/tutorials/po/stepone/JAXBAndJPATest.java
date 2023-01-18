package org.jvnet.hyperjaxb.ejb.tutorials.po.stepone;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.io.File;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import generated.ObjectFactory;
import generated.PurchaseOrderType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class JAXBAndJPATest
{
	private static Logger log = LoggerFactory.getLogger(JAXBAndJPATest.class);
	private ObjectFactory objectFactory;
	private EntityManagerFactory entityManagerFactory;
	private JAXBContext context;

	@BeforeEach
	public void setUp()
		throws Exception
	{
		objectFactory = new ObjectFactory();
		Map<String, String> properties = createEntityManagerFactoryProperties(getClass());
		entityManagerFactory = createEntityManagerFactory("generated", properties);
		context = JAXBContext.newInstance("generated");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRoundtrip()
		throws JAXBException
	{
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final Object object = unmarshaller.unmarshal(new File("src/test/samples/po.xml"));
		final PurchaseOrderType alpha = ((JAXBElement<PurchaseOrderType>) object).getValue();
		final EntityManager saveManager = entityManagerFactory.createEntityManager();
		saveManager.getTransaction().begin();
		saveManager.persist(alpha);
		saveManager.getTransaction().commit();
		saveManager.close();
		final Long id = alpha.getHjid();
		final EntityManager loadManager = entityManagerFactory.createEntityManager();
		final PurchaseOrderType beta = loadManager.find(PurchaseOrderType.class, id);
		assertEquals(alpha, beta, "Objects are not equal.");
		log.info("JAXB object equals JPA object.");
		final Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(objectFactory.createPurchaseOrder(beta), System.out);
		loadManager.close();
	}
}
