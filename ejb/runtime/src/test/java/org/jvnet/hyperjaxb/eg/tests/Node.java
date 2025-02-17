package org.jvnet.hyperjaxb.eg.tests;

import jakarta.xml.bind.annotation.XmlAttribute;

public abstract class Node 
{
	private String name;

	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
