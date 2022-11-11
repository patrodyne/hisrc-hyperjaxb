package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import org.jvnet.basicjaxb.strategy.ClassOutlineProcessor;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;

public interface ClassOutlineMapping<T> extends ClassOutlineProcessor<T, Mapping>
{
	public T process(Mapping context, ClassOutline classOutline, Options options);
}
