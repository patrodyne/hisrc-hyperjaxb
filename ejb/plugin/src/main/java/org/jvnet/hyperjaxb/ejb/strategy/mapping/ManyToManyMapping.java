package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static org.jvnet.basicjaxb.lang.StringUtils.capitalize;
import static org.jvnet.basicjaxb.util.FieldAccessorUtils.getter;

import java.util.Collection;
import java.util.List;

import org.jvnet.hyperjaxb.xjc.model.CTypeInfoUtils;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.ManyToMany;
import jakarta.xml.bind.Unmarshaller;

public class ManyToManyMapping implements FieldOutlineMapping<ManyToMany>
{
	@Override
	public ManyToMany process(Mapping context, FieldOutline fieldOutline)
	{
		final ManyToMany manyToMany = context.getCustomizing().getManyToMany(fieldOutline);
		createManyToMany$Name(context, fieldOutline, manyToMany);
		createManyToMany$OrderColumn(context, fieldOutline, manyToMany);
		createManyToMany$TargetEntity(context, fieldOutline, manyToMany);

		// The @JoinTable specifies the mapping of 'many-to-many' associations. It is applied
		// to the owning side of an association. On the other hand, the 'mappedBy' attribute
		// is used to define the referencing side (non-owning side) of the relationship.

		// Join table are not used for 'mapped-by'
//		if ( manyToMany.getMappedBy() != null )
//		{
//			manyToMany.setJoinTable(null);
//			createManyToMany$MappedByMethods(fieldOutline, manyToMany);
//		}
//		else
			createManyToMany$JoinTable(context, fieldOutline, manyToMany);

		return manyToMany;
	}

	private void createManyToMany$MappedByMethods(FieldOutline fieldOutline, ManyToMany manyToMany)
	{
		if ( fieldOutline.getRawType() instanceof JClass )
		{
			JClass valueType = (JClass) fieldOutline.getRawType();
			List<JClass> typeParameters = valueType.getTypeParameters();
			if ( !typeParameters.isEmpty() )
			{
				if ( typeParameters.get(0) instanceof JDefinedClass )
				{
					JDefinedClass mappedBySideClass = fieldOutline.parent().implClass;
					JDefinedClass owningSideClass = (JDefinedClass) typeParameters.get(0);

					JMethod owningGetter = getter(fieldOutline);
					if ( owningGetter != null )
					{
						createManyToMany$AfterUnmarshalMethod(owningSideClass, mappedBySideClass, owningGetter);
					}

					String mappedByGetterName = "get" + capitalize(manyToMany.getMappedBy());
					JMethod mappedByGetter = owningSideClass.getMethod(mappedByGetterName, new JType[0]);
					if ( mappedByGetter != null )
					{
						createManyToMany$AfterUnmarshalMethod(mappedBySideClass, owningSideClass, mappedByGetter);
					}
				}
			}
		}
	}

	private void createManyToMany$AfterUnmarshalMethod(JDefinedClass mappedBySideClass, JDefinedClass owningSideClass,
		JMethod mappedByGetter)
	{
		// Prepare owning-side class references.
		JCodeModel owningSideOwner = owningSideClass.owner();
		JClass refUnmarshaller = owningSideOwner.ref(Unmarshaller.class);
		JClass refObject = owningSideOwner.ref(Object.class);

		// Look for an existing "afterUnmarshal" method
		JMethod afterUnmarshalMethod =
			owningSideClass.getMethod("afterUnmarshal", new JType[] {refUnmarshaller, refObject});

		// Otherwise, add a new method to the owning-side class.
		if ( afterUnmarshalMethod == null  )
			afterUnmarshalMethod = owningSideClass.method(JMod.NONE, owningSideOwner.VOID, "afterUnmarshal");

		// Add mapped-by setter statement to the method.
		@SuppressWarnings("unused")
		JVar aumTarget = afterUnmarshalMethod.param(Unmarshaller.class, "target");
		JVar aumParent = afterUnmarshalMethod.param(Object.class, "parent");
		JBlock aumBody = afterUnmarshalMethod.body();

		aumBody.
			_if(mappedBySideClass.dotclass().invoke("isAssignableFrom").arg(aumParent.invoke("getClass"))).
			_then().invoke(invoke(mappedByGetter), "add").arg(cast(mappedBySideClass, aumParent));
		afterUnmarshalMethod.javadoc()
			.add("Callback method invoked after unmarshalling XML data into this entity. ");
	}

	public void createManyToMany$Name(Mapping context, FieldOutline fieldOutline, final ManyToMany manyToMany)
	{
		manyToMany.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

	public void createManyToMany$OrderColumn(Mapping context, FieldOutline fieldOutline, final ManyToMany manyToMany)
	{
		if ( manyToMany.getOrderColumn() != null )
			context.getAssociationMapping().createOrderColumn(context, fieldOutline, manyToMany.getOrderColumn());
	}

	public void createManyToMany$TargetEntity(Mapping context, FieldOutline fieldOutline, final ManyToMany manyToMany)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		final CTypeInfo type = CTypeInfoUtils.getCommonBaseTypeInfo(types);
		assert type != null;
		assert type instanceof CClass;
		final CClass childClassInfo = (CClass) type;
		manyToMany.setTargetEntity
		(
			context.getNaming().getEntityClass
			(
				context,
				fieldOutline.parent().parent(),
				childClassInfo.getType()
			)
		);
	}

	public void createManyToMany$JoinTable(Mapping context, FieldOutline fieldOutline, ManyToMany manyToMany)
	{
		final Collection<FieldOutline> sourceIdFieldOutlines =
			context.getAssociationMapping().getSourceIdFieldsOutline(context, fieldOutline);
		final Collection<FieldOutline> targetIdFieldOutlines =
			context.getAssociationMapping().getTargetIdFieldsOutline(context, fieldOutline);

		// if (sourceIdFieldOutlines.isEmpty())
		//     manyToMany.setJoinTable(null);
		// else
		if ( manyToMany.getJoinTable() != null )
		{
			context.getAssociationMapping().createJoinTable
			(
				context,
				fieldOutline,
				sourceIdFieldOutlines,
				targetIdFieldOutlines,
				manyToMany.getJoinTable()
			);
		}
		// else
		// {
		//     final JoinTable joinTable = new JoinTable();
		//     manyToMany.setJoinTable(joinTable);
		//     context.getAssociationMapping().createJoinTable
		//     (
		//         context,
		//         fieldOutline,
		//         sourceIdFieldOutlines,
		//         targetIdFieldOutlines,
		//         manyToMany.getJoinTable()
		//     );
		// }
	}
}
