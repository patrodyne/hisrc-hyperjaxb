package org.jvnet.hyperjaxb.jpa;

import static jakarta.xml.bind.JAXBContext.newInstance;
import static java.util.Collections.unmodifiableSet;
import static org.jvnet.basicjaxb.lang.ContextUtils.getContextPath;
import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.basicjaxb.util.CustomizationUtils.getCustomizations;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;

import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CCustomizable;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class Customizations
{
	public static final String NAMESPACE_URI = "urn:jvnet.org:hyperjaxb:jpa";
	public static final String JPA_NAMESPACE_URI = "https://jakarta.ee/xml/ns/persistence";
	public static final String ORM_NAMESPACE_URI = "https://jakarta.ee/xml/ns/persistence/orm";
	public static final Set<String> NAMESPACES;
	static
	{
		final Set<String> namespaces = new HashSet<String>(3);
		namespaces.add(NAMESPACE_URI);
		namespaces.add(JPA_NAMESPACE_URI);
		namespaces.add(ORM_NAMESPACE_URI);
		NAMESPACES = unmodifiableSet(namespaces);
	}
	public static final String CONTEXT_PATH = getContextPath
	(
		ee.jakarta.xml.ns.persistence.ObjectFactory.class,
		ee.jakarta.xml.ns.persistence.orm.ObjectFactory.class,
		org.jvnet.hyperjaxb.jpa.ObjectFactory.class
	);
	
	private static final JAXBContext context;
	static
	{
		try
		{
			context = newInstance(CONTEXT_PATH, org.jvnet.hyperjaxb.jpa.ObjectFactory.class.getClassLoader());
		}
		catch (JAXBException e)
		{
			throw new ExceptionInInitializerError(e);
		}
	}
	public static JAXBContext getContext()
	{
		return context;
	}

	public static QName hj(String localPart)
	{
		return new QName(NAMESPACE_URI, localPart);
	}

	public static QName orm(String localPart)
	{
		return new QName(ORM_NAMESPACE_URI, localPart);
	}

	public static final QName PERSISTENCE_ELEMENT_NAME = hj("persistence");
	public static final QName ITEM_ELEMENT_NAME = hj("item");
	public static final QName IGNORED_ELEMENT_NAME = hj("ignored");
	public static final QName IGNORED_PACKAGE_ELEMENT_NAME = hj("ignored-package");
	public static final QName GENERATED_CLASS_ELEMENT_NAME = hj("generated-class");
	public static final QName GENERATED_PROPERTY_ELEMENT_NAME = hj("generated-property");
	public static final QName GENERATED_ID_ELEMENT_NAME = hj("generated-id");
	public static final QName GENERATED_VERSION_ELEMENT_NAME = hj("generated-version");
	public static final QName EMBEDDED_ID_ELEMENT_NAME = hj("embedded-id");
	public static final QName ID_ELEMENT_NAME = hj("id");
	public static final QName VERSION_ELEMENT_NAME = hj("version");
	public static final QName TABLE_ELEMENT_NAME = hj("table");
	public static final QName COLUMN_ELEMENT_NAME = hj("column");
	public static final QName ONE_TO_MANY_ELEMENT_NAME = hj("one-to-many");
	public static final QName ONE_TO_ONE_ELEMENT_NAME = hj("one-to-one");
	public static final QName MANY_TO_ONE_ELEMENT_NAME = hj("many-to-one");
	public static final QName MANY_TO_MANY_ELEMENT_NAME = hj("many-to-many");
	public static final QName ELEMENT_COLLECTION_ELEMENT_NAME = hj("element-collection");
	public static final QName BASIC_ELEMENT_NAME = hj("basic");
	public static final QName EMBEDDED_ELEMENT_NAME = hj("embedded");
	
	public static QName GENERATED_ELEMENT_NAME = new QName("urn:jvnet.org:basicjaxb:xjc", "generated");
	//
	public static final QName ENTITY_ELEMENT_NAME = hj("entity");
	public static final QName MAPPED_SUPERCLASS_ELEMENT_NAME = hj("mapped-superclass");
	public static final QName EMBEDDABLE_ELEMENT_NAME = hj("embeddable");
	public static final QName JAXB_CONTEXT_ELEMENT_NAME = hj("jaxb-context");

	public static void markAsAcknowledged(final CPluginCustomization customization)
	{
		if (customization != null && NAMESPACE_URI.equals(customization.element.getNamespaceURI()))
			customization.markAsAcknowledged();
	}

	public static void markAsAcknowledged(final CCustomizations customizations)
	{
		for (CPluginCustomization customization : customizations)
			markAsAcknowledged(customization);
	}

	public static void markAsAcknowledged(final CPropertyInfo customizable)
	{
		markAsAcknowledged(getCustomizations(customizable));
	}

	public static void markAsAcknowledged(final CClassInfo customizable)
	{
		markAsAcknowledged(getCustomizations(customizable));
	}

	public static CPluginCustomization createCustomization(Object object, Locator locator)
	{
		final Marshaller marshaller;
		try
		{
			marshaller = getContext().createMarshaller();
		}
		catch (JAXBException ex)
		{
			final AssertionError error = new AssertionError("Marshaller could not be created.");
			error.initCause(ex);
			throw error;
		}
		
		final DOMResult result = new DOMResult();
		try
		{
			marshaller.marshal(object, result);
		}
		catch (JAXBException ex)
		{
			throw new IllegalArgumentException("The argument could not be marshalled.", ex);
		}
		
		final Node node = result.getNode();
		final Element element;
		if (node instanceof Document)
		{
			final Document document = (Document) node;
			element = document.getDocumentElement();
		}
		else if (node instanceof Element)
			element = (Element) node;
		else
			throw new AssertionError("Could not retrive the element from the marshalled result.");

		return CustomizationUtils.createCustomization(element, locator);
	}

	private final static ee.jakarta.xml.ns.persistence.ObjectFactory persistenceObjectFactory = new ee.jakarta.xml.ns.persistence.ObjectFactory();
	public static ee.jakarta.xml.ns.persistence.ObjectFactory getPersistenceObjectFactory()
	{
		return persistenceObjectFactory;
	}

	private final static ee.jakarta.xml.ns.persistence.orm.ObjectFactory ormObjectFactory = new ee.jakarta.xml.ns.persistence.orm.ObjectFactory();
	public static ee.jakarta.xml.ns.persistence.orm.ObjectFactory getOrmObjectFactory()
	{
		return ormObjectFactory;
	}

	private final static org.jvnet.hyperjaxb.jpa.ObjectFactory customizationsObjectFactory = new org.jvnet.hyperjaxb.jpa.ObjectFactory();
	public static org.jvnet.hyperjaxb.jpa.ObjectFactory getCustomizationsObjectFactory()
	{
		return customizationsObjectFactory;
	}

	public static CPluginCustomization createCustomization$Ignored(Locator locator)
	{
		final CPluginCustomization customization =
			CustomizationUtils.createCustomization(IGNORED_ELEMENT_NAME, locator);
		customization.markAsAcknowledged();
		return customization;
	}

	public static CPluginCustomization createCustomization$Generated(Locator locator)
	{
		final CPluginCustomization customization =
			CustomizationUtils.createCustomization(GENERATED_ELEMENT_NAME, locator);
		customization.markAsAcknowledged();
		return customization;
	}

	public static void markIgnored(CCustomizable customizable)
	{
		Locator locator = customizable.getLocator();
		if ( (locator == null) && (customizable.getSchemaComponent() != null) )
			locator = customizable.getSchemaComponent().getLocator();
		customizable.getCustomizations().add(createCustomization$Ignored(locator));
	}

	public static void markGenerated(CCustomizable customizable)
	{
		Locator locator = customizable.getLocator();
		if ( (locator == null) && (customizable.getSchemaComponent() != null) )
			locator = customizable.getSchemaComponent().getLocator();
		customizable.getCustomizations().add(createCustomization$Generated(locator));
	}

	public static boolean isGenerated(CClassInfo classInfo)
	{
		return containsCustomization(classInfo, GENERATED_ELEMENT_NAME);
	}

	public static boolean isGenerated(CPropertyInfo propertyInfo)
	{
		return containsCustomization(propertyInfo, GENERATED_ELEMENT_NAME);
	}

	public static boolean isVersion(CPropertyInfo propertyInfo)
	{
		return containsCustomization(propertyInfo, VERSION_ELEMENT_NAME);
	}
}
