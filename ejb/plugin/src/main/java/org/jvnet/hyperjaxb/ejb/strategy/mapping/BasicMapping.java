package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.Column;

/**
 * Map a {@link FieldOutline} to a JPA {@code Basic} annotation.
 * A Basic annotation is applied to a property or instance variable
 * whose type is any one of the following: {@code String}, Java primitive types,
 * {@code java.time} types, Java {@code enum} type, other serializable types.
 *
 * See {@code org/jvnet/hyperjaxb/ejb/strategy/customizing/impl/DefaultCustomizations.xml}
 */
public class BasicMapping implements FieldOutlineMapping<Basic>
{
	@Override
	public Basic process(Mapping context, FieldOutline fieldOutline)
	{
		final Basic basic = context.getCustomizing().getBasic(fieldOutline);
		createBasic$Name(context, fieldOutline, basic);
		createBasic$Column(context, fieldOutline, basic);

		if ( basic.getLob() == null && basic.getTemporal() == null && basic.getEnumerated() == null )
		{
			if ( context.getAttributeMapping().isTemporal(context, fieldOutline) )
				basic.setTemporal(context.getAttributeMapping().createTemporalType(context, fieldOutline));
			else if ( context.getAttributeMapping().isEnumerated(context, fieldOutline) )
				basic.setEnumerated(context.getAttributeMapping().createEnumerated(context, fieldOutline));
			else if ( context.getAttributeMapping().isLob(context, fieldOutline) )
				basic.setLob(context.getAttributeMapping().createLob(context, fieldOutline));
		}

		// The {@code jakarta.persistence.Temporal} annotation must
		// be specified for persistent fields or properties of type
		// {@code java.util.Date} and {@code java.util.Calendar}.
		// It may only be specified for fields or properties of
		// those types. The newer {@code java.time.* } types do not
		// use the {@code jakarta.persistence.Temporal} annotation.
		if ( basic.getTemporal() != null )
		{
			if ( ! context.getAttributeMapping().isTemporal(context, fieldOutline) )
				basic.setTemporal(null);
		}

		return basic;
	}

	public void createBasic$Name(Mapping context, FieldOutline fieldOutline, final Basic basic)
	{
		basic.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

	public void createBasic$Column(Mapping context, FieldOutline fieldOutline, final Basic basic)
	{
		if ( basic.getColumn() == null )
			basic.setColumn(new Column());
		basic.setColumn(context.getAttributeMapping().createColumn(context, fieldOutline, basic.getColumn()));
	}
}
