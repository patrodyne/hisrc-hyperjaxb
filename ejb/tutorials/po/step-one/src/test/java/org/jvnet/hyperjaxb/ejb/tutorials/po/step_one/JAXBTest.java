package org.jvnet.hyperjaxb.ejb.tutorials.po.step_one;

import generated.ObjectFactory;
import generated.PurchaseOrderType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jvnet.hyperjaxb.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.xml.sax.SAXException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JAXBTest {

	private static Logger log = LoggerFactory.getLogger(JAXBTest.class);

	private JAXBContext context;

	private ObjectFactory objectFactory;

	@BeforeEach
	protected void setUp() throws Exception {
		context = JAXBContext.newInstance("generated");
		objectFactory = new ObjectFactory();
	}

	@Test
	public void testUnmarshall() throws JAXBException {
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final Object object = unmarshaller.unmarshal(new File(
				"src/test/samples/po.xml"));
		@SuppressWarnings("unchecked")
		final PurchaseOrderType purchaseOrder = ((JAXBElement<PurchaseOrderType>) object)
				.getValue();
		assertEquals("Mill Valley", purchaseOrder.getShipTo().getCity(), "Wrong city");
	}

	@Test
	public void testMarshal() throws JAXBException, XPathException {
		final PurchaseOrderType purchaseOrder = objectFactory
				.createPurchaseOrderType();
		purchaseOrder.setShipTo(objectFactory.createUSAddress());
		purchaseOrder.getShipTo().setCity("New Orleans");
		final JAXBElement<PurchaseOrderType> purchaseOrderElement = objectFactory
				.createPurchaseOrder(purchaseOrder);

		final Marshaller marshaller = context.createMarshaller();

		final DOMResult result = new DOMResult();
		marshaller.marshal(purchaseOrderElement, result);

		final XPathFactory xPathFactory = XPathFactory.newInstance();

		assertEquals("New Orleans", xPathFactory.newXPath()
				.evaluate("/purchaseOrder/shipTo/city", result.getNode()), "Wrong city");
	}

	@Test
	public void testValidate() throws SAXException, JAXBException {

		final PurchaseOrderType purchaseOrder = objectFactory
				.createPurchaseOrderType();
		purchaseOrder.setShipTo(objectFactory.createUSAddress());
		purchaseOrder.setBillTo(objectFactory.createUSAddress());
		final JAXBElement<PurchaseOrderType> purchaseOrderElement = objectFactory
				.createPurchaseOrder(purchaseOrder);

		final SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = schemaFactory.newSchema(new StreamSource(
				getClass().getClassLoader().getResourceAsStream("po.xsd")));

		final Marshaller marshaller = context.createMarshaller();

		marshaller.setSchema(schema);

		final List<ValidationEvent> events = new LinkedList<ValidationEvent>();

		marshaller.setEventHandler(new ValidationEventHandler() {
			@Override
			public boolean handleEvent(ValidationEvent event) {
				events.add(event);
				return true;
			}
		});
		marshaller.marshal(purchaseOrderElement, new DOMResult());

		assertFalse(events.isEmpty(), "List of validation events must not be empty.");
		log.info("Validation Events (expected):");
		for ( int index=0; index < events.size(); ++index )
		{
			ValidationEvent event = events.get(index);
			log.info("  Event #" + index);
			log.info("    Severity..: " + event.getSeverity());
			log.info("    Message...: " + event.getMessage());
			log.info("    Locator...: " + event.getLocator());
			log.info("    Exception.: " + event.getLinkedException());
		}
	}
}
