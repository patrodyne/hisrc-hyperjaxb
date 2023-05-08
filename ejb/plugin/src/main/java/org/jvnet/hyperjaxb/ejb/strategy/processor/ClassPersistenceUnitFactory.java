package org.jvnet.hyperjaxb.ejb.strategy.processor;

import java.util.Collection;

import org.jvnet.basicjaxb.util.OutlineUtils;

import com.sun.tools.xjc.outline.ClassOutline;

import ee.jakarta.xml.ns.persistence.Persistence.PersistenceUnit;

public class ClassPersistenceUnitFactory implements PersistenceUnitFactory{

	@Override
	public PersistenceUnit createPersistenceUnit(
			final Collection<ClassOutline> includedClasses) {
		final PersistenceUnit persistenceUnit = new PersistenceUnit();
		for (final ClassOutline classOutline : includedClasses) {
			persistenceUnit.getClazz().add(
					OutlineUtils.getClassName(classOutline));
		}
		return persistenceUnit;

	}
}
