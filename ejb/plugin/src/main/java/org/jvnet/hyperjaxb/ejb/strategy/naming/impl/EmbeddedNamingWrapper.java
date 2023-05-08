package org.jvnet.hyperjaxb.ejb.strategy.naming.impl;

import org.jvnet.hyperjaxb.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb.ejb.strategy.naming.ReservedNames;
import org.jvnet.hyperjaxb.jpa.Embedded;

import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

/**
 * Wrap a Naming instance and a FieldOutline instance to provide a column name prefix, etc..
 * 
 * The JPA annotations @Embedded and @Embeddable are used to define common properties
 * for multiple entities, without a separate table for the embedded type.
 * 
 */
public class EmbeddedNamingWrapper implements Naming
{
	private final Naming naming;
	private final FieldOutline parentFieldOutline;

	@Override
	public int getMaxIdentifierLength() { return naming.getMaxIdentifierLength(); }
	@Override
	public void setMaxIdentifierLength(int maxIdentifierLength) { naming.setMaxIdentifierLength(maxIdentifierLength); }

	public EmbeddedNamingWrapper(Naming naming, FieldOutline fieldOutline)
	{
		super();
		this.naming = naming;
		this.parentFieldOutline = fieldOutline;
	}

	@Override
	public ReservedNames getReservedNames() { return naming.getReservedNames(); }
	@Override
	public Ignoring getIgnoring() { return naming.getIgnoring(); }

	@Override
	public Naming createEmbeddedNaming(Mapping context, FieldOutline fieldOutline)
	{
		return new EmbeddedNamingWrapper(this, fieldOutline);
	}
	
	@Override
	public String getPropertyName(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getPropertyName(context, fieldOutline);
	}

	@Override
	public String getEntityClass(Mapping context, Outline outline, NType type)
	{
		return naming.getEntityClass(context, outline, type);
	}

	@Override
	public String getEntityName(Mapping context, Outline outline, NType type)
	{
		return naming.getEntityName(context, outline, type);
	}

	@Override
	public String getPersistenceUnitName(Mapping context, Outline outline)
	{
		return naming.getPersistenceUnitName(context, outline);
	}

	@Override
	public String getEntityTable$Name(Mapping context, ClassOutline classOutline)
	{
		return naming.getEntityTable$Name(context, classOutline);
	}

	@Override
	public String getEntityTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getEntityTable$Name(context, fieldOutline);
	}

	@Override
	public String getColumn$Name$Prefix(Mapping context)
	{
		final String prefix;
		final Embedded embedded = context.getCustomizing().getEmbedded(parentFieldOutline);
		if (embedded != null && embedded.getColumnNamePrefix() != null)
			prefix = embedded.getColumnNamePrefix();
		else
			prefix = naming.getColumn$Name$Prefix(context) + parentFieldOutline.getPropertyInfo().getName(true) + "_";
		return prefix;
	}

	@Override
	public String getColumn$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getColumn$Name(context, fieldOutline);
	}

	@Override
	public String getJoinTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getJoinTable$Name(context, fieldOutline);
	}

	@Override
	public String getJoinColumn$Name(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline)
	{
		return naming.getJoinColumn$Name(context, fieldOutline, idFieldOutline);
	}

	@Override
	public String getJoinTable$JoinColumn$Name(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline)
	{
		return naming.getJoinTable$JoinColumn$Name(context, fieldOutline, idFieldOutline);
	}

	@Override
	public String getJoinTable$InverseJoinColumn$Name(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline)
	{
		return naming.getJoinTable$InverseJoinColumn$Name(context, fieldOutline, idFieldOutline);
	}

	@Override
	public String getOrderColumn$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getOrderColumn$Name(context, fieldOutline);
	}

	@Override
	public String getName(Mapping context, String draftName)
	{
		return naming.getName(context, draftName);
	}

	@Override
	public String getElementCollection$CollectionTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getElementCollection$CollectionTable$Name(context, fieldOutline);
	}

	@Override
	public String getElementCollection$CollectionTable$JoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline)
	{
		return naming.getElementCollection$CollectionTable$JoinColumn$Name(context, fieldOutline, idFieldOutline);
	}

	@Override
	public String getElementCollection$Column$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getElementCollection$Column$Name(context, fieldOutline);
	}

	@Override
	public String getElementCollection$OrderColumn$Name(Mapping context, FieldOutline fieldOutline)
	{
		return naming.getElementCollection$OrderColumn$Name(context, fieldOutline);
	}
}
