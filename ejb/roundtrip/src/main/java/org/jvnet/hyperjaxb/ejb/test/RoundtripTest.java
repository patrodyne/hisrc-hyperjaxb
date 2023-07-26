package org.jvnet.hyperjaxb.ejb.test;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jvnet.basicjaxb.lang.ContextUtils.createSchemaOutputDomResolver;

import java.io.File;
import java.io.IOException;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jvnet.basicjaxb.lang.ContextUtils;
import org.jvnet.basicjaxb.lang.CopyTo;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.MergeFrom;
import org.jvnet.basicjaxb.locator.DefaultRootObjectLocator;
import org.jvnet.hyperjaxb.ejb.util.EntityUtils;
import org.jvnet.hyperjaxb.lang.builder.ExtendedJAXBEqualsStrategy;
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

	@Override
	protected void checkSample(File sampleFile)
		throws Exception
	{
		final JAXBContext context = createContext();
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		
		if ( isValidateXml() )
			ContextUtils.enableXmlSchemaValidator(unmarshaller, null, context);
		
		getLogger().debug("Unmarshalling sample.");
		Sample initialSample = new Sample(unmarshaller, sampleFile);
		
		getLogger().debug("Unmarshalling etalon.");
		Sample etalonSample = new Sample(unmarshaller, sampleFile);
		
		if ( getLogger().isTraceEnabled() )
		{
			if (initialSample.getElement() != null)
			{
				final JAXBElement<Object> initialElement = initialSample.getElement();
				getLogger().trace("Initial element:\n" + ContextUtils.toString(context, initialElement));
			}
			else
				getLogger().trace("Initial object:\n" + ContextUtils.toString(context, initialSample.getValue()));
		}
		
		// Save
		getLogger().debug("Persisting the unmarshalled sample.");
		Object mergedId = null;
		Class<?> mergedClass = null;
		try ( final EntityManager saveManager = createEntityManager() )
		{
			// Transaction
			EntityTransaction saveTX = saveManager.getTransaction();
			
			saveTX.begin();
			Object mergedEntity = saveManager.merge(initialSample.getValue());
			saveTX.commit();
			
			mergedClass = mergedEntity.getClass();
			
			saveTX.begin();
			mergedId = EntityUtils.getId(saveManager, mergedEntity);
			saveTX.commit();
			
			if ( getLogger().isTraceEnabled() )
				getLogger().trace("Merged object:\n" + ContextUtils.toString(context, mergedEntity));
			
		}
		
		assertNotNull(mergedId, "merged identifier");
		
		// Load
		getLogger().debug("Loading the persisted sample.");
		try ( EntityManager loadManager = createEntityManager() )
		{
			// Transaction
			EntityTransaction loadTX = loadManager.getTransaction();
			
			loadTX.begin();
			final Object loadedEntity = loadManager.find(mergedClass, mergedId);
			loadTX.commit();

			if ( getLogger().isTraceEnabled() )
				getLogger().trace("Loaded object:\n" + ContextUtils.toString(context, loadedEntity));
			
			getLogger().debug("Checking the sample object identity: Etalon vs Loaded.");
			checkObjects(etalonSample.getValue(), loadedEntity);

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

				getLogger().trace("Checking the sample object identity: Etalon vs Initial vs Loaded merge.");
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
	
	protected void checkObjects(final Object lhsObject, final Object rhsObject)
	{
		final EqualsStrategy strategy = new ExtendedJAXBEqualsStrategy();
		assertTrue(strategy.equals(new DefaultRootObjectLocator(lhsObject), new DefaultRootObjectLocator(rhsObject),
			lhsObject, rhsObject, true, true), "Objects NOT equal. Use DEBUG for location details.");
	}
	
	protected void generateXmlSchemaValidatorFromDom(JAXBContext context, Unmarshaller unmarshaller)
		throws IOException, SAXException
	{
		// Generate a Schema Validator from given the JAXB context.
		SchemaOutputDomResolver sodr = createSchemaOutputDomResolver(context);
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
