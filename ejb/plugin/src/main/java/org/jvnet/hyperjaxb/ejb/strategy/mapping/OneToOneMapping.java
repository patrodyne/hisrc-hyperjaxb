package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.jvnet.basicjaxb.lang.StringUtils.capitalize;
import static org.jvnet.basicjaxb.util.CodeModelUtils.groupMethods;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.field;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.getter;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.matchByTypeAndName;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCast;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.JoinColumn;
import ee.jakarta.xml.ns.persistence.orm.OneToOne;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlTransient;

public class OneToOneMapping implements FieldOutlineMapping<OneToOne>
{
	@Override
	public OneToOne process(Mapping context, FieldOutline fieldOutline)
	{
		final OneToOne oneToOne = context.getCustomizing().getOneToOne(fieldOutline);
		createOneToOne$Name(context, fieldOutline, oneToOne);
		createOneToOne$TargetEntity(context, fieldOutline, oneToOne);

		// The @JoinColumn annotation specifies the column to use for joining an entity
		// association or element collection. On the other hand, the mappedBy attribute
		// is used to define the referencing side (non-owning side) of the relationship.

		// Join columns are not used for 'mapped-by' and 'maps-id'.
		if ( oneToOne.getMappedBy() != null )
		{
			oneToOne.getJoinColumn().clear();
			createOneToOne$MappedByMethods(context, fieldOutline, oneToOne);
		}
		else if ( oneToOne.getMapsId() != null )
		{
			// Verify join column names.
			boolean isUnNamedJoinColumns = true;
			for ( JoinColumn joinColumn : oneToOne.getJoinColumn() )
			{
				String name = joinColumn.getName();
				if ( (name != null) && !name.isBlank() )
				{
					isUnNamedJoinColumns = false;
					break;
				}
			}
			if ( isUnNamedJoinColumns )
				oneToOne.getJoinColumn().clear();
		}
		else
			createOneToOne$JoinTableOrJoinColumnOrPrimaryKeyJoinColumn(context, fieldOutline, oneToOne);

		return oneToOne;
	}

	private void createOneToOne$MappedByMethods(Mapping context, FieldOutline fieldOutline, final OneToOne oneToOne)
	{
		if ( fieldOutline.getRawType() instanceof JDefinedClass )
		{
			JDefinedClass mappedBySideClass = fieldOutline.parent().implClass;
			String mappedBySetterName = "set" + capitalize(oneToOne.getMappedBy());

			JDefinedClass owningSideClass = (JDefinedClass) fieldOutline.getRawType();
			JType[] mappedBySetterTypes = new JType[] { mappedBySideClass };
			JMethod mappedBySetter = owningSideClass.getMethod(mappedBySetterName, mappedBySetterTypes);

			if ( mappedBySetter != null )
			{
				String mappedBySidePropName = fieldOutline.getPropertyInfo().getName(true);
				createOneToOne$AfterUnmarshalMethod(context, mappedBySideClass, owningSideClass, mappedBySetter);
				createOneToOne$TieItemMethod(context, mappedBySideClass, mappedBySidePropName, owningSideClass, mappedBySetter);
			}
		}
	}

	private void createOneToOne$AfterUnmarshalMethod(Mapping context, JDefinedClass mappedBySideClass, JDefinedClass owningSideClass,
		JMethod mappedBySetter)
	{
		// Prepare owning-side class references.
		JCodeModel owningSideModel = owningSideClass.owner();
		JClass refUnmarshaller = owningSideModel.ref(Unmarshaller.class);
		JClass refObject = owningSideModel.ref(Object.class);

		// Look for an existing "afterUnmarshal" method
		JMethod afterUnmarshalMethod =
			owningSideClass.getMethod("afterUnmarshal", new JType[] {refUnmarshaller, refObject});

		// Otherwise, add a new method to the many-side class.
		if ( afterUnmarshalMethod == null  )
			afterUnmarshalMethod = owningSideClass.method(JMod.NONE, owningSideModel.VOID, "afterUnmarshal");

		// Get mapped-by setter parameter references.
		@SuppressWarnings("unused")
		JVar aumTarget = afterUnmarshalMethod.param(Unmarshaller.class, "target");
		JVar aumParent = afterUnmarshalMethod.param(Object.class, "parent");

		// Add mapped-by setter statement and the primary id setter(s)
		// to the 'afterUnmarshal' method.
		JBlock aumBody = afterUnmarshalMethod.body();
		JInvocation aumParentCond = mappedBySideClass.dotclass().invoke("isAssignableFrom").arg(aumParent.invoke("getClass"));
		JBlock aumIfParentThen = aumBody._if(aumParentCond)._then();
		JCast mappedBySideCast = cast(mappedBySideClass, aumParent);

		aumIfParentThen.add(invoke(mappedBySetter).arg(mappedBySideCast));

		List<JInvocation> idSyncList =
			createOneToOne$IdSyncList(context, mappedBySideClass, mappedBySideCast, owningSideClass, true);
		for ( JInvocation idSync : idSyncList )
			aumIfParentThen.add(idSync);

		afterUnmarshalMethod.javadoc()
			.add("Callback method invoked after unmarshalling XML data into this entity. ");
	}

	private void createOneToOne$TieItemMethod(Mapping context, JDefinedClass mappedBySideClass,
		String mappedBySidePropName, JDefinedClass owningSideClass, JMethod mappedBySetter)
	{
		String getItemName = "get" + mappedBySidePropName;
		JMethod getItemMethod = mappedBySideClass.getMethod(getItemName, new JType[0]);
		if ( getItemMethod != null )
		{
			String tieItemName = "tie" + mappedBySidePropName;
			JMethod tieItemMethod = mappedBySideClass.method(PUBLIC, mappedBySideClass, tieItemName);
			{
				JBlock tieItemMethodBody = tieItemMethod.body();
				JInvocation invokeGetItem = invoke(getItemMethod);
				tieItemMethodBody.add(invokeGetItem.invoke(mappedBySetter).arg(_this()));

				// getMyEntityData().setId(getId());
				List<JInvocation> idSyncList =
					createOneToOne$IdSyncList(context, mappedBySideClass, invokeGetItem, owningSideClass, false);
				for ( JInvocation idSync : idSyncList )
					tieItemMethodBody.add(idSync);

				tieItemMethodBody._return(_this());
			}
			tieItemMethod.javadoc().add("OneToOne tie the {@code " + owningSideClass.name() + "} item to this entity.");

			groupMethods(mappedBySideClass, getItemMethod, tieItemMethod);
		}
	}

	private List<JInvocation> createOneToOne$IdSyncList(Mapping context, JDefinedClass mappedBySideClass,
		JExpression otherSideRef, JDefinedClass owningSideClass, boolean owningSide)
	{
		List<JInvocation> idSyncList = new ArrayList<>();
		// Sync the primary identifiers from mappedBySideClass to owningSideClass.
		// Example: setId(((MyEntity) parent).getId());
		Map<JDefinedClass, List<FieldOutline>> pidMap = context.getPlugin().getPrimaryIdMap();
		List<FieldOutline> mappedByIdFields = pidMap.get(mappedBySideClass);
		if ( mappedByIdFields != null )
		{
			List<FieldOutline> owningSideIdFields = pidMap.get(owningSideClass);
			if ( owningSideIdFields != null )
			{
				for ( FieldOutline mappedByIdField : mappedByIdFields )
				{
					for ( FieldOutline owningSideIdField : owningSideIdFields )
					{
						if ( matchByTypeAndName(owningSideIdField, mappedByIdField) )
						{
							JMethod mappedBySideIdGetter = getter(mappedByIdField);
							JMethod owningSideIdSetter = setter(owningSideIdField);
							if ( owningSide )
							{
								JInvocation mappedBySideId = otherSideRef.invoke(mappedBySideIdGetter);
								idSyncList.add(invoke(owningSideIdSetter).arg(mappedBySideId));

								// Owning side identifiers are XML transient because
								// the mappedBy side provides the identifiers.
								JFieldVar owningSideIdFieldVar = field(owningSideIdField);
								// XmlTransient excludes all other JAXB annotations.
								if ( !hasXmlTransient(owningSideIdFieldVar) )
									owningSideIdFieldVar.annotate(XmlTransient.class);
							}
							else
							{
								JInvocation mappedBySideIdRef = invoke(mappedBySideIdGetter);
								idSyncList.add(otherSideRef.invoke(owningSideIdSetter).arg(mappedBySideIdRef));
							}
						}
					}
				}
			}
		}
		return idSyncList;
	}

	private boolean hasXmlTransient(JAnnotatable annotatable)
	{
		boolean hasXmlTransient = false;
		String xtName = XmlTransient.class.getName();
		for ( JAnnotationUse au : annotatable.annotations() )
		{
			if ( xtName.equals(au.getAnnotationClass().fullName()) )
			{
				hasXmlTransient = true;
				break;
			}
		}
		return hasXmlTransient;
	}

	public void createOneToOne$Name(Mapping context, FieldOutline fieldOutline, final OneToOne oneToOne)
	{
		oneToOne.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

	public void createOneToOne$TargetEntity(Mapping context, FieldOutline fieldOutline, final OneToOne oneToOne)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		assert types.size() == 1;
		final CTypeInfo type = types.iterator().next();
		assert type instanceof CClass;
		final CClass childClassInfo = (CClass) type;
		oneToOne.setTargetEntity
		(
			context.getNaming().getEntityClass
			(
				context,
				fieldOutline.parent().parent(),
				childClassInfo.getType()
			)
		);
	}

	public void createOneToOne$JoinTableOrJoinColumnOrPrimaryKeyJoinColumn(Mapping context, FieldOutline fieldOutline,
		OneToOne oneToOne)
	{
		if ( !oneToOne.getPrimaryKeyJoinColumn().isEmpty() )
		{
			final Collection<FieldOutline> idFieldOutlines =
				context.getAssociationMapping().getSourceIdFieldsOutline(context, fieldOutline);

			// if (idFieldOutlines.isEmpty())
			//     oneToOne.getPrimaryKeyJoinColumn().clear();
			// else
			context.getAssociationMapping().createPrimaryKeyJoinColumns(context, fieldOutline, idFieldOutlines,
				oneToOne.getPrimaryKeyJoinColumn());
		}
		else if ( !oneToOne.getJoinColumn().isEmpty() )
		{
			final Collection<FieldOutline> idFieldsOutline =
				context.getAssociationMapping().getSourceIdFieldsOutline(context, fieldOutline);

			// if (idFieldsOutline.isEmpty())
			//     oneToOne.getJoinColumn().clear();
			// else
			context.getAssociationMapping().createJoinColumns(context, fieldOutline, idFieldsOutline,
				oneToOne.getJoinColumn());
		}
		else if ( oneToOne.getJoinTable() != null )
		{
			final Collection<FieldOutline> sourceIdFieldOutlines =
				context.getAssociationMapping().getSourceIdFieldsOutline(context, fieldOutline);
			final Collection<FieldOutline> targetIdFieldOutlines =
				context.getAssociationMapping().getTargetIdFieldsOutline(context, fieldOutline);

			// if (sourceIdFieldOutlines.isEmpty())
			//     oneToOne.setJoinTable(null);
			// else
			context.getAssociationMapping().createJoinTable(context, fieldOutline, sourceIdFieldOutlines,
				targetIdFieldOutlines, oneToOne.getJoinTable());
		}
	}
}
