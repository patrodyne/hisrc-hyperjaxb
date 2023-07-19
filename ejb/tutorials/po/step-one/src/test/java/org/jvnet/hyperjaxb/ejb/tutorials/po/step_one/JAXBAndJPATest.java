package org.jvnet.hyperjaxb.ejb.tutorials.po.step_one;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
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
	private static final String PUN = "generated";
	
	private static final String SAMPLE_XML = "src/test/samples/po.xml";
	
	private ObjectFactory objectFactory;
	private EntityManagerFactory entityManagerFactory;
	private JAXBContext context;

	@BeforeEach
	public void setUp()
		throws Exception
	{
		objectFactory = new ObjectFactory();
		Map<String, String> properties = createEntityManagerFactoryProperties(getClass());
		entityManagerFactory = createEntityManagerFactory(PUN, properties);
		context = JAXBContext.newInstance(ObjectFactory.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRoundtrip()
		throws JAXBException
	{
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final Object object = unmarshaller.unmarshal(new File(SAMPLE_XML));
		
		final PurchaseOrderType alpha = ((JAXBElement<PurchaseOrderType>) object).getValue();
		try ( final EntityManager saveManager = entityManagerFactory.createEntityManager() )
		{
			saveManager.getTransaction().begin();
			saveManager.persist(alpha);
			saveManager.getTransaction().commit();
		}
		
		final Long id = alpha.getHjid();
		try ( final EntityManager loadManager = entityManagerFactory.createEntityManager() )
		{
			final PurchaseOrderType beta = loadManager.find(PurchaseOrderType.class, id);
			assertEquals(alpha, beta, "Objects are not equal.");
			log.info("JAXB object equals JPA object.");
			
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			Writer sw = new StringWriter();
			marshaller.marshal(objectFactory.createPurchaseOrder(beta), sw);
			log.debug("PurchaseOrder:\n" + sw);
		}
	}
}
