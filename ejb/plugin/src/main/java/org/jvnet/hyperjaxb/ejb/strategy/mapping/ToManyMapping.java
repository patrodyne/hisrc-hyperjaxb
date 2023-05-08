package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.ManyToMany;
import ee.jakarta.xml.ns.persistence.orm.OneToMany;

public class ToManyMapping implements FieldOutlineMapping<Object> {

	@Override
	public Object process(Mapping context, FieldOutline fieldOutline) {

		final Object ToMany = context.getCustomizing().getToMany(fieldOutline);

		if (ToMany instanceof ManyToMany) {
			return context.getManyToManyMapping().process(context, fieldOutline);
		} else if (ToMany instanceof OneToMany) {
			return context.getOneToManyMapping().process(context, fieldOutline);
		} else {
			throw new AssertionError(
					"Either one-to-many or many-to-many mappings are expected.");
		}
	}

}
