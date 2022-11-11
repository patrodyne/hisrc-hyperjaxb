package org.jvnet.hyperjaxb.ejb.strategy.processor;

import java.util.Collection;

import org.jvnet.basicjaxb.util.OutlineUtils;

import ee.jakarta.xml.ns.persistence.Persistence.PersistenceUnit;
import com.sun.tools.xjc.outline.ClassOutline;

public class ClassPersistenceUnitFactory implements PersistenceUnitFactory{

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
