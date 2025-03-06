# Mapping an Arbitrary List of Objects using JAXB's @XmlAnyElement and XmlAdapter

> Article from [blog.bdoughan.com](http://blog.bdoughan.com/2012/02/xmlanyelement-and-xmladapter.html)

The _@XmlAnyElement_ annotation enables a property to handle arbitrary XML elements, and the _XmlAdapter_ provides a way to convert an object that can not be mapped into one that can.  In this post we will combine these two mechanisms  to map a list of arbitrary objects.

This post will cover the following concepts:

1. The _@XmlAnyElement_ annotation
2. A type level _XmlAdapter_
3. Marshalling/Unmarshalling root level simple data types (i.e. _String_ and _Integer_)
4. Specifying a root element via _JAXBElement_
5. Specifying the type to be unmarshalled on the _Unmarshaller_


## XML

Below is the XML that we will use for this example.  It represents a method call and includes the name of the method and parameter values.  The key detail is that the name of the parameter is represented by the element name, and these element names are not known ahead of time.

~~~
<?xml version="1.0" encoding="UTF-8"?>
<method name="addCustomer">
    <id type="java.lang.Integer">123</id>
    <name type="java.lang.String">Jane Doe</name>
    <address type="blog.anyelement.adapted.Address">
        <street>123 A Street</street>
        <city>Any Town</city>
    </address>
</method>
~~~


## Java Model

Here is the Java model we will use for this post.

### Method

This is the root object for our domain model.  We do not know the names and types of all the elements that will correspond to the _parameters_ property, so we will annotate it with _@XmlAnyElement_.

~~~
package blog.anyelement.adapted;

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlRootElement
public class Method {

    private String name;
    private List& lt;Parameter> parameters;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAnyElement
    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

}
~~~

### Parameter

Our parameters have a name and a value.  Since we will need to adapt all instances of _Parameter_, we will specify a type level _XmlAdapter_ using the _@XmlJavaTypeAdapter_ annotation.

~~~
package blog.anyelement.adapted;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(ParameterAdapter.class)
public class Parameter {

    private String name;
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
~~~

### Address

This is an example of a domain object that may be set as a parameter value.

~~~
package blog.anyelement.adapted;

public class Address {

    private String street;
    private String city;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
~~~

### ParameterAdapter

In this _XmlAdapter_ we will convert an instance of _Parameter_ to a DOM element that can be handled by the property mapped with _@XmlAnyElement_.

#### Unmarshal Operation

1. Read the type attribute to determine the class for the value object (line 73).
2. Unmarshal the DOM element using one unmarshal methods that takes a class parameter (line 78).  This parameter tells the JAXB implementation what the target class is.  We need to do this since we have not (and could not have) associated that local root element with a class using _@XmlRootElement_ or _@XmlElementDecl_.
3. Build the instance of parameter populated the DOM element (line 82) and JAXBElement (line 83).


#### Marshal Operation

1. Build a _QName_ to represent the local root element for the instance of _Parameter_ (line 50).
2. Create the _JAXBElement_ based on the _QName_, type of the value, and the value object (line 53).
3. Marshal the _JAXBElement_ to a DOM element (line 58).
4. Set the type attribute on the DOM element based on the type of the value object (line 62).

~~~
package blog.anyelement.adapted;

import javax.xml.bind.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ParameterAdapter extends XmlAdapter<Element, Parameter> {

    private ClassLoader classLoader;
    private DocumentBuilder documentBuilder;
    private JAXBContext jaxbContext;

    public ParameterAdapter() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public ParameterAdapter(JAXBContext jaxbContext) {
        this();
        this.jaxbContext = jaxbContext;
    }

    private DocumentBuilder getDocumentBuilder() throws Exception {
        // Lazy load the DocumentBuilder as it is not used for unmarshalling.
        if (null == documentBuilder) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            documentBuilder = dbf.newDocumentBuilder();
        }
        return documentBuilder;
    }

    private JAXBContext getJAXBContext(Class<?> type) throws Exception {
        if (null == jaxbContext) {
            // A JAXBContext was not set, so create a new one based  on the type.
            return JAXBContext.newInstance(type);
        }
        return jaxbContext;
    }

    @Override
    public Element marshal(Parameter parameter) throws Exception {
        if (null == parameter) {
            return null;
        }

        // 1. Build the JAXBElement to wrap the instance of Parameter.
        QName rootElement = new QName(parameter.getName());
        Object value = parameter.getValue();
        Class<?> type = value.getClass();
        JAXBElement jaxbElement = new JAXBElement(rootElement, type, value);

        // 2.  Marshal the JAXBElement to a DOM element.
        Document document = getDocumentBuilder().newDocument();
        Marshaller marshaller = getJAXBContext(type).createMarshaller();
        marshaller.marshal(jaxbElement, document);
        Element element = document.getDocumentElement();

        // 3.  Set the type attribute based on the value's type.
        element.setAttribute("type", type.getName());
        return element;
    }

    @Override
    public Parameter unmarshal(Element element) throws Exception {
        if (null == element) {
            return null;
        }

        // 1. Determine the values type from the type attribute.
        Class<?> type = classLoader.loadClass(element.getAttribute("type"));

        // 2. Unmarshal the element based on the value's type.
        DOMSource source = new DOMSource(element);
        Unmarshaller unmarshaller = getJAXBContext(type).createUnmarshaller();
        JAXBElement jaxbElement = unmarshaller.unmarshal(source, type);

        // 3. Build the instance of Parameter
        Parameter parameter = new Parameter();
        parameter.setName(element.getLocalName());
        parameter.setValue(jaxbElement.getValue());
        return parameter;
    }

}
~~~

### Demo

The demo code below will load the XML to objects and then marshal the objects back to XML.  We will create an instance of _ParameterAdapter_ based on the _JAXBContext_ (line 11), and set it on both the _unmarshaller_ (line 14) and _marshaller_ (line 19).

~~~
package blog.anyelement.adapted;

import java.io.File;
import javax.xml.bind.*;

public class Demo {

    public static void main(String[] args) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Method.class, Parameter.class,
                Address.class);
        ParameterAdapter adapter = new ParameterAdapter(jc);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setAdapter(adapter);
        File xml = new File("src/blog/anyelement/adapted/input.xml");
        Method action = (Method) unmarshaller.unmarshal(xml);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setAdapter(adapter);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(action, System.out);
    }

}
~~~