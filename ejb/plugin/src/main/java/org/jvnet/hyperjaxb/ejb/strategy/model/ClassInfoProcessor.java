package org.jvnet.hyperjaxb.ejb.strategy.model;

import com.sun.tools.xjc.model.CClassInfo;

public interface ClassInfoProcessor<T, C> {

	public T process(C context, CClassInfo classInfo);

}
