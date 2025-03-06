package org.jvnet.hyperjaxb.ejb.tests.anyelement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import blog.anyelement.adapted.model.Method;
import blog.anyelement.adapted.model.MethodAnyItem;
import blog.anyelement.adapted.ParameterAdapter;
import blog.anyelement.adapted.other.Address;
import blog.anyelement.adapted.other.Parameter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Unmarshaller;

public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	@BeforeEach
	public void setUp() throws Exception
	{
		setFailFast(true);
		super.setUp();
	}
	
	@Override
	public String getContextPath()
	{
		return "blog.anyelement.adapted.model";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "blog.anyelement.adapted.model";
	}
	
	@Override
	protected void checkSample(File sampleFile)
		throws Exception
	{
		super.checkSample(sampleFile);
		
		final JAXBContext context = createContext();
		final JAXBContext otherContext = JAXBContext.newInstance(Address.class);
		
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		Object sampleObject = unmarshaller.unmarshal(sampleFile);
		assertNotNull(sampleObject);
		Method sampleMethod = (Method) JAXBIntrospector.getValue(sampleObject);
		
		assertEquals("addCustomer", sampleMethod.getName());
		
		for ( MethodAnyItem anyItem : sampleMethod.getAnyItems() )
		{
			// HJ ItemElement as String
			String itemElementAsString = anyItem.getItemElement();
			assertNotNull(itemElementAsString, "itemElementAsString");
			
			// HJ Item as org.w3c.dom.Node
			Object item = anyItem.getItem();
			assertNotNull(item, "item");
			
			assertInstanceOf(Node.class, item, "item");
			Node itemNode = (Node) item;
			
			String valueNS = itemNode.getNamespaceURI();
			assertEquals("urn:blog:anyelement:adapted:other", valueNS, "valueNS");
			
			String valueName = itemNode.getLocalName();
			assertNotNull(valueName, "valueName");
			
			Node valueTypeNode = itemNode.getAttributes().getNamedItem("type");
			assertNotNull(valueTypeNode, "valueTypeNode");
			
			String valueType = valueTypeNode.getNodeValue();
			assertNotNull(valueType, "valueType");
			
			Node valueNode = itemNode.getFirstChild();
			assertNotNull(valueNode, "valueNode");
			
			String valueText = valueNode.getTextContent();
			assertNotNull(valueText, "valueText");
			
			// HJ Item as org.w3c.dom.Element
			assertInstanceOf(Element.class, item, "item");
			Element itemElement = (Element) item;
			
			ParameterAdapter pa = new ParameterAdapter(otherContext);
			Parameter parameter = pa.unmarshal(itemElement);
			assertNotNull(parameter, "parameter");
			assertEquals(valueName, parameter.getName(), "valueName");
			assertNotNull(parameter.getValue(), "parameter value" );
			assertEquals(valueType, parameter.getValue().getClass().getName(), "valueType");
			
			if ( parameter.getValue() instanceof Address )
			{
				Address address = (Address) parameter.getValue();
				assertNotNull(address.getStreet(), "address street");
				assertNotNull(address.getCity(), "address city");
			}

			// HJ ItemObject
			assertNull(anyItem.getItemObject());
		}
	}
}
