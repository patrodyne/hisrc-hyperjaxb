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
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public abstract class RoundtripTest extends AbstractEntityManagerSamplesTest
{
	private Boolean validateXml = true;
	public Boolean isValidateXml() { return validateXml; }
	public void setValidateXml(Boolean validateXml) { this.validateXml = validateXml; }

	@Override
	protected void checkSample(File sampleFile)
		throws Exception
	{
		final JAXBContext context = createContext();
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		
		if ( isValidateXml() )
			generateXmlSchemaValidatorFromDom(context, unmarshaller);
		
		logger.debug("Unmarshalling sample.");
		Sample initialSample = new Sample(unmarshaller, sampleFile);
		
		logger.debug("Unmarshalling etalon.");
		Sample etalonSample = new Sample(unmarshaller, sampleFile);
		
		logger.debug("Persisting the unmarshalled sample.");
		final EntityManager saveManager = createEntityManager();
		saveManager.getTransaction().begin();
		final Object mergedEntity = saveManager.merge(initialSample.getValue());
		saveManager.getTransaction().commit();
		final Object mergedId = EntityUtils.getId(saveManager, mergedEntity);
		// Close the save session
		saveManager.clear();
		saveManager.close();
		
		logger.debug("Loading the persisted sample.");
		final EntityManager loadManager = createEntityManager();
		final Object loadedEntity = loadManager.find(mergedEntity.getClass(), mergedId);
		
		if (etalonSample.getElement() != null)
		{
			final JAXBElement<Object> etalonElement = etalonSample.getElement();
			final JAXBElement<Object> mergedElement = wrap(etalonElement, mergedEntity);
			final JAXBElement<Object> loadedElement = wrap(etalonElement, loadedEntity);
			logger.debug("Etalon element:\n" + ContextUtils.toString(context, etalonElement));
			logger.debug("Merged element:\n" + ContextUtils.toString(context, mergedElement));
			logger.debug("Loaded element:\n" + ContextUtils.toString(context, loadedElement));
		}
		else
		{
			logger.debug("Etalon object:\n" + ContextUtils.toString(context, etalonSample.getValue()));
			logger.debug("Merged object:\n" + ContextUtils.toString(context, mergedEntity));
			logger.debug("Loaded object:\n" + ContextUtils.toString(context, loadedEntity));
		}
		
		logger.debug("Checking the sample object identity: Merged vs Loaded.");
		checkObjects(mergedEntity, loadedEntity);
		
		logger.debug("Checking the sample object identity: Etalon vs Loaded.");
		checkObjects(etalonSample.getValue(), loadedEntity);
		
		// Close the load session
		loadManager.clear();
		loadManager.close();
	}

	private JAXBElement<Object> wrap(JAXBElement<Object> element, Object obj)
	{
		return new JAXBElement<Object>(element.getName(), element.getDeclaredType(), obj);
	}
	
	protected void checkObjects(final Object leftObject, final Object rightObject)
	{
		final EqualsStrategy strategy = new ExtendedJAXBEqualsStrategy();
		assertTrue(strategy.equals(new DefaultRootObjectLocator(leftObject), new DefaultRootObjectLocator(rightObject),
			leftObject, rightObject, true, true), "Objects NOT equal. Use DEBUG for location details.");
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
	
	/**
	 * This local class represents a sample file's unmarshalled value and optional JAXBElement.
	 */
	private class Sample
	{
		private JAXBElement<Object> element;
		public JAXBElement<Object> getElement() { return element; }
		public void setElement(JAXBElement<Object> element) { this.element = element; }

		private Object value;
		public Object getValue() { return value; }
		public void setValue(Object value) { this.value = value; }
		
		/**
		 * Construct with a JAXB unmarshaller and sample file.
		 * @param unmarshaller A JAXB unmarshaller.
		 * @param sampleFile A sample file.
		 * @throws JAXBException When the sample file cannot be unmarshalled.
		 */
		@SuppressWarnings("unchecked")
		private Sample(Unmarshaller unmarshaller, File sampleFile) throws JAXBException
		{
			Object unmarshalledDraft = unmarshaller.unmarshal(sampleFile);
			if (unmarshalledDraft instanceof JAXBElement)
			{
				setElement((JAXBElement<Object>) unmarshalledDraft);
				setValue(getElement().getValue());
			}
			else
			{
				setElement(null);
				setValue(unmarshalledDraft);
			}
		}
	}
}
