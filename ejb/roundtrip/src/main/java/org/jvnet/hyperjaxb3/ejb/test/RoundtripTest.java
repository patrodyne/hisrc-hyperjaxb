package org.jvnet.hyperjaxb3.ejb.test;

import static org.jvnet.jaxb2_commons.locator.util.LocatorUtils.item;
import static org.jvnet.jaxb2_commons.locator.util.LocatorUtils.property;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang3.ObjectUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jvnet.hyperjaxb3.ejb.util.EntityUtils;
import org.jvnet.hyperjaxb3.lang.builder.ExtendedJAXBEqualsStrategy;
import org.jvnet.hyperjaxb3.xml.datatype.util.XMLGregorianCalendarUtils;
import org.jvnet.jaxb2_commons.lang.ContextUtils;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.locator.DefaultRootObjectLocator;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;
import org.w3c.dom.Node;

public abstract class RoundtripTest extends AbstractEntityManagerSamplesTest {

	@Override
	protected void checkSample(File sample) throws Exception {
		final JAXBContext context = createContext();
		logger.debug("Unmarshalling.");
		final Unmarshaller unmarshaller = context.createUnmarshaller();

		final JAXBElement unmarshalledElement;
		final Object unmarshalledObject;

		final Object unmarshalledDraft = unmarshaller.unmarshal(sample);

		if (unmarshalledDraft instanceof JAXBElement) {
			unmarshalledElement = (JAXBElement) unmarshalledDraft;
			unmarshalledObject = unmarshalledElement.getValue();
		} else {
			unmarshalledElement = null;
			unmarshalledObject = unmarshalledDraft;
		}

		final JAXBElement etalonElement;
		final Object etalonObject;

		final Object etalonDraft = unmarshaller.unmarshal(sample);

		if (etalonDraft instanceof JAXBElement) {
			etalonElement = (JAXBElement) etalonDraft;
			etalonObject = etalonElement.getValue();
		} else {
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
		final Object loadedObject = loadManager.find(mergedObject.getClass(),
				id);
		logger.debug("Closing the session.");

		if (unmarshalledElement != null) {
			final JAXBElement<Object> mergedElement = new JAXBElement(
					unmarshalledElement.getName(),
					unmarshalledElement.getDeclaredType(), mergedObject);

			final JAXBElement loadedElement = new JAXBElement(
					unmarshalledElement.getName(),
					unmarshalledElement.getDeclaredType(), loadedObject);

			logger.debug("Initial object:\n"
					+ ContextUtils.toString(context, etalonElement));

			logger.debug("Source object:\n"
					+ ContextUtils.toString(context, mergedElement));
			logger.debug("Result object:\n"
					+ ContextUtils.toString(context, loadedElement));

		} else {
			logger.debug("Initial object:\n"
					+ ContextUtils.toString(context, etalonObject));

			logger.debug("Source object:\n"
					+ ContextUtils.toString(context, mergedObject));
			logger.debug("Result object:\n"
					+ ContextUtils.toString(context, loadedObject));

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
		
		assertTrue("Objects NOT equal. Use DEBUG for location details.", strategy.equals(
				new DefaultRootObjectLocator(leftObject),
				new DefaultRootObjectLocator(rightObject),
				leftObject,
				rightObject));
	}
}
