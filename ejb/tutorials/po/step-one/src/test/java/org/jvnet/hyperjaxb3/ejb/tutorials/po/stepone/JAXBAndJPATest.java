package org.jvnet.hyperjaxb3.ejb.tutorials.po.stepone;

import static org.jvnet.hyperjaxb3.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesBaseFile;
import static org.jvnet.hyperjaxb3.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesMoreFile;

import generated.ObjectFactory;
import generated.PurchaseOrderType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.dom.DOMResult;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAXBAndJPATest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(JAXBAndJPATest.class);

	private ObjectFactory objectFactory;

	private EntityManagerFactory entityManagerFactory;

	private JAXBContext context;

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

		context = JAXBContext.newInstance("generated");
	}

	@SuppressWarnings("unchecked")
	public void testRoundtrip() throws JAXBException {

		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final Object object = unmarshaller.unmarshal(new File(
				"src/test/samples/po.xml"));
		final PurchaseOrderType alpha = ((JAXBElement<PurchaseOrderType>) object)
				.getValue();

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
		assertEquals("Objects are not equal.", alpha, beta);
		log.info("JAXB object equals JPA object.");
		
		final Marshaller marshaller = context.createMarshaller();
		marshaller.marshal(objectFactory.createPurchaseOrder(beta), System.out);
		loadManager.close();
	}
}
