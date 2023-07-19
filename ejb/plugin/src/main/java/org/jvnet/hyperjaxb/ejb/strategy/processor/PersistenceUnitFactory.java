package org.jvnet.hyperjaxb.ejb.strategy.processor;

import java.util.Collection;

import com.sun.tools.xjc.outline.ClassOutline;

import ee.jakarta.xml.ns.persistence.Persistence.PersistenceUnit;

public interface PersistenceUnitFactory
{
	public PersistenceUnit createPersistenceUnit(Collection<ClassOutline> includedClasses);
}
