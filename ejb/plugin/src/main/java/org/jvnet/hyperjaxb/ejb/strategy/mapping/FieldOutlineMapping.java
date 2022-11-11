package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import org.jvnet.basicjaxb.strategy.FieldOutlineProcessor;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.FieldOutline;

public interface FieldOutlineMapping<T> extends
		FieldOutlineProcessor<T, Mapping> {

	public T process(Mapping context, FieldOutline fieldOutline, Options options);

}
