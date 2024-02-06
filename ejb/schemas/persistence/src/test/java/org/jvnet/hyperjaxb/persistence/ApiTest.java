package org.jvnet.hyperjaxb.persistence;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jvnet.basicjaxb.config.LocatorInputFactory.locatorExists;

import org.junit.jupiter.api.Test;

/**
 * Unit test to compare the Jakarta and HyperJAXB persistence and orm specifications, in use.
 * 
 * @see hisrc-hyperjaxb/ejb/schemas/customizations/src/main/resources/customizations.xsd
 */
public class ApiTest
{
	public static final String JAKARTA_PERSISTENCE_SPEC = "3_0";
	public static final String JAKARTA_PERSISTENCE_ORM_SPEC = "3_1";
	public static final String JAKARTA_PERSISTENCE_PKG_SPEC = "3.1";
	
	public static final String HYPERJAXB_PERSISTENCE_SPEC = JAKARTA_PERSISTENCE_SPEC;
	public static final String HYPERJAXB_PERSISTENCE_ORM_SPEC = JAKARTA_PERSISTENCE_ORM_SPEC;
	
	private static final Class<?> JAKARTA_PERSISTENCE_CLASS = jakarta.persistence.Entity.class;
	private static final Class<?> HYPERJAXB_PERSISTENCE_CLASS = ee.jakarta.xml.ns.persistence.Persistence.class;
	
	@Test
	public void testSpecUpToDate() throws Exception
	{
		String ps = JAKARTA_PERSISTENCE_CLASS.getPackage().getSpecificationVersion();
		assertEquals(JAKARTA_PERSISTENCE_PKG_SPEC, ps, "Jakarta Persistence Specification");
		
		String jakartaPersistenceSpecLocation = format("classpath:/jakarta/persistence/persistence_%s.xsd", JAKARTA_PERSISTENCE_SPEC);
		assertTrue(locatorExists(jakartaPersistenceSpecLocation, JAKARTA_PERSISTENCE_CLASS), "Jakarta Persistence Spec is " + JAKARTA_PERSISTENCE_SPEC);

		String jakartaPersistenceOrmSpecLocation = format("classpath:/jakarta/persistence/orm_%s.xsd", JAKARTA_PERSISTENCE_ORM_SPEC);
		assertTrue(locatorExists(jakartaPersistenceOrmSpecLocation, JAKARTA_PERSISTENCE_CLASS), "Jakarta Persistence ORM Spec is " + JAKARTA_PERSISTENCE_ORM_SPEC);
		
		String hyperjaxbPersistenceSpecLocation = format("classpath:/persistence/persistence_%s.xsd", HYPERJAXB_PERSISTENCE_SPEC);
		assertTrue(locatorExists(hyperjaxbPersistenceSpecLocation, HYPERJAXB_PERSISTENCE_CLASS), "HyperJAXB Persistence Spec is " + HYPERJAXB_PERSISTENCE_SPEC);

		String hyperjaxbPersistenceOrmSpecLocation = format("classpath:/persistence/orm/orm_%s.xsd", HYPERJAXB_PERSISTENCE_ORM_SPEC);
		assertTrue(locatorExists(hyperjaxbPersistenceOrmSpecLocation, HYPERJAXB_PERSISTENCE_CLASS), "HyperJAXB Persistence ORM Spec is " + HYPERJAXB_PERSISTENCE_ORM_SPEC);
	}
}
