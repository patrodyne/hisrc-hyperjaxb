package org.jvnet.hyperjaxb.xsom.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.jvnet.hyperjaxb.xsom.SimpleTypeAnalyzer;

import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.parser.XSOMParser;

public class SimpleTypesAnalyzerTest
{
	public static final String NS = "urn:org.jvnet.hyperjaxb.xsom.tests";
	public static final String SCHEMA_RESOURCE =
		SimpleTypesAnalyzerTest.class.getPackage().getName().replace('.', '/') + "/" + "SimpleTypesAnalyze.xsd";

	private XSSchemaSet schemaSet;
	public XSSchemaSet getSchemaSet() { return schemaSet; }
	public void setSchemaSet(XSSchemaSet schemaSet) { this.schemaSet = schemaSet; }
	
	@BeforeEach
	public void setUp() throws Exception
	{
		setSchemaSet(parse(SCHEMA_RESOURCE));
	}

	@Test
	public void testLength() throws Exception
	{
//		 XSSimpleType simpleType = schema.getSimpleType("length");
//		 simpleType.toString();
//		 for (Entry<String, XSType> entry : schemaSet.getSimpleType(arg0, arg1)getTypes().entrySet())
//			 log.debug(entry.getKey());
		
		final XSSimpleType minLength = getSchemaSet().getSimpleType(NS, "minLength");
		final XSSimpleType maxLength = getSchemaSet().getSimpleType(NS, "maxLength");
		final XSSimpleType length = getSchemaSet().getSimpleType(NS, "length");
		final XSSimpleType digits = getSchemaSet().getSimpleType(NS, "digits");
		final XSSimpleType totalDigits = getSchemaSet().getSimpleType(NS, "totalDigits");
		final XSSimpleType fractionDigits = getSchemaSet().getSimpleType(NS, "fractionDigits");
		
		assertEquals(Long.valueOf(5), SimpleTypeAnalyzer.getMinLength(minLength));
		assertEquals(null, SimpleTypeAnalyzer.getMaxLength(minLength));
		assertEquals(null, SimpleTypeAnalyzer.getLength(minLength));
		assertEquals(Long.valueOf(5), SimpleTypeAnalyzer.getMinLength(maxLength));
		assertEquals(Long.valueOf(10), SimpleTypeAnalyzer.getMaxLength(maxLength));
		assertEquals(null, SimpleTypeAnalyzer.getLength(maxLength));
		assertEquals(Long.valueOf(5), SimpleTypeAnalyzer.getMinLength(length));
		assertEquals(Long.valueOf(10), SimpleTypeAnalyzer.getMaxLength(length));
		assertEquals(Long.valueOf(8), SimpleTypeAnalyzer.getLength(length));
		assertEquals(Long.valueOf(5), SimpleTypeAnalyzer.getTotalDigits(digits));
		assertEquals(Long.valueOf(2), SimpleTypeAnalyzer.getFractionDigits(digits));
		assertEquals(Long.valueOf(3), SimpleTypeAnalyzer.getTotalDigits(totalDigits));
		assertEquals(null, SimpleTypeAnalyzer.getFractionDigits(totalDigits));
		assertEquals(null, SimpleTypeAnalyzer.getTotalDigits(fractionDigits));
		assertEquals(Long.valueOf(2), SimpleTypeAnalyzer.getFractionDigits(fractionDigits));
	}

	private XSSchemaSet parse(String resource) throws Exception
	{
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final XSOMParser parser = new XSOMParser(factory);
		
		parser.setErrorHandler(null);
		parser.setEntityResolver(null);
		final URL resourceUrl = getClass().getClassLoader().getResource(resource);
		
		// parser.parseSchema(
		//
		// new File("myschema.xsd"));
		// parser.parseSchema( new File("XHTML.xsd"));
		
		parser.parse(resourceUrl);
		XSSchemaSet sset = parser.getResult();
		return sset;
	}
}
