# Modifications

This folder contains the original **Uniprot** XML schema and XML sample files. The actual build files have been modified, slightly, to achieve a successful build.

## Using generateIsSetMethod="true"

Included in this project are customized XJB, XSD and XML files to demonstrate how the _Uniprot_ schema can be implemented with JAXB `isSet` methods.

- **bindings.xjb** (setns jaxb=http://java.sun.com/xml/ns/jaxb)
    - `/jaxb:bindings/jaxb:bindings[1]/jaxb:globalBindings`
        - Added attribute: generateIsSetMethod="true"
        - Added attribute: optionalProperty="wrapper"
- **uniprot2008_rev1.xsd** (setns xs=http://www.w3.org/2001/XMLSchema)
    - `/xs:schema/xs:complexType[@name='commentType']/xs:attribute[@name='mass']`
        - Added attribute: default="0.0"
    - `/xs:schema/xs:complexType[@name='sequenceType']//xs:attribute[@name='precursor']`
        - Added attribute: default="false"
- **P01189.xml** (setns uni=http://uniprot.org/uniprot)
    - `/uni:uniprot/uni:entry[@dataset='Swiss-Prot']/uni:comment`
        - Added attribute: mass="0.0"
    - `/uni:uniprot/uni:entry[@dataset='Swiss-Prot']//uni:*[@position]`
        - Added attribute: status="certain"

## Using generateIsSetMethod="false"

Not included, this is how to customized XJB and XSD files to demonstrate how the _Uniprot_ schema can be implemented without JAXB `isSet` methods. No change to the XML files. The default declarations are removed because they cause the round trip test to fail.

- **bindings.xjb** (setns jaxb=http://java.sun.com/xml/ns/jaxb)
    - `/jaxb:bindings/jaxb:bindings[1]/jaxb:globalBindings`
        - Added attribute: generateIsSetMethod="false"
- **uniprot2008_rev1.xsd** (setns xs=http://www.w3.org/2001/XMLSchema)
    - `/xs:schema/xs:complexType[@name='commentType']//xs:element[@name='organismsDiffer']`
        - Remove attribute: default="false"
    - `/xs:schema/xs:complexType[@name='positionType']/xs:attribute[@name='status']`
        - Remove attribute: default="certain"
    - `/xs:schema/xs:complexType[@name='statusType']//xs:attribute[@name='status']`
        - Remove attribute: default="known"

> **Recommendations:** Avoid `generateIsSetMethod="true"` because it can produce attribute accessor(s) with _Simple Value_ return types and _Complex Type_ backing fields. This can lead to _Null Pointer Exceptions_ when auto-boxing a `null` backing field into its return type. This is a documented JAXB issue. Avoid using XML Schema _default value_ because they does not travel well across round trip tests.

# Set Value & Default Value

JAXB generated Java classes may include default values on attributes and elements. The default value for an *attribute* is implemented as an "if...then" block in the accessor. The block returns the default value when the backing field is null; otherwise, it returns the backing field value. The default value for an *element* is implemented as a parameter of the `@XmlElement` annotation.

**Definitions**

*Set Value*
: A property is said to have a _set value_ if that value was assigned to it during unmarshalling or by invoking its mutation method. The value of a property is its _set value_, if defined; otherwise, it is the property’s schema specified _default value_, if any; otherwise, it is the default initial value for the property’s base type - as it would be assigned for an uninitialized field within a Java class.[^1]

*Default Value*
: A schema component can have a schema specified default value which is used when property’s value is not set and not nil.[^1]

*isSet Modifier*
: An optional modifier augments a modifiable property to enable the manipulation of the property’s value as a _set value_ or a _defaulted value_.[^2]

*unset Method*
: Marks the property as having no _set value_. A subsequent call to `getId` method returns the schema-specified default if it existed; otherwise, it returns the Java default initial value for `Type`.[^2]

**Note:** The alternative of a _set value_ is the _default value_ rather than the _null value_. In other words, _setness_ is different then _notnullness_. A property can be "not set" and yet return a _default value_ that is not null!

## Attribute Accessor Return Type

Normally, JAXB generates accessors and backing fields with nullable Object types like `Boolean` and `Integer`. Setting the global binding `generateIsSetMethod="true"` causes *attribute* generated accessors to return primitive types! The backing fields keep their nullable Object types. Unfortunately, this can lean to Null Pointer Exceptions (NPEs) when the underling field is null and the accessor attempts to auto-box it to a primitive return type.

Also, if the XML schema specifies a default value on an *attribute* then JAXB will flip the return type to the appropriate primitive: `boolean`, `int`, etc. But, the accessor will have an `if...then` block to return the default value when the backing field is null; thus, avoiding the NPE.

> In both cases, the `String` type is not affected by this behavior; its return type is a nullable `String`.

> The _element_ accessor return type always matches the backing field type, avoiding the NPE problem.

## Default Value

There is a problem relating to the declaration of _default values_ in an XML schema. It may appear trivial but the use of _default values_ creates several issues when representing and/or transferring data. Since the XML schema _owns_ the _default value_, the value is not known outside the XML schema context. Often, data moves into other contexts (SQL, JSON, CSV, etc.) where the _default value_ is not present and adding a default value on the other contexts is not practical or may not be supported. Let's look at a concrete example.

### XML Schema

Consider this XML schema, it defines a `Pet` type with three values, a `Name` element, an optional `Legs` element  and an optional `Trained` attribute. The `Trained` attribute indicates when the pet is house-broken and it defaults to `true`.

```
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
  targetNamespace="http://example.org/animal"
>
  <xs:element name="Pet">
    <xs:complexType>
      <xs:sequence>
        <xs:element name="Name" type="xs:string" />
        <xs:element name="Legs" type="xs:int" minOccurs="0" default="4"/>
      </xs:sequence>
      <xs:attribute name="Trained" type="xs:boolean" use="optional" default="true"/>
    </xs:complexType>
  </xs:element>
</xs:schema>
```

#### Attribute and Element Defaulting[^3]

The element default value is derived from the element declaration’s default attribute. Unlike attribute defaulting, an element only defaults when there is an empty element tag in the xml instance. The unmarshaller sets the property to this default value when it encounters an empty element tag. The marshaller may output an empty element tag whenever the element’s `@XmlValue` property value is the same as its defaulted value but the implementation may choose to always output the _set value_.

> JAXB has the `@XmlValue` type to implement simple types. Simple types are types that consist only of text, without any elements.

>The JAXB annotation `@XmlElement` with parameter `defaultValue` sets default value for element. However, the `@XmlAttribute` annotation does not have an equivalent _default_ option! Instead, XJC will generate an "if clause" to check for a null value and if present, the clause will return the default value.

**Remember:** Default attribute values apply when attributes are missing, and default element values apply when elements are empty.

### XML Instances

Next consider these two XML instances of our `Pet` type. They represent the same house-broken pet but differ in their representation. The first instance allows the schema to provide the default value for the `Trained` attribute and `Legs` element; ...

#### Instance `#1`
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<pet:Pet xmlns:pet="http://example.org/animal">
    <Name>Fido</Name>
    <Legs></Legs>
</pet:Pet>
```

... but, the second instance explicitly declares them.

#### Instance `#2`
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<pet:Pet xmlns:pet="http://example.org/animal" Trained="true">
    <Name>Fido</Name>
    <Legs>4</Legs>
</pet:Pet>
```

### Question: Does instance `#1` equal instance `#2`?

Temporarily, I'll argue that the two instances exhibit data equality. The fist instance explicitly provides a value for the `Trained` attribute. The second instance relies on the JAXB class to supply the value at unmarshall time. However, because data can travel, we need to consider what happens beyond this context and into other contexts.

Later, I'll reject the argument that these two instances are "equal" and conclude that these two instances are not equal because they differ by their `isSet` values. In other words, objects with _defaulted values_ differ from objects with _set values_ even when elsewise they are the same.

### JAXB Unmarshall and Marshall

To process XML instances, we'll use JAXB to unmarshall the instances into Java objects using a `Pet` class, see below. This class is a POJO with JAXB annotations. It declares the field, accessor, mutator and isSet/unset methods for the `Name` element and the `Trained` attribute. JAXB is configured the use the fields to access/mutate the data. Here are the focal points:

- The `Name` field is required, per schema; thus, it does not have an `unset` method. JAXB allows this to be nulled internally by using the setter method. Because there is no XML Schema default defined for this element, the concepts of "is set" and "not null" are the same.
- The `Legs` field is optional, per schema; its `@XmlElement` provides a _default value_ which is set on unmarshalling. 
- The `Trained` accessor returns a primitive because the property is an XML attribute and attributes are always Simple Types[^4]. It declares an `unsetTrained()` method with internal logic to unset the attribute. The internal logic allows the field to be nullable; although, the wrappers must remain primitive.
- The `Trained` accessor contains subtle but critical logic. When the `trained` field is not set, the accessor returns the _default value_, in the case, the default is the value "`true`". When the field value is set then the field value is returned.
- After the `unsetTrained()` method is invoked, the `isTrained()` accessor will return its XML schema defined _default value_. This is a JAXB requirement.[^2]
- The `Trained` accessor and its field, taken together, enforce the concept that "is set" and "not defaulted" are the same. Contrapositively, the concepts of "is not set" and "is defaulted" are the same here. Then also, "is not set" does not equate to "is null"!

#### JAXB/XJC generated Pet class

```
package org.example.animal;

import java.io.Serializable;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name", "legs" })
@XmlRootElement(name = "Pet")
public class Pet implements Serializable
{
    private final static long serialVersionUID = 20211201L;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Legs", defaultValue = "4")
    protected Integer legs;
    @XmlAttribute(name = "Trained")
    protected Boolean trained;

    // Element: Name
    public String getName() { return name; }
    public void setName(String value) { this.name = value; }
    @Transient
    public boolean isSetName() {
        return (this.name!= null);
    }

    // Element: Legs
    public Integer getLegs() { return legs; }
    public void setLegs(Integer value) { this.legs = value; }
    @Transient
    public boolean isSetLegs() {
        return (this.legs!= null);
    }

    // Attribute: Trained
    public boolean isTrained() {
        if (trained == null) {
            return true;
        } else {
            return trained;
        }
    }
    public void setTrained(boolean value) {
        this.trained = value;
    }
    @Transient
    public boolean isSetTrained() {
        return (this.trained!= null);
    }
    public void unsetTrained() {
        this.trained = null;
    }
}
```

#### Unmarshall

When we use JAXB to unmarshall instances `#1` & `#2` (above) into `Pet` objects, we get:

##### Pet Name Element
| Instance `#1`                | Instance `#2`                |
| ---------------------------- | ---------------------------- |
| pet1.name is "Fido"          | pet2.name is "Fido"          |
| pet1.getName() is "Fido"     | pet2.getName() is "Fido"     |
| pet1.isSetName() is true     | pet2.isSetName() is true     |

##### Pet Legs Element
| Instance `#1`                | Instance `#2`                |
| ---------------------------- | ---------------------------- |
| pet1.legs is 4               | pet2.legs is 4               |
| pet1.getLegs() is 4          | pet2.getLegs() is 4          |
| pet1.isSetLegs() is true     | pet2.isSetLegs() is true     |

##### Pet Trained Attribute
| Instance `#1`                | Instance `#2`                |
| ---------------------------- | ---------------------------- |
| pet1.trained is null         | pet2.trained is true         |
| pet1.isTrained() is true     | pet2.isTrained() is true     |
| pet1.isSetTrained() is false | pet2.isSetTrained() is false |

The difference is that instance `#1` populates the `trained` field with the default attribute and element values but instance `#2` does sets the values explicitly. Both objects return the same _accessor_ values, one _accessor_ using the implicit field value and the other using the explicit default value.

> **Note:** JAXB/XJC implements attributes and elements differently. When  an attribute is unset in the Java object, its _accessor_ returns the XML schema default value using its `if...then` block. When an element is nulled using its setter, its _accessor_ returns null!

#### Marshall

Next, let's look at the XML instance produced using JAXB to marshall the Java objects.

As shown below, instance `#1` omits the `Trained` attribute because the Java object was unmarshalled using its accessor's `if...then` block and its backing field was not set. The `Legs` element is output to the XML because JAXB used the `@XmlElement` to declare and set its backing field.

##### Instance `#1`
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:Pet xmlns:ns2="http://example.org/animal">
    <Name>Fido</Name>
    <Legs>4</Legs>
</ns2:Pet>
```

Instance `#2` includes the `Trained` attribute value and `Legs` element because they were set during unmarshalling.

##### Instance `#2`
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:Pet xmlns:ns2="http://example.org/animal" Trained="true">
    <Name>Fido</Name>
    <Legs>4</Legs>
</ns2:Pet>
```

> **Note:** A custom implementation can be used to omit `set values` when they match the `default value` but this is not the standard implementation.

### Round Trip

A goal of JAXB is to support "round trip" where an XML instance can be unmarshalled and marshalled into the same XML representation. This provides verification that JAXB is working, as intended.

In the examples above, the marshalled instance `#1` differs slightly from its original representation because the default for `Legs' element is set explicitly during unmarshalling. Instance `#2` fairs better because the original XML does not rely on default values.

### Question: Does instance `#1` equal instance `#2`?

This is the question asked earlier but this time I'll answer that the instances are not equal. They are not equal because the `isSetTraining` operator returns `false` for instance `#1` and `true` for instance `#2`. This is the difference between an object with a `default value and another object with a `set value`!

### Equality/Hash Set Value Logic

To provide the _set value_ logic, the `equal(Object)` and `hashCode` methods are implemented by checking the `isSet` state before checking the value state.

#### Equals

The equals logic returns true when the left/right objects match on _set value_ and on _value_.

```
    @Override
    public boolean equals(Object object) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final Pet that = ((Pet) object);
        {
            String leftName;
            leftName = this.getName();
            String rightName;
            rightName = that.getName();
            if (this.isSetName()) {
                if (that.isSetName()) {
                    if (!leftName.equals(rightName)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.isSetName()) {
                    return false;
                }
            }
        }
        {
            Integer leftLegs;
            leftLegs = this.getLegs();
            Integer rightLegs;
            rightLegs = that.getLegs();
            if (this.isSetLegs()) {
                if (that.isSetLegs()) {
                    if (!leftLegs.equals(rightLegs)) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.isSetLegs()) {
                    return false;
                }
            }
        }
        {
            boolean leftTrained;
            leftTrained = (this.isSetTrained()?this.isTrained():true);
            boolean rightTrained;
            rightTrained = (that.isSetTrained()?that.isTrained():true);
            if (this.isSetTrained()) {
                if (that.isSetTrained()) {
                    if (leftTrained!= rightTrained) {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                if (that.isSetTrained()) {
                    return false;
                }
            }
        }
        return true;
    }
```

#### Hash Code

The hash code logic implements different hash values for the `Trained` attribute depending of its `isSet` state.

```
    @Override
    public int hashCode() {
        int currentHashCode = 1;
        {
            currentHashCode = (currentHashCode* 31);
            String theName;
            theName = this.getName();
            if (this.isSetName()) {
                currentHashCode += theName.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            Integer theLegs;
            theLegs = this.getLegs();
            if (this.isSetLegs()) {
                currentHashCode += theLegs.hashCode();
            }
        }
        {
            currentHashCode = (currentHashCode* 31);
            boolean theTrained;
            theTrained = (this.isSetTrained()?this.isTrained():true);
            if (this.isSetTrained()) {
                currentHashCode += (theTrained? 1231 : 1237);
            }
        }
        return currentHashCode;
    }
```

<!-- Footnotes -->

[^1]: Specification: Jakarta XML Binding, Version: 3.0, §5.5.
[^2]: Specification: Jakarta XML Binding, Version: 3.0, §5.5.4.
[^3]: Specification: Jakarta XML Binding, Version: 3.0, §6.7.4
[^4]: Specification: Jakarta XML Binding, Version: 3.0, §6.8

