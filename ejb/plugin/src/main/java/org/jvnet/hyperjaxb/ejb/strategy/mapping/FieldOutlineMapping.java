package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import org.jvnet.basicjaxb.strategy.FieldOutlineProcessor;

import com.sun.tools.xjc.outline.FieldOutline;

public interface FieldOutlineMapping<T> extends FieldOutlineProcessor<T, Mapping>
{
	@Override
	public T process(Mapping context, FieldOutline fieldOutline);
}
