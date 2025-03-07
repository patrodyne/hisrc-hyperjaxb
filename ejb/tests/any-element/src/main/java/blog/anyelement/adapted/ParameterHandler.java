package blog.anyelement.adapted;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import blog.anyelement.adapted.other.Address;
import blog.anyelement.adapted.other.Method;
import blog.anyelement.adapted.other.Parameter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.DomHandler;
import jakarta.xml.bind.annotation.W3CDomHandler;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;

/**
 * An implementation of {@link DomHandler} to trace handler events:
 * create a new {@link DOMResult} holder, access the {@link Element}
 * instance or marshal the instance into a {@link Source} to regenerate
 * the XML sub-tree.
 */
public class ParameterHandler implements DomHandler<Object,DOMResult>
{
	// Represents the logger for this class.
	private static Logger logger = LoggerFactory.getLogger(ParameterHandler.class);
	public static Logger getLogger() { return logger; }
	
	private static final W3CDomHandler W3C = new W3CDomHandler();
	
	/** Default constructor */
	public ParameterHandler()
	{
		super();
	}
	
	/** Construct with a JAXBContext. */
	public ParameterHandler(JAXBContext context)
	{
		super();
		setContext(context);
	}

	private JAXBContext context;
	public JAXBContext getContext()
	{
		if ( context == null )
		{
			try
			{
				setContext(JAXBContext.newInstance(Method.class, Parameter.class, Address.class));
			}
			catch (JAXBException ex)
			{
				getLogger().error("Cannot instantiate a JAXB context", ex);
			}
		}
		return context;
	}
	public void setContext(JAXBContext context)
	{
		this.context = context;
	}
	
	private ParameterAdapter parameterAdapter;
	public ParameterAdapter getParameterAdapter()
	{
		if ( parameterAdapter == null )
			setParameterAdapter(new ParameterAdapter(getContext()));
		return parameterAdapter;
	}
	public void setParameterAdapter(ParameterAdapter parameterAdapter)
	{
		this.parameterAdapter = parameterAdapter;
	}
	
	private ValidationEventHandler validationEventHandler;
	public ValidationEventHandler getValidationEventHandler()
	{
		if ( validationEventHandler == null )
			setValidationEventHandler(new DefaultValidationEventHandler());
		return validationEventHandler;
	}
	public void setValidationEventHandler(ValidationEventHandler validationEventHandler)
	{
		this.validationEventHandler = validationEventHandler;
	}
	
	/**
	 * Create a new {@link DOMResult} instance for the XML Binding provider
	 * to transform a portion of the XML into the instance.
	 * 
	 * @return A holder for a transformation result tree in the form of a DOM tree.
	 */
	public DOMResult createUnmarshaller()
	{
		return createUnmarshaller(getValidationEventHandler());
	}

	/**
	 * Create a new {@link DOMResult} instance for the XML Binding provider
	 * to transform a portion of the XML into the instance.
	 * 
	 * @param errorHandler An event handler interface for validation errors.
	 * 
	 * @return A holder for a transformation result tree in the form of a DOM tree.
	 */
	@Override
	public DOMResult createUnmarshaller(ValidationEventHandler errorHandler)
	{
		getLogger().trace("createUnmarshaller: {}", errorHandler);
		DOMResult domResult = W3C.createUnmarshaller(errorHandler);
		return domResult;
	}
	
	/**
	 * Once a portion of the XML is transformed to the {@link DOMResult} instance,
	 * this method is called by the XML Binding provider to obtain the unmarshalled
	 * {@link Element} representation.
	 */
	@Override
	public Object getElement(DOMResult domResult)
	{
		Object parameter = null;
		getLogger().trace("unmarshal: {}", domResult);
		Element element = W3C.getElement(domResult);
        try
		{
			parameter = getParameterAdapter().unmarshal(element);
		}
		catch (Exception ex)
		{
			getLogger().warn("getElement: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
			parameter = element;
		}
        return parameter;
	}
	
	/**
	 * This method is called when a the XML Binding provider needs to marshal
	 * the given {@link Object} instance to an XML representation. Typically,
	 * the {@link Source} is a {@link DOMSource} that acts as a holder for a
	 * transformation source Document Object Model (DOM) tree.
	 */
	@Override
	public Source marshal(Object value, ValidationEventHandler errorHandler)
	{
		getLogger().trace("marshal: {}", value);
		try
		{
			Element element = getParameterAdapter().marshal((Parameter) value);
			return W3C.marshal(element, errorHandler);
		}
		catch (Exception ex)
		{
			getLogger().warn("marshal: [{}] {}", ex.getClass().getSimpleName(), ex.getMessage());
			return null;
		}
	}
}

