package org.jvnet.hyperjaxb.hibernate.tests.hhh;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesBaseFile;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.getPersistencePropertiesMoreFile;

import java.io.IOException;
import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.Test;

public class EntityManagerFactoryTest {

	@Test
	public void entityManagerFactoryCreated() throws IOException {

		final Properties properties = new Properties();
		properties.load(getClass().getClassLoader().getResourceAsStream(getPersistencePropertiesBaseFile()));
		Properties propertiesMore = new Properties();
		propertiesMore.load(getClass().getClassLoader().getResourceAsStream(getPersistencePropertiesMoreFile()));
		properties.putAll(propertiesMore);

		final String persistenceUnitName = getClass().getPackage().getName();
		final EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory(persistenceUnitName, properties);
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