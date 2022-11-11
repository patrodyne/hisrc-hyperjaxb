package org.jvnet.hyperjaxb.ejb.jpa.strategy.processor;

import org.jvnet.hyperjaxb.persistence.jpa.JPAUtils;

import jakarta.enterprise.inject.Vetoed;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

@Vetoed
public class PersistenceMarshaller extends org.jvnet.hyperjaxb.ejb.strategy.processor.PersistenceMarshaller
{
	@Override
	protected Marshaller getMarshaller()
		throws JAXBException
	{
		return JPAUtils.createMarshaller();
	}
}
