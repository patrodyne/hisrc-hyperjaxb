package blog.anyelement.adapted;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import blog.anyelement.adapted.other.Parameter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ParameterAdapter extends XmlAdapter<Element, Parameter>
{
	private ClassLoader classLoader;
	private DocumentBuilder documentBuilder;
	private JAXBContext jaxbContext;

	public ParameterAdapter()
	{
		classLoader = Thread.currentThread().getContextClassLoader();
	}

	public ParameterAdapter(JAXBContext jaxbContext)
	{
		this();
		this.jaxbContext = jaxbContext;
	}

	private DocumentBuilder getDocumentBuilder()
		throws Exception
	{
		// Lazy load the DocumentBuilder as it is not used for unmarshalling.
		if ( null == documentBuilder )
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			documentBuilder = dbf.newDocumentBuilder();
		}
		return documentBuilder;
	}

	private JAXBContext getJAXBContext(Class<?> type)
		throws Exception
	{
		// If a JAXBContext is not set, create a new one based on the type.
		if ( null == jaxbContext )
			return JAXBContext.newInstance(type);
		return jaxbContext;
	}

	@Override
	public Element marshal(Parameter parameter)
		throws Exception
	{
		Element element = null;
		
		if ( parameter != null )
		{
			// 1. Build the JAXBElement to wrap the instance of Parameter.
			QName rootElement = new QName(parameter.getName());
			Object value = parameter.getValue();
			Class<?> type = value.getClass();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			JAXBElement jaxbElement = new JAXBElement(rootElement, type, value);
			
			// 2. Marshal the JAXBElement to a DOM element.
			Document document = getDocumentBuilder().newDocument();
			Marshaller marshaller = getJAXBContext(type).createMarshaller();
			marshaller.marshal(jaxbElement, document);
			element = document.getDocumentElement();
			
			// 3. Set the type attribute based on the value's type.
			element.setAttribute("type", type.getName());
		}
		
		return element;
	}

	@Override
	public Parameter unmarshal(Element element)
		throws Exception
	{
		Parameter parameter = null;
		
		if ( element !=  null )
		{
			// 1. Determine the values type from the type attribute.
			Class<?> type = classLoader.loadClass(element.getAttribute("type"));
			
			// 2. Unmarshal the element based on the value's type.
			DOMSource source = new DOMSource(element);
			Unmarshaller unmarshaller = getJAXBContext(type).createUnmarshaller();
			JAXBElement<?> jaxbElement = unmarshaller.unmarshal(source, type);
			
			// 3. Build the instance of Parameter
			parameter = new Parameter();
			parameter.setName(element.getLocalName());
			parameter.setValue(jaxbElement.getValue());

		}
		
		return parameter;
	}
}