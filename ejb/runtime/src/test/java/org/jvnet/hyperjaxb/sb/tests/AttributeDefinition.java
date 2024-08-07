package org.jvnet.hyperjaxb.sb.tests;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Attribute")
public class AttributeDefinition<T> {

	T value;

	@XmlElement(name = "Value")
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
}