package org.jvnet.hyperjaxb.sb.tests;

import jakarta.xml.bind.JAXBContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

public class AtributeDefinitionTest
{
	@Test
	public void testUnmarshall()
		throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(AttributeDefinition.class);
		try ( InputStream is = getClass().getResourceAsStream("attribute.xml") )
		{
			@SuppressWarnings("unchecked")
			AttributeDefinition<Integer> attributeDefinition =
				(AttributeDefinition<Integer>) context.createUnmarshaller().unmarshal(is);
			assertEquals(Integer.valueOf(5), attributeDefinition.getValue());
		}
	}

	@Test
	public void testSimpleUnmarshall()
		throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(SimpleAttributeDefinition.class);
		try ( InputStream is = getClass().getResourceAsStream("simpleAttribute.xml") )
		{
			@SuppressWarnings("unchecked")
			SimpleAttributeDefinition<Integer> attributeDefinition =
				(SimpleAttributeDefinition<Integer>) context.createUnmarshaller().unmarshal(is);
			// TODO: Review SimpleAttributeDefinition.afterUnmarshal(Unmarshaller unmarshaller, Object parent)
			// TODO: Review SimpleAttributeDefinition.beforeMarshal(Marshaller marshaller, Object parent)
			assertEquals(String.valueOf(5), attributeDefinition.getContent(), "Expected content is 5.");
			assertNull(attributeDefinition.getValue(), "Expected value is null.");
		}
	}
}
