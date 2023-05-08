package org.jvnet.hyperjaxb.ejb.jpa.strategy.mapping;

import static org.jvnet.hyperjaxb.ejb.Constants.ORM_JPA_VERSION;

import org.jvnet.hyperjaxb.persistence.jpa.JPAUtils;

import ee.jakarta.xml.ns.persistence.orm.EntityMappings;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class MarshalMappings extends org.jvnet.hyperjaxb.ejb.strategy.mapping.MarshalMappings
{
	@Override
	protected Marshaller getMarshaller()
		throws JAXBException
	{
		return JPAUtils.createMarshaller();
	}

	@Override
	protected EntityMappings createEntityMappings()
	{
		final EntityMappings entityMappings = new EntityMappings();
		entityMappings.setVersion(ORM_JPA_VERSION);
		return entityMappings;
	}
}
