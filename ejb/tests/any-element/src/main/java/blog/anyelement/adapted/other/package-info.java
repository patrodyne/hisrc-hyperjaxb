@XmlSchema(
	namespace = "urn:blog:anyelement:adapted:other",
	elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    xmlns = { 
        @XmlNs(prefix = "other", namespaceURI = "urn:blog:anyelement:adapted:other")
    }
)
package blog.anyelement.adapted.other;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;
