package org.jvnet.hyperjaxb3.ejb.test;

import static org.jvnet.jaxb2_commons.locator.util.LocatorUtils.item;
import static org.jvnet.jaxb2_commons.locator.util.LocatorUtils.property;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang3.ObjectUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.jvnet.hyperjaxb3.ejb.util.EntityUtils;
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
		final EqualsStrategy strategy = new org.jvnet.hyperjaxb3.lang.builder.ExtendedJAXBEqualsStrategy()
		{
			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, boolean left, boolean right)
			{
				if ( super.equals(leftLocator, rightLocator, left, right) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, left, right);
					return false;
				}
			}

			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, byte left, byte right)
			{
				if ( super.equals(leftLocator, rightLocator, left, right) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, left, right);
					return false;
				}
			}

			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, char left, char right)
			{
				if ( super.equals(leftLocator, rightLocator, left, right) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, left, right);
					return false;
				}
			}

			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, long left, long right)
			{
				if ( super.equals(leftLocator, rightLocator, left, right) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, left, right);
					return false;
				}
			}

			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, int left, int right)
			{
				if ( super.equals(leftLocator, rightLocator, left, right) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, left, right);
					return false;
				}
			}

			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, short left, short right)
			{
				if ( super.equals(leftLocator, rightLocator, left, right) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, left, right);
					return false;
				}
			}
			
			@Override
			public boolean equals(ObjectLocator leftLocator, ObjectLocator rightLocator, Object lhs, Object rhs)
			{
				if ( super.equals(leftLocator, rightLocator, lhs, rhs) )
					return true;
				else
				{
					debug(leftLocator, rightLocator, lhs, rhs);
					return false;
				}
			}

			private void debug(ObjectLocator lhsLocator, ObjectLocator rhsLocator, Object lhs, Object rhs)
			{
				logger.debug("Objects are NOT equal!");
				debugMessage("LHS", lhsLocator, lhs);
				debugMessage("RHS", rhsLocator, rhs);
			}

			private void debugMessage(String label, ObjectLocator locator, Object obj)
			{
				String value = "null";
				String text = "";
				
				if ( obj != null )
				{
					if (obj instanceof Collection<?>)
						value = obj.getClass().getName() + "[" + ((Collection) obj).size() +"]";
					else if ( obj != null && obj.getClass().isArray() )
						value = obj.getClass().getName() + "[" + ((Object[]) obj).length +"]";
					else
						value = obj.toString();
				}
				
				if (locator != null)
					text = label + ": " + "{"+locator.getPathAsString()+"} -> " + value;
				else
					text = label + ": " + "{} -> " + value;
					
				logger.debug(text);
			}
		};
		
		assertTrue("Objects NOT equal. Use DEBUG for location details.", strategy.equals(
				new DefaultRootObjectLocator(leftObject),
				new DefaultRootObjectLocator(rightObject),
				leftObject,
				rightObject));
	}
}
