package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Embedded;
import ee.jakarta.xml.ns.persistence.orm.ManyToOne;
import ee.jakarta.xml.ns.persistence.orm.OneToOne;

public class ToOneMapping implements FieldOutlineMapping<Object> {

	@Override
	public Object process(Mapping context, FieldOutline fieldOutline) {

		final Object toOne = context.getCustomizing().getToOne(fieldOutline);

		if (toOne instanceof ManyToOne) {
			return context.getManyToOneMapping().process(context, fieldOutline);
		} else if (toOne instanceof OneToOne) {
			return context.getOneToOneMapping().process(context, fieldOutline);
		} else if (toOne instanceof Embedded) {
			return context.getEmbeddedMapping().process(context, fieldOutline);
		} else {
			throw new AssertionError(
					"Either many-to-one or one-to-one mappings are expected.");
		}
	}

}
