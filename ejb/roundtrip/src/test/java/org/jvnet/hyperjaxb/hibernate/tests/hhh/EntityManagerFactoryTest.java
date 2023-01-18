package org.jvnet.hyperjaxb.hibernate.tests.hhh;

import static jakarta.persistence.Persistence.createEntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManagerFactory;

public class EntityManagerFactoryTest {

	@Test
	public void entityManagerFactoryCreated() throws IOException
	{
		Map<String, String> properties = createEntityManagerFactoryProperties(getClass());
		String persistenceUnitName = getClass().getPackage().getName();
		EntityManagerFactory entityManagerFactory =	createEntityManagerFactory(persistenceUnitName, properties);
		assertNotNull(entityManagerFactory);
	}

	public String getPersistenceUnitName() {
		final Package _package = getClass().getPackage();
		final String name = _package.getName();
		if (name == null) {
			return "root";
		} else {
			return name;
		}
	}
}
