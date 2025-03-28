package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.ejb.Constants.TODO_LOG_LEVEL;
import static org.jvnet.hyperjaxb.jpa.Customizations.EMBEDDED_ID_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.ID_ELEMENT_NAME;
import static org.jvnet.hyperjaxb.jpa.Customizations.IGNORED_ELEMENT_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jvnet.hyperjaxb.xjc.model.CTypeInfoUtils;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.orm.AssociationOverride;
import ee.jakarta.xml.ns.persistence.orm.ElementCollection;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import ee.jakarta.xml.ns.persistence.orm.Embedded;
import ee.jakarta.xml.ns.persistence.orm.JoinColumn;
import ee.jakarta.xml.ns.persistence.orm.JoinTable;
import ee.jakarta.xml.ns.persistence.orm.ManyToMany;
import ee.jakarta.xml.ns.persistence.orm.ManyToOne;
import ee.jakarta.xml.ns.persistence.orm.OneToMany;
import ee.jakarta.xml.ns.persistence.orm.OneToOne;
import ee.jakarta.xml.ns.persistence.orm.OrderColumn;
import ee.jakarta.xml.ns.persistence.orm.PrimaryKeyJoinColumn;

/**
 * Default implementation of methods to create or collect associations 
 * for the given {@link Mapping} context and {@link FieldOutline}.
 */
public class DefaultAssociationMapping implements AssociationMapping
{
	@Override
	public Collection<FieldOutline> getSourceIdFieldsOutline(Mapping context, FieldOutline fieldOutline)
	{
		final ClassOutline classOutline = fieldOutline.parent();
		return getIdFieldsOutline(classOutline);
	}

	@Override
	public Collection<FieldOutline> getTargetIdFieldsOutline(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		final CTypeInfo type = CTypeInfoUtils.getCommonBaseTypeInfo(types);
		
		assert type != null;
		assert type instanceof CClass;
		
		if (type instanceof CClassInfo)
		{
			final ClassOutline targetClassOutline = fieldOutline.parent().parent().getClazz((CClassInfo) type);
			return getIdFieldsOutline(targetClassOutline);
		}
		else
		{
			String level = System.getProperty(TODO_LOG_LEVEL);
			if ( "WARN".equalsIgnoreCase(level) )
			{
				context.getPlugin().warn
				(
					"{}, getTargetIdFieldsOutline: Class={}, Field={};"
					+ " references the type [{}] which is not present in the XJC model"
					+ " (it is probably a class reference due to episodic compilation)."
					+ " Due to this reason HyperJAXB cannot generate correct identifier column mapping."
					+ " Please customize your association manually. See also issue HJIII-51.",
					toLocation(fieldOutline),
					fieldOutline.parent().target.shortName,
					propertyInfo.getName(false),
					type.getType().fullName()
				);
			}
			return Collections.emptyList();
		}
	}

	private Collection<FieldOutline> getIdFieldsOutline(final ClassOutline classOutline)
	{
		final Collection<FieldOutline> idFieldOutlines = new ArrayList<FieldOutline>();
		ClassOutline current = classOutline;
		while (current != null)
		{
			for (FieldOutline idFieldOutline : current.getDeclaredFields())
			{
				final CPropertyInfo propertyInfo = idFieldOutline.getPropertyInfo();
				if
				(
					(
						containsCustomization(propertyInfo, ID_ELEMENT_NAME)
						|| containsCustomization(propertyInfo, EMBEDDED_ID_ELEMENT_NAME)
					)
					&& !containsCustomization(propertyInfo, IGNORED_ELEMENT_NAME)
				)
				{
					idFieldOutlines.add(idFieldOutline);
				}
			}
			current = current.getSuperClass();
		}
		return idFieldOutlines;
	}
	// * 1:1

	@Override
	public void createPrimaryKeyJoinColumns(Mapping context, FieldOutline fieldOutline,
		Collection<FieldOutline> idFieldOutlines, List<PrimaryKeyJoinColumn> primaryKeyJoinColumns)
	{
		final Iterator<PrimaryKeyJoinColumn> joinColumnIterator =
			new ArrayList<PrimaryKeyJoinColumn>( primaryKeyJoinColumns).iterator();
		
		for (FieldOutline idFieldOutline : idFieldOutlines)
		{
			final PrimaryKeyJoinColumn joinColumn;
			if (joinColumnIterator.hasNext())
				joinColumn = joinColumnIterator.next();
			else
			{
				joinColumn = new PrimaryKeyJoinColumn();
				primaryKeyJoinColumns.add(joinColumn);
			}
			createPrimaryKeyJoinColumn(context, fieldOutline, idFieldOutline, joinColumn);
		}
	}

	protected void createPrimaryKeyJoinColumn(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline,
		PrimaryKeyJoinColumn primaryKeyJoinColumn)
	{
		createPrimaryKeyJoinColumn$Name(context, fieldOutline, idFieldOutline, primaryKeyJoinColumn);
	}

	protected void createPrimaryKeyJoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, PrimaryKeyJoinColumn primaryKeyJoinColumn)
	{
		if ( (primaryKeyJoinColumn.getName() == null) || "##default".equals(primaryKeyJoinColumn.getName()) )
			primaryKeyJoinColumn.setName(context.getNaming().getJoinColumn$Name(context, fieldOutline, idFieldOutline));
	}
	
	// * M:1
	// * 1:M
	// * 1:1
	@Override
	public void createJoinColumns(Mapping context, FieldOutline fieldOutline, Collection<FieldOutline> idFieldOutlines,
		List<JoinColumn> joinColumns)
	{
		final Iterator<JoinColumn> joinColumnIterator = new ArrayList<JoinColumn>(joinColumns).iterator();
		
		for (FieldOutline idFieldOutline : idFieldOutlines)
		{
			final JoinColumn joinColumn;
			if (joinColumnIterator.hasNext())
				joinColumn = joinColumnIterator.next();
			else
			{
				joinColumn = new JoinColumn();
				joinColumns.add(joinColumn);
			}
			createJoinColumn(context, fieldOutline, idFieldOutline, joinColumn);
		}
	}

	protected void createJoinColumn(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline,
		JoinColumn joinColumn)
	{
		createJoinColumn$Name(context, fieldOutline, idFieldOutline, joinColumn);
	}

	protected void createJoinColumn$Name(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline,
		JoinColumn joinColumn)
	{
		if ( (joinColumn.getName() == null) || "##default".equals(joinColumn.getName()) )
			joinColumn.setName(context.getNaming().getJoinColumn$Name(context, fieldOutline, idFieldOutline));
	}

	// 1:1
	// M:1
	// 1:M
	// M:M
	@Override
	public void createJoinTable(Mapping context, FieldOutline fieldOutline,
		Collection<FieldOutline> sourceIdFieldOutlines, Collection<FieldOutline> targetIdFieldOutlines,
		JoinTable joinTable)
	{
		createJoinTable$Name(context, fieldOutline, joinTable);
		createJoinTable$JoinColumn(context, fieldOutline, sourceIdFieldOutlines, joinTable);
		createJoinTable$InverseJoinColumn(context, fieldOutline, targetIdFieldOutlines, joinTable);
	}

	protected void createJoinTable$Name(Mapping context, FieldOutline fieldOutline, JoinTable joinTable)
	{
		if ( (joinTable.getName() == null) || "##default".equals(joinTable.getName()) )
			joinTable.setName(context.getNaming().getJoinTable$Name(context, fieldOutline));
	}

	protected void createJoinTable$JoinColumn(Mapping context, FieldOutline fieldOutline,
		Collection<FieldOutline> idFieldOutlines, JoinTable joinTable)
	{
		final Iterator<JoinColumn> joinColumnIterator = new ArrayList<JoinColumn>(joinTable.getJoinColumn()).iterator();
		
		for (FieldOutline idFieldOutline : idFieldOutlines)
		{
			final JoinColumn joinColumn;
			if (joinColumnIterator.hasNext())
				joinColumn = joinColumnIterator.next();
			else
			{
				joinColumn = new JoinColumn();
				joinTable.getJoinColumn().add(joinColumn);
			}
			createJoinTable$JoinColumn(context, fieldOutline, idFieldOutline, joinColumn);
		}
	}

	protected void createJoinTable$InverseJoinColumn(Mapping context, FieldOutline fieldOutline,
		Collection<FieldOutline> idFieldOutlines, JoinTable joinTable)
	{
		final Iterator<JoinColumn> joinColumnIterator =
			new ArrayList<JoinColumn>(joinTable.getInverseJoinColumn()).iterator();
		final Map<FieldOutline, JoinColumn> map = new IdentityHashMap<FieldOutline, JoinColumn>();
		
		for (FieldOutline idFieldOutline : idFieldOutlines)
		{
			final JoinColumn joinColumn;
			if (joinColumnIterator.hasNext())
				joinColumn = joinColumnIterator.next();
			else
			{
				joinColumn = new JoinColumn();
				joinTable.getInverseJoinColumn().add(joinColumn);
			}
			map.put(idFieldOutline, joinColumn);
			createJoinTable$InverseJoinColumn(context, fieldOutline, idFieldOutline, joinColumn);
		}
	}

	protected void createJoinTable$JoinColumn(Mapping context, FieldOutline fieldOutline, FieldOutline idFieldOutline,
		JoinColumn joinColumn)
	{
		createJoinTable$JoinColumn$Name(context, fieldOutline, idFieldOutline, joinColumn);
		// createJoinTable$JoinColumn$ReferencedColumnName(context, fieldOutline, idFieldOutline, joinColumn);
	}

	protected void createJoinTable$InverseJoinColumn(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, JoinColumn joinColumn)
	{
		createManyToOne$JoinTable$InverseJoinColumn$Name(context, fieldOutline, idFieldOutline, joinColumn);
	}

	protected void createJoinTable$JoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, JoinColumn joinColumn)
	{
		if ( (joinColumn.getName() == null) || "##default".equals(joinColumn.getName()) )
			joinColumn.setName(context.getNaming().getJoinTable$JoinColumn$Name(context, fieldOutline, idFieldOutline));
	}

	protected void createJoinTable$JoinColumn$ReferencedColumnName(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, JoinColumn joinColumn)
	{
		if ( (joinColumn.getName() == null) || "##default".equals(joinColumn.getName()) )
			joinColumn.setReferencedColumnName(context.getNaming().getColumn$Name(context, idFieldOutline));
	}

	@Override
	public void createOrderColumn(Mapping context, FieldOutline fieldOutline, final OrderColumn orderColumn)
	{
		createOrderColumn$Name(context, fieldOutline, orderColumn);
	}

	protected void createOrderColumn$Name(Mapping context, FieldOutline fieldOutline, final OrderColumn orderColumn)
	{
		if ( (orderColumn.getName() == null) || "##default".equals(orderColumn.getName()) )
			orderColumn.setName(context.getNaming().getOrderColumn$Name(context, fieldOutline));
	}

	@Override
	public void createElementCollection$OrderColumn(Mapping context, FieldOutline fieldOutline,
		final OrderColumn orderColumn)
	{
		createElementCollection$OrderColumn$Name(context, fieldOutline, orderColumn);
	}

	protected void createElementCollection$OrderColumn$Name(Mapping context, FieldOutline fieldOutline,
		final OrderColumn orderColumn)
	{
		if ( (orderColumn.getName() == null) || "##default".equals(orderColumn.getName()) )
			orderColumn.setName(context.getNaming().getElementCollection$OrderColumn$Name(context, fieldOutline));
	}

	@Override
	public void createElementCollection$CollectionTable$JoinColumns(Mapping context, FieldOutline fieldOutline,
		Collection<FieldOutline> idFieldOutlines, List<JoinColumn> joinColumns)
	{
		final Iterator<JoinColumn> joinColumnIterator = new ArrayList<JoinColumn>(joinColumns).iterator();
		for (FieldOutline idFieldOutline : idFieldOutlines)
		{
			final JoinColumn joinColumn;
			if (joinColumnIterator.hasNext())
				joinColumn = joinColumnIterator.next();
			else
			{
				joinColumn = new JoinColumn();
				joinColumns.add(joinColumn);
			}
			createElementCollection$CollectionTable$JoinColumn(context, fieldOutline, idFieldOutline, joinColumn);
		}
	}

	protected void createElementCollection$CollectionTable$JoinColumn(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, JoinColumn joinColumn)
	{
		createElementCollection$CollectionTable$JoinColumn$Name(context, fieldOutline, idFieldOutline, joinColumn);
	}

	protected void createElementCollection$CollectionTable$JoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, JoinColumn joinColumn)
	{
		if ( (joinColumn.getName() == null) || "##default".equals(joinColumn.getName()) )
		{
			joinColumn.setName(context.getNaming()
				.getElementCollection$CollectionTable$JoinColumn$Name(context, fieldOutline, idFieldOutline));
		}
	}

	protected void createManyToOne$JoinTable$InverseJoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline, JoinColumn joinColumn)
	{
		if ( (joinColumn.getName() == null) || "##default".equals(joinColumn.getName()) )
		{
			joinColumn.setName(context.getNaming()
				.getJoinTable$InverseJoinColumn$Name(context, fieldOutline, idFieldOutline));
		}
	}

	@Override
	public AssociationMapping createEmbeddedAssociationMapping(FieldOutline fieldOutline)
	{
		return new EmbeddedAssociationMappingWrapper(this, fieldOutline);
	}

	@Override
	public void createAssociationOverride(Mapping context, FieldOutline fieldOutline,
		final List<AssociationOverride> associationOverrides)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		
		assert types.size() == 1;
		
		final CTypeInfo typeInfo = types.iterator().next();
		
		assert typeInfo instanceof CClassInfo;
		
		final CClassInfo classInfo = (CClassInfo) typeInfo;
		final Outline outline = fieldOutline.parent().parent();
		final ClassOutline classOutline = outline.getClazz(classInfo);
		final Map<String, AssociationOverride> associationOverridesMap = new HashMap<String, AssociationOverride>();
		
		for (final AssociationOverride associationOverride : associationOverrides)
		{
			associationOverridesMap.put(associationOverride.getName(), associationOverride);
		}
		
		Mapping embeddedMapping = context.createEmbeddedMapping(context, fieldOutline);
		final EmbeddableAttributes embeddableAttributes = embeddedMapping.getEmbeddableAttributesMapping()
			.process(embeddedMapping, classOutline);
		
		for (final ManyToOne source : embeddableAttributes.getManyToOne())
		{
			final String name = source.getName();
			final AssociationOverride associationOverride;
			if (!associationOverridesMap.containsKey(name))
			{
				associationOverride = new AssociationOverride();
				associationOverride.setName(name);
				associationOverride.setJoinTable(source.getJoinTable());
				associationOverride.getJoinColumn().addAll(source.getJoinColumn());
				associationOverridesMap.put(name, associationOverride);
				associationOverrides.add(associationOverride);
			}
			else
				associationOverride = associationOverridesMap.get(name);
		}
		
		for (final OneToMany source : embeddableAttributes.getOneToMany())
		{
			final String name = source.getName();
			final AssociationOverride associationOverride;
			if (!associationOverridesMap.containsKey(name))
			{
				associationOverride = new AssociationOverride();
				associationOverride.setName(name);
				associationOverride.setJoinTable(source.getJoinTable());
				if (source.getMappedBy() != null)
				{
					// Join columns must not be overridden for 1:X
					// associationOverride.getJoinColumn()
					//   .addAll(source.getJoinColumn());
					source.getJoinColumn().clear();
				}
				associationOverridesMap.put(name, associationOverride);
				associationOverrides.add(associationOverride);
			}
			else
				associationOverride = associationOverridesMap.get(name);
		}
		
		for (final ManyToMany source : embeddableAttributes.getManyToMany())
		{
			final String name = source.getName();
			final AssociationOverride associationOverride;
			if (!associationOverridesMap.containsKey(name))
			{
				associationOverride = new AssociationOverride();
				associationOverride.setName(name);
				associationOverride.setJoinTable(source.getJoinTable());
				associationOverridesMap.put(name, associationOverride);
				associationOverrides.add(associationOverride);
			}
			else
				associationOverride = associationOverridesMap.get(name);
		}
		
		for (final OneToOne source : embeddableAttributes.getOneToOne())
		{
			final String name = source.getName();
			final AssociationOverride associationOverride;
			if (!associationOverridesMap.containsKey(name))
			{
				associationOverride = new AssociationOverride();
				associationOverride.setName(name);
				associationOverride.setJoinTable(source.getJoinTable());
				if (source.getMappedBy() == null)
				{
					// Join columns must not be overridden for 1:X
					// associationOverride.getJoinColumn()
					//   .addAll(source.getJoinColumn());
				}
				else
					associationOverride.getJoinColumn().addAll(source.getJoinColumn());
				associationOverridesMap.put(name, associationOverride);
				associationOverrides.add(associationOverride);
			}
			else
				associationOverride = associationOverridesMap.get(name);
		}
		
		// TODO Element collection
		// http://jira.highsource.org/browse/HJIII-64
		for (final ElementCollection source : embeddableAttributes.getElementCollection())
		{
			final String name = source.getName();
			final AssociationOverride associationOverride;
			if (!associationOverridesMap.containsKey(name))
			{
				associationOverride = new AssociationOverride();
				associationOverride.setName(name);
				final JoinTable joinTable = new JoinTable();
				if (source.getCollectionTable() != null)
				{
					joinTable.setCatalog(source.getCollectionTable().getCatalog());
					joinTable.setSchema(source.getCollectionTable().getSchema());
					joinTable.setName(source.getCollectionTable().getName());
					joinTable.getJoinColumn().addAll(source.getCollectionTable().getJoinColumn());
				}
				associationOverride.setJoinTable(joinTable);
				associationOverridesMap.put(name, associationOverride);
				associationOverrides.add(associationOverride);
			}
			else
				associationOverride = associationOverridesMap.get(name);
		}
		
		for (final Embedded embedded : embeddableAttributes.getEmbedded())
		{
			final String parentName = embedded.getName();
			for (AssociationOverride embeddedAssociationOverride : embedded.getAssociationOverride())
			{
				final String childName = embeddedAssociationOverride.getName();
				final String name = parentName + "." + childName;
				final AssociationOverride associationOverride;
				if (!associationOverridesMap.containsKey(name))
				{
					associationOverride = new AssociationOverride();
					associationOverride.setName(name);
					associationOverride.setJoinTable(embeddedAssociationOverride.getJoinTable());
					associationOverride.getJoinColumn().addAll(embeddedAssociationOverride.getJoinColumn());
					associationOverridesMap.put(name, associationOverride);
					associationOverrides.add(associationOverride);
				}
				else
					associationOverride = associationOverridesMap.get(name);
			}
		}
	}
}
