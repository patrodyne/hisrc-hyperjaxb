package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static jakarta.persistence.GenerationType.SEQUENCE;
import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.valueOf;
import static org.jvnet.basicjaxb.util.CustomizationUtils.containsCustomization;
import static org.jvnet.hyperjaxb.jpa.Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;

import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.DiscriminatorColumn;
import ee.jakarta.xml.ns.persistence.orm.Entity;
import ee.jakarta.xml.ns.persistence.orm.Id;
import ee.jakarta.xml.ns.persistence.orm.Inheritance;
import ee.jakarta.xml.ns.persistence.orm.Table;
import jakarta.persistence.InheritanceType;

public class EntityMapping
	implements
	ClassOutlineMapping<Entity>
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	@Override
	public Entity process(Mapping context, ClassOutline classOutline)
	{
		setPlugin(context.getPlugin());
		final Entity entity = context.getCustomizing().getEntity(classOutline);
		createEntity(context, classOutline, entity);
		return entity;
	}

	public void createEntity(Mapping context, ClassOutline classOutline, final Entity entity)
	{
		createEntity$Name(context, classOutline, entity);
		createEntity$Class(context, classOutline, entity);
		createEntity$Inheritance(context, classOutline, entity);
		createEntity$Table(context, classOutline, entity);
		createEntity$Attributes(context, classOutline, entity);
	}

	public void createEntity$Name(Mapping context, ClassOutline classOutline, final Entity entity)
	{
		if (entity.getName() == null || "##default".equals(entity.getName()))
			entity.setName(context.getNaming().getEntityName(context, classOutline.parent(), classOutline.target));
	}

	public void createEntity$Class(Mapping context, ClassOutline classOutline, final Entity entity)
	{
		if (entity.getClazz() == null || "##default".equals(entity.getClazz()))
			entity.setClazz(context.getNaming().getEntityClass(context, classOutline.parent(), classOutline.target));
	}

	public void createEntity$Inheritance(Mapping context, ClassOutline classOutline, final Entity entity)
	{
		final InheritanceType inheritanceStrategy = getInheritanceStrategy(context, classOutline, entity);
		if ( isRootClass(context, classOutline) )
		{
			boolean isSuperClass = isSuperClass(context, classOutline);
			boolean containsNDC = containsNonDiscriminatorColumn(entity);
			
			if ( isSuperClass  || !containsNDC )
			{
				// This entity DOES have sub-classes for inheritance OR
				// We need to keep discriminator when present for insertability (i.e. EclipseLink).
				if ( (entity.getInheritance() == null) || (entity.getInheritance().getStrategy() == null) )
				{
					entity.setInheritance(new Inheritance());
					entity.getInheritance().setStrategy(inheritanceStrategy.name());
				}				
			}
			
			if ( !isSuperClass && containsNDC )
			{
				// It is a root entity but it does NOT have sub-classes for inheritance
				// and it DOES have a non-discriminator field/column for EclipseLink insertability.
				// Remove explicit inheritance annotation and discriminator field/column.
				if ( (entity.getInheritance() != null) && (entity.getInheritance().getStrategy() != null) )
					entity.setInheritance(null);
				if (entity.getDiscriminatorColumn() != null)
					entity.setDiscriminatorColumn(null);
				if (entity.getDiscriminatorValue() != null)
					entity.setDiscriminatorValue(null);
			}
		}
		else
		{
			// Not a root class, it should always be insertable even without a discriminator field/column.
			// Remove explicit inheritance annotation and discriminator field/column.
			// Fixes "HHH000133: Discriminator column has to be defined in the root entity,
			//        it will be ignored in subclass" in Hibernate"
			if ( (entity.getInheritance() != null) && (entity.getInheritance().getStrategy() != null) )
				entity.setInheritance(null);
			if (entity.getDiscriminatorColumn() != null)
				entity.setDiscriminatorColumn(null);
			if (entity.getDiscriminatorValue() != null)
				entity.setDiscriminatorValue(null);
		}
	}

	private void createEntity$Table(Mapping context, ClassOutline classOutline, Entity entity)
	{
		final InheritanceType inheritanceStrategy = getInheritanceStrategy(context, classOutline, entity);
		switch (inheritanceStrategy)
		{
			case JOINED:
				if (entity.getTable() == null)
					entity.setTable(new Table());
				createTable(context, classOutline, entity.getTable());
				break;
			case SINGLE_TABLE:
				if (isRootClass(context, classOutline))
				{
					if (entity.getTable() == null)
						entity.setTable(new Table());
					createTable(context, classOutline, entity.getTable());
				}
				else
				{
					if (entity.getTable() != null)
						entity.setTable(null);
				}
				break;
			case TABLE_PER_CLASS:
				if (entity.getTable() == null)
					entity.setTable(new Table());
				createTable(context, classOutline, entity.getTable());
				break;
			default:
				throw new IllegalArgumentException("Unknown inheritance strategy.");
		}
	}

	public void createTable(Mapping context, ClassOutline classOutline, final Table table)
	{
		if (table.getName() == null || "##default".equals(table.getName()))
			table.setName(context.getNaming().getEntityTable$Name(context, classOutline));
	}

	public void createEntity$Attributes(Mapping context, ClassOutline classOutline, final Entity entity)
	{
		final Attributes attributes = context.getAttributesMapping()
			.process(context, classOutline);
		entity.setAttributes(attributes);
	}

	public InheritanceType getInheritanceStrategy(Mapping context,
		ClassOutline classOutline, Entity entity)
	{
		if ( isRootClass(context, classOutline) )
		{
			if ( (entity.getInheritance() != null) && (entity.getInheritance().getStrategy() != null) )
				return valueOf(entity.getInheritance().getStrategy());
			else
				return jakarta.persistence.InheritanceType.JOINED;
		}
		else
		{
			final ClassOutline superClassOutline = getSuperClass(context, classOutline);
			final Entity superClassEntity = context.getCustomizing().getEntity(superClassOutline);
			return getInheritanceStrategy(context, superClassOutline, superClassEntity);
		}
	}

	public ClassOutline getSuperClass(Mapping context, ClassOutline classOutline)
	{
		return classOutline.getSuperClass();
	}
	
	/*
	 * public ClassOutline getSuperClassOutline(Mapping context, ClassOutline classOutline)
	 * {
	 *     return classOutline.getSuperClass();
	 * }
	 * 
	 * public boolean isEntityClassHierarchyRoot(Mapping context, ClassOutline classOutline)
	 * {
	 *     final ClassOutline superClassOutline = getSuperClassOutline(context, classOutline);
	 *     if (superClassOutline == null)
	 *         return true;
	 *     else if (containsCustomization(classOutline, MAPPED_SUPERCLASS_ELEMENT_NAME))
	 *         return true;
	 *     else if (context.getIgnoring().isClassOutlineIgnored(superClassOutline))
	 *         return true;
	 *     else
	 *         return false;
	 * }
	 */

	public boolean isRootClass(Mapping context, ClassOutline classOutline)
	{
		if ( classOutline.getSuperClass() != null )
		{
			return !containsCustomization(classOutline, MAPPED_SUPERCLASS_ELEMENT_NAME)
				&& !isSelfOrAncestorRootClass(context, classOutline.getSuperClass());
		}
		else
			return !containsCustomization(classOutline, MAPPED_SUPERCLASS_ELEMENT_NAME);
	}

	public boolean isSelfOrAncestorRootClass(Mapping context, ClassOutline classOutline)
	{
		if (context.getIgnoring().isClassOutlineIgnored(context, classOutline))
			return false;
		else if ( isRootClass(context, classOutline) )
			return true;
		else if ( classOutline.getSuperClass() != null )
			return isSelfOrAncestorRootClass(context, classOutline.getSuperClass());
		else
			return !containsCustomization(classOutline, MAPPED_SUPERCLASS_ELEMENT_NAME);
	}

	/**
	 * Is the given class outline a superclass of any active class outline.
	 * 
	 * @param context The mapping context.
	 * @param theClassOutline The class outline to examine.
	 * 
	 * @return True when there is any class outline that is a sub-class; otherwise, false.
	 */
	public boolean isSuperClass(Mapping context, ClassOutline theClassOutline)
	{
		boolean isSuperClass = false;
		Outline outline = theClassOutline.parent();
		for ( ClassOutline anyClassOutline : outline.getClasses() )
		{
			if ( !context.getIgnoring().isClassOutlineIgnored(context, anyClassOutline) )
			{
				if ( theClassOutline.equals(anyClassOutline.getSuperClass()) )
				{
					isSuperClass = true;
					break;
				}
			}
		}
		return isSuperClass;
	}
	
	/**
	 * Does the given entity contain a non-discriminator column.
	 * 
	 * EclipseLink cannot insert an empty field/column set. This can occur when an entity
	 * uses the IDENTITY or AUTO generation strategy. This method checks for a non-empty
	 * field/column set. When this method retourns true then the caller may elect to
	 * remove the discriminator column when it is not required (i.e. the entity has no
	 * inheritance).
	 * 
	 * @param entity An ORM entity.
	 * 
	 * @return True when the entity contains a non-discriminator column; otherwise, false.
	 */
	public boolean containsNonDiscriminatorColumn(Entity entity)
	{
		boolean containsNDC = false;

		// The ORM attributes contain the entity field or property mappings.
		Attributes attributes = entity.getAttributes();
		if ( attributes != null )
		{
			// Entities using VERSION should be insertable.
			if ( attributes.getVersion().size() > 0 )
				containsNDC = true;
			else
			{
				// Entities using SEQUENCE or TABLE should be insertable.
				for ( Id id : attributes.getId() )
				{
					if ( ( id.getGeneratedValue() != null ) )
					{
						String strategy = id.getGeneratedValue().getStrategy();
						if ( (SEQUENCE.name().equals(strategy)) || (TABLE.name().equals(strategy)) )
						{
							containsNDC = true;
							break;
						}
					}
				} 
				
				// Otherwise ...
				if ( !containsNDC )
				{
					DiscriminatorColumn dc = entity.getDiscriminatorColumn();
					if ( dc != null )
					{
						// Check for at least one basic non-discriminator field.
						for (  Basic basic : attributes.getBasic() )
						{
							if ( !basic.getColumn().getName().equals(dc.getName()) )
							{
								containsNDC = true;
								break;
							}
						}			
					}
					else
					{
						// Any other Basic field will do.
						containsNDC = (attributes.getBasic().size() > 0);
					}
				}
			}
			
			//
			// TODO: Review this list for other non-discriminator column candidates:
			//
			// attributes.getManyToMany();
			// attributes.getElementCollection();
			// attributes.getEmbedded();
			// attributes.getEmbeddedId();
			// attributes.getManyToMany();
			// attributes.getManyToOne();
			// attributes.getOneToMany();
			// attributes.getOneToOne();

			// These are not discriminator column candidates:
			//
			// attributes.getDescription();
			// attributes.getTransient();
		}
		
		return containsNDC;
	}
}
