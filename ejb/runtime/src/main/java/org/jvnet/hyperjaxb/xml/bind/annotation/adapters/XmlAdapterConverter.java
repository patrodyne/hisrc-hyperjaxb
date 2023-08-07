package org.jvnet.hyperjaxb.xml.bind.annotation.adapters;

import static java.util.Objects.requireNonNull;

import org.jvnet.hyperjaxb.item.Converter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class XmlAdapterConverter<I, O> implements Converter<I, O> {

	private final XmlAdapter<O, I> adapter;

	public XmlAdapterConverter(XmlAdapter<O, I> adapter) {
		requireNonNull(adapter);
		this.adapter = adapter;
	}

	@Override
	public I direct(O outer) {
		if (outer == null) {
			return null;
		} else {
			try {
				return adapter.unmarshal(outer);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public O inverse(I inner) {
		if (inner == null) {
			return null;
		} else {
			try {
				return adapter.marshal(inner);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

}
