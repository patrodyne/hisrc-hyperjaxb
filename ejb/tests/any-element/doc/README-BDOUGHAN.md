Mapping an Arbitrary List of Objects using JAXB's @XmlAnyElement and XmlAdapter
===============================================================================

This is the complete source code for the following blog post:

* http://blog.bdoughan.com/2012/02/xmlanyelement-and-xmladapter.html

Summary
-------

The @XmlAnyElement annotation enables a property to handle arbitrary XML elements, and the XmlAdapter provides a way to convert an object that can not be mapped into one that can.  In this post we will combine these two mechanisms  to map a list of arbitrary objects.

We will map:

    public class Method {

        private String name;
        private List<Parameter> parameters;
    

And:

    public class Parameter {

        private String name;
        private Object value;

    }
    
To the following XML:

    <?xml version="1.0" encoding="UTF-8"?>
    <method name="addCustomer">
        <id type="java.lang.Integer">123</id>
        <name type="java.lang.String">Jane Doe</name>
        <address type="blog.anyelement.adapted.other.Address">
            <street>123 A Street</street>
            <city>Any Town</city>
        </address>
    </method>

Compile the Example
-------------------

You can compile the example code using the following command.  Maven will automatically fetch the required dependencies.

     mvn compile

Run the Example
---------------

You can run the example using the following command.

    mvn exec:java