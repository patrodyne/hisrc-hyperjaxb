package org.jvnet.hyperjaxb.transform;

import java.util.List;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.jvnet.hyperjaxb.item.ConvertedList;
import org.jvnet.hyperjaxb.item.Converter;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XmlAdapterUtils;

public class TransformUtils {

	public static <T> boolean shouldBeWrapped(List<T> inner) {
		if (inner == null || !(inner instanceof ConvertedList)) {
			return true;
		} else {
			return false;
		}
	}

	public static <I, O> List<I> wrap(List<I> inner, List<O> outer,
			Class<? extends XmlAdapter<I, O>> xmlAdapterClass) {
		if (inner == null || !(inner instanceof ConvertedList)) {
			Converter<O, I> converter = XmlAdapterUtils
					.getConverter(xmlAdapterClass);
			final List<I> newInner = new ConvertedList<I, O>(outer, converter);
			if (inner != null) {
				newInner.addAll(inner);
			}
			return newInner;
		} else {
			return inner;
		}
	}
}
