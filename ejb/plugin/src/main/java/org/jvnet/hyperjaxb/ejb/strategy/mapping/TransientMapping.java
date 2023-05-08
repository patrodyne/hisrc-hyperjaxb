package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Transient;

public class TransientMapping implements FieldOutlineMapping<Transient> {

	@Override
	public Transient process(Mapping context, FieldOutline fieldOutline) {

		// TODO
		final Transient _transient = new Transient();
		// context.getCustomizations().getTransient(fieldOutline);

		createTransient(context, fieldOutline, _transient);
		return _transient;
	}

	public void createTransient(Mapping context, FieldOutline fieldOutline,
			Transient _transient) {
		createTransient$Name(context, fieldOutline, _transient);
	}

	public void createTransient$Name(Mapping context,
			FieldOutline fieldOutline, final Transient _transient) {
		_transient.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

}
