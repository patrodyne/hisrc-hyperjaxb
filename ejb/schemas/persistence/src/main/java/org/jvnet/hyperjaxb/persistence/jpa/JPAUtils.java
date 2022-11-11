package org.jvnet.hyperjaxb.persistence.jpa;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.jvnet.hyperjaxb.persistence.util.PersistenceUtils;

public class JPAUtils {

	public static Marshaller createMarshaller() throws JAXBException
	{
		final Marshaller marshaller = PersistenceUtils.CONTEXT.createMarshaller();
		
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,	JPAConstants.SCHEMA_LOCATION);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("org.glassfish.jaxb.namespacePrefixMapper", PersistenceUtils.NAMESPACE_PREFIX_MAPPER);

		return marshaller;
	}

}
