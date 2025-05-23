package org.jvnet.hyperjaxb.ejb.strategy.processor;

import java.io.StringWriter;
import java.io.Writer;

import org.jvnet.hyperjaxb.persistence.jpa.JPAUtils;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.fmt.JTextFile;

import ee.jakarta.xml.ns.persistence.Persistence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

@ApplicationScoped
public class PersistenceMarshaller
{
	protected Marshaller getMarshaller()
		throws JAXBException
	{
		return JPAUtils.createMarshaller();
	}

	public void marshallPersistence(JCodeModel codeModel, Persistence persistence)
		throws Exception
	{
		// final JPackage defaultPackage = codeModel._package("");
		final JPackage metaInfPackage = codeModel._package("META-INF");
		final JTextFile persistenceXmlFile = new JTextFile("persistence.xml");
		metaInfPackage.addResourceFile(persistenceXmlFile);
		final Writer writer = new StringWriter();
		getMarshaller().marshal(persistence, writer);
		persistenceXmlFile.setContents(writer.toString());
	}
}
