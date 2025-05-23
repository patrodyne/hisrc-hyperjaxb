package org.jvnet.hyperjaxb.util.tests;

import java.io.Serializable;

import org.jvnet.hyperjaxb.item.Item;

public class StringItem implements Item<String>, Serializable {

	private static final long serialVersionUID = 1L;

	private String value;

	@Override
	public void setItem(String value) {
		this.value = value;
	}

	@Override
	public String getItem() {
		return this.value;
	}

}
