package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.Column;
import ee.jakarta.xml.ns.persistence.orm.Version;

public class VersionMapping implements FieldOutlineMapping<Version>
{
	@Override
	public Version process(Mapping context, FieldOutline fieldOutline)
	{
		final Version version = context.getCustomizing().getVersion(fieldOutline);
		createVersion$Name(context, fieldOutline, version);
		createVersion$Column(context, fieldOutline, version);
		createVersion$Temporal(context, fieldOutline, version);
		return version;
	}

	public void createVersion$Name(Mapping context, FieldOutline fieldOutline, final Version version)
	{
		version.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

	public void createVersion$Column(Mapping context, FieldOutline fieldOutline, final Version version)
	{
		if (version.getColumn() == null)
			version.setColumn(new Column());
		// Create column from context, outline and column.
		version.setColumn(context.getAttributeMapping().createColumn(context, fieldOutline, version.getColumn()));
	}

	public void createVersion$Temporal(Mapping context, FieldOutline fieldOutline, Version version)
	{
		if ( (version.getTemporal() == null) && context.getAttributeMapping().isTemporal(context, fieldOutline) )
			version.setTemporal(context.getAttributeMapping().createTemporalType(context, fieldOutline));
	}
}
