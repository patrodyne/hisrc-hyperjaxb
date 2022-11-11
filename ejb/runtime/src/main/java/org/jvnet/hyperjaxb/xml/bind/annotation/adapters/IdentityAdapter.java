package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class IdentityAdapter extends XmlAdapter<Object, Object> {

	@Override
	public Object marshal(Object v) throws Exception {
		return v;
	}

	@Override
	public Object unmarshal(Object v) throws Exception {
		return v;
	}

}
