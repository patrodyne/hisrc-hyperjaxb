package org.jvnet.hyperjaxb.ejb.test;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jvnet.basicjaxb.lang.ContextUtils.createJAXBElement;
import static org.jvnet.basicjaxb.lang.ContextUtils.createSchemaOutputDomResolver;
import static org.jvnet.basicjaxb.lang.ContextUtils.enableXmlSchemaValidator;
import static org.jvnet.hyperjaxb.ejb.util.EntityUtils.getId;
import static org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils.isBindingElement;
import static org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils.isElement;
import static org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils.marshal;
import static org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils.marshalElement;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jvnet.basicjaxb.lang.CopyTo;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.MergeFrom;
import org.jvnet.basicjaxb.locator.DefaultRootObjectLocator;
import org.jvnet.hyperjaxb.lang.ExtendedJAXBEqualsStrategy;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputDomResolver;
import org.xml.sax.SAXException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public abstract class RoundtripTest extends AbstractEntityManagerSamplesTest
{
	private Boolean validateXml = true;
	public Boolean isValidateXml() { return validateXml; }
	public void setValidateXml(Boolean validateXml) { this.validateXml = validateXml; }

	public static enum SaveType { MERGE, PERSIST }

	@Override
	protected void checkSample(File sampleFile)
		throws Exception
	{
		checkSample(sampleFile, SaveType.MERGE);
	}

	protected void checkSample(File sampleFile, SaveType saveType)
		throws Exception
	{
		final JAXBContext context = createContext();
		final Unmarshaller unmarshaller = context.createUnmarshaller();

		if ( isValidateXml() )
			enableXmlSchemaValidator(unmarshaller, null, context);

		getLogger().debug("Unmarshalling sample.");
		Sample initialSample = new Sample(unmarshaller, sampleFile);

		getLogger().debug("Unmarshalling etalon.");
		Sample etalonSample = new Sample(unmarshaller, sampleFile);

		if ( getLogger().isTraceEnabled() )
		{
			if (etalonSample.getJAXBElement() != null)
			{
				final JAXBElement<Object> jaxbElement = etalonSample.getJAXBElement();
				String xml = "UNMARSHALLABLE";
		        if (isBindingElement(context, jaxbElement))
		        	xml = marshal(context, jaxbElement, true);
				getLogger().trace("Etalon element:\n" + xml);
			}
			else
			{
				final Object value = etalonSample.getValue();
				String xml = "UNMARSHALLABLE";
		        if (isBindingElement(context, value))
		        	xml = marshal(context, value, true);
		        else if (isElement(value))
	        		xml = marshalElement(value);
		        else
		        {
		        	JAXBElement<Object> valueElement = createJAXBElement(value);
		        	if ( valueElement != null )
		        		xml = marshal(context, valueElement, true);
		        }
		        getLogger().trace("Etalon object:\n" + xml);
			}
		}

		// Save
		getLogger().debug("Persisting the unmarshalled sample.");
		Object savedId = null;
		Class<?> savedClass = null;
		try ( final EntityManager saveManager = createEntityManager() )
		{
			// Transaction
			EntityTransaction saveTX = saveManager.getTransaction();

			Object savedEntity = null;
			saveTX.begin();
			switch ( saveType )
			{
				case MERGE:
					savedEntity = saveManager.merge(initialSample.getValue());
					break;
				case PERSIST:
					saveManager.persist(initialSample.getValue());
					savedEntity = initialSample.getValue();
					break;
			}
			saveTX.commit();

			savedClass = savedEntity.getClass();

			saveTX.begin();
			savedId = getId(saveManager, savedEntity);
			saveTX.commit();

			if ( getLogger().isTraceEnabled() )
			{
				String xml = "UNMARSHALLABLE";
		        if (isBindingElement(context, savedEntity))
		        	xml = marshal(context, savedEntity, true);
		        else if (isElement(savedEntity))
	        		xml = marshalElement(savedEntity);
		        else
		        {
		        	JAXBElement<Object> savedEntityElement = createJAXBElement(savedEntity);
		        	if ( savedEntityElement != null )
		        		xml = marshal(context, savedEntityElement, true);
		        }
				getLogger().trace("Saved object:\n" + xml);
			}
		}

		assertNotNull(savedId, "saved identifier");

		// Load
		getLogger().debug("Loading the persisted sample.");
		try ( EntityManager loadManager = createEntityManager() )
		{
			// Transaction
			EntityTransaction loadTX = loadManager.getTransaction();

			loadTX.begin();
			final Object loadedEntity = loadManager.find(savedClass, savedId);
			loadTX.commit();

			if ( getLogger().isTraceEnabled() )
			{
				String xml = "UNMARSHALLABLE";
		        if (isBindingElement(context, loadedEntity))
		        	xml = marshal(context, loadedEntity, true);
		        else if (isElement(loadedEntity))
	        		xml = marshalElement(loadedEntity);
		        else
		        {
		        	JAXBElement<Object> loadedEntityElement = createJAXBElement(loadedEntity);
		        	if ( loadedEntityElement != null )
		        		xml = marshal(context, loadedEntityElement, true);
		        }
				getLogger().trace("Loaded object:\n" + xml);
			}

			getLogger().debug("Checking the sample object identity: Etalon vs Loaded.");
			checkObjects(etalonSample.getValue(), loadedEntity);
			checkObject(etalonSample.getValue());

			// Check Copyable (CopyTo / Cloneable), when present.
			if ( getLogger().isDebugEnabled() )
			{
				getLogger().debug("Checking the sample object identity: Etalon vs Etalon clone.");
				checkCopyable(etalonSample.getValue());
			}

			// Deeper check Copyable (CopyTo / Cloneable) and Mergeable, when present.
			if ( getLogger().isTraceEnabled() )
			{
				getLogger().trace("Checking the sample object identity: Loaded vs Loaded clone.");
				checkCopyable(loadedEntity);

				getLogger().trace("Checking the sample object identity: Etalon vs Initial vs Loaded.");
				checkMergeable(etalonSample.getValue(), initialSample.getValue(), loadedEntity);
			}
		}
	}

	private void checkCopyable(Object value)
	{
		if ( value instanceof CopyTo )
		{
			CopyTo valueCopyTo = (CopyTo) value;
			Object valueClone = valueCopyTo.clone();
			checkObjects(valueCopyTo, valueClone);
		}
	}

	private void checkMergeable(Object value1, Object value2, Object value3)
	{
		if ( (value1 instanceof MergeFrom) && (value2 instanceof MergeFrom) && (value3 instanceof MergeFrom) )
		{
			MergeFrom value1MergeFrom = (MergeFrom) value1;
			MergeFrom value2MergeFrom = (MergeFrom) value2;
			MergeFrom lhsValueMergeFrom = (MergeFrom) value1MergeFrom.createNewInstance();
			lhsValueMergeFrom.mergeFrom(value1MergeFrom, value2MergeFrom);

			MergeFrom value3MergeFrom = (MergeFrom) value3;
			MergeFrom rhsValueMergeFrom = (MergeFrom) value1MergeFrom.createNewInstance();
			rhsValueMergeFrom.mergeFrom(value1MergeFrom, value3MergeFrom);

			checkObjects(lhsValueMergeFrom, rhsValueMergeFrom);
		}
	}

	protected void checkObject(final Object obj)
	{
		;
	}

	protected void checkObjects(final Object lhsObject, final Object rhsObject)
	{
		final EqualsStrategy strategy = new ExtendedJAXBEqualsStrategy();
		assertTrue(strategy.equals(new DefaultRootObjectLocator(lhsObject), new DefaultRootObjectLocator(rhsObject),
			lhsObject, rhsObject, true, true), "Objects NOT equal. Use DEBUG for location details.");
	}

	protected void generateXmlSchemaValidatorFromDom(JAXBContext context, Unmarshaller unmarshaller)
		throws IOException, SAXException, TransformerException
	{
		// Generate a Schema Validator from given the JAXB context.
		SchemaOutputDomResolver sodr = createSchemaOutputDomResolver(context);
		getLogger().debug("Xml Schema from DOM:\n{}\n", sodr.getSchemaDomNodeString());
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
		private JAXBElement<Object> jaxbElement;
		public JAXBElement<Object> getJAXBElement() { return jaxbElement; }
		public void setJAXBElement(JAXBElement<Object> jaxbElement) { this.jaxbElement = jaxbElement; }

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
				setJAXBElement((JAXBElement<Object>) unmarshalledDraft);
				setValue(getJAXBElement().getValue());
			}
			else
			{
				setJAXBElement(null);
				setValue(unmarshalledDraft);
			}
		}
	}

	public RoundtripTest()
	{
		super();
	}
}
