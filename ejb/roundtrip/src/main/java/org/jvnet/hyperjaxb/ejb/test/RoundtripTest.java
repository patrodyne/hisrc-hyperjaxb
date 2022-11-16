package org.jvnet.hyperjaxb.ejb.test;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jvnet.basicjaxb.lang.ContextUtils;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.locator.DefaultRootObjectLocator;
import org.jvnet.hyperjaxb.ejb.util.EntityUtils;
import org.jvnet.hyperjaxb.lang.builder.ExtendedJAXBEqualsStrategy;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputDomResolver;
import org.xml.sax.SAXException;

import jakarta.persistence.EntityManager;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;

public abstract class RoundtripTest extends AbstractEntityManagerSamplesTest
{
	private Boolean validateXml = true;
	public Boolean isValidateXml() { return validateXml; }
	public void setValidateXml(Boolean validateXml) { this.validateXml = validateXml; }

	@Override
	protected void checkSample(File sample)
		throws Exception
	{
		final JAXBContext context = createContext();
		logger.debug("Unmarshalling.");
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		
		if ( isValidateXml() )
			generateXmlSchemaValidatorFromDom(context, unmarshaller);
		
		final JAXBElement unmarshalledElement;
		final Object unmarshalledObject;
		final Object unmarshalledDraft = unmarshaller.unmarshal(sample);
		
		if (unmarshalledDraft instanceof JAXBElement)
		{
			unmarshalledElement = (JAXBElement) unmarshalledDraft;
			unmarshalledObject = unmarshalledElement.getValue();
		}
		else
		{
			unmarshalledElement = null;
			unmarshalledObject = unmarshalledDraft;
		}
		
		final JAXBElement etalonElement;
		final Object etalonObject;
		final Object etalonDraft = unmarshaller.unmarshal(sample);
		
		if (etalonDraft instanceof JAXBElement)
		{
			etalonElement = (JAXBElement) etalonDraft;
			etalonObject = etalonElement.getValue();
		}
		else
		{
			etalonElement = null;
			etalonObject = etalonDraft;
		}
		
		logger.debug("Opening session.");
		// Open the session, save object into the database
		logger.debug("Saving the object.");
		final EntityManager saveManager = createEntityManager();
		saveManager.getTransaction().begin();
		// final Object merged = saveSession.merge(object);
		// saveSession.replicate(object, ReplicationMode.OVERWRITE);
		// saveSession.get
		// final Serializable id =
		final Object mergedObject = saveManager.merge(unmarshalledObject);
		saveManager.getTransaction().commit();
		final Object id = EntityUtils.getId(saveManager, mergedObject);
		// saveSession.getIdentifier(object);
		saveManager.clear();
		// Close the session
		saveManager.close();
		logger.debug("Opening session.");
		// Open the session, load the object
		final EntityManager loadManager = createEntityManager();
		logger.debug("Loading the object.");
		final Object loadedObject = loadManager.find(mergedObject.getClass(), id);
		logger.debug("Closing the session.");
		
		if (unmarshalledElement != null)
		{
			final JAXBElement<Object> mergedElement = new JAXBElement(unmarshalledElement.getName(),
				unmarshalledElement.getDeclaredType(), mergedObject);
			final JAXBElement loadedElement = new JAXBElement(unmarshalledElement.getName(),
				unmarshalledElement.getDeclaredType(), loadedObject);
			logger.debug("Initial object:\n" + ContextUtils.toString(context, etalonElement));
			logger.debug("Source object:\n" + ContextUtils.toString(context, mergedElement));
			logger.debug("Result object:\n" + ContextUtils.toString(context, loadedElement));
		}
		else
		{
			logger.debug("Initial object:\n" + ContextUtils.toString(context, etalonObject));
			logger.debug("Source object:\n" + ContextUtils.toString(context, mergedObject));
			logger.debug("Result object:\n" + ContextUtils.toString(context, loadedObject));
		}
		
		logger.debug("Checking the sample object identity: Merged vs Loaded.");
		checkObjects(mergedObject, loadedObject);
		logger.debug("Checking the sample object identity: Etalon vs Loaded.");
		checkObjects(etalonObject, loadedObject);
		loadManager.close();
	}

	protected void checkObjects(final Object leftObject, final Object rightObject)
	{
		final EqualsStrategy strategy = new ExtendedJAXBEqualsStrategy();
		assertTrue(strategy.equals(new DefaultRootObjectLocator(leftObject), new DefaultRootObjectLocator(rightObject),
			leftObject, rightObject), "Objects NOT equal. Use DEBUG for location details.");
	}
	
	protected void generateXmlSchemaValidatorFromDom(JAXBContext context, Unmarshaller unmarshaller)
		throws IOException, SAXException
    {
        // Generate a Schema Validator from given the JAXB context.
        SchemaOutputDomResolver sodr = new SchemaOutputDomResolver();
        context.generateSchema(sodr);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
        Schema schemaValidator = schemaFactory.newSchema(sodr.getDomSource());
        // Configure Marshaller / unmarshaller to use validator.
        unmarshaller.setSchema(schemaValidator);
    }
}
