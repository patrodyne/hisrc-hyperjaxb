package org.example.pub;

import java.io.File;

import org.jvnet.basicjaxb.lang.ContextUtils;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;
import org.jvnet.hyperjaxb.ejb.util.EntityUtils;

import jakarta.persistence.EntityManager;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
    public String getContextPath()
	{
        return "org.example.pub";
    }

	@Override
    public String getPersistenceUnitName()
	{
        return "org.example.pub";
    }
	
	@Override
	protected void checkSample(File sampleFile)
		throws Exception
	{
		final JAXBContext context = createContext();
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setListener(new PublicationUL());
		
		if ( isValidateXml() )
			generateXmlSchemaValidatorFromDom(context, unmarshaller);
		
		getLogger().debug("Unmarshalling sample.");
		Sample initialSample = new Sample(unmarshaller, sampleFile);
		
		getLogger().debug("Unmarshalling etalon.");
		Sample etalonSample = new Sample(unmarshaller, sampleFile);
		
		getLogger().debug("Persisting the unmarshalled sample.");
		final EntityManager saveManager = createEntityManager();
		saveManager.getTransaction().begin();
		final Object mergedEntity = saveManager.merge(initialSample.getValue());
		saveManager.getTransaction().commit();
		final Object mergedId = EntityUtils.getId(saveManager, mergedEntity);
		// Close the save session
		saveManager.clear();
		saveManager.close();
		
		getLogger().debug("Loading the persisted sample.");
		final EntityManager loadManager = createEntityManager();
		final Object loadedEntity = loadManager.find(mergedEntity.getClass(), mergedId);
		
		if (etalonSample.getElement() != null)
		{
			final JAXBElement<Object> etalonElement = etalonSample.getElement();
			final JAXBElement<Object> mergedElement = wrap(etalonElement, mergedEntity);
			final JAXBElement<Object> loadedElement = wrap(etalonElement, loadedEntity);
			getLogger().debug("Etalon element:\n" + ContextUtils.toString(context, etalonElement));
			getLogger().debug("Merged element:\n" + ContextUtils.toString(context, mergedElement));
			getLogger().debug("Loaded element:\n" + ContextUtils.toString(context, loadedElement));
		}
		else
		{
			getLogger().debug("Etalon object:\n" + ContextUtils.toString(context, etalonSample.getValue()));
			getLogger().debug("Merged object:\n" + ContextUtils.toString(context, mergedEntity));
			getLogger().debug("Loaded object:\n" + ContextUtils.toString(context, loadedEntity));
		}
		
		getLogger().debug("Checking the sample object identity: Merged vs Loaded.");
		checkObjects(mergedEntity, loadedEntity);
		
		getLogger().debug("Checking the sample object identity: Etalon vs Loaded.");
		checkObjects(etalonSample.getValue(), loadedEntity);
		
		// Close the load session
		loadManager.clear();
		loadManager.close();
	}

	private JAXBElement<Object> wrap(JAXBElement<Object> element, Object obj)
	{
		return new JAXBElement<Object>(element.getName(), element.getDeclaredType(), obj);
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
