package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._this;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.ref;
import static com.sun.codemodel.JMod.PUBLIC;
import static org.jvnet.basicjaxb.lang.StringUtils.capitalize;
import static org.jvnet.basicjaxb.util.CodeModelUtils.groupMethods;

import java.util.Collection;
import java.util.List;

import org.jvnet.hyperjaxb.xjc.model.CTypeInfoUtils;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.FieldOutline;

import ee.jakarta.xml.ns.persistence.orm.OneToMany;
import jakarta.xml.bind.Unmarshaller;

public class OneToManyMapping implements FieldOutlineMapping<OneToMany>
{
	@Override
	public OneToMany process(Mapping context, FieldOutline fieldOutline)
	{
		final OneToMany oneToMany = context.getCustomizing().getOneToMany(fieldOutline);
		createOneToMany$Name(context, fieldOutline, oneToMany);
		createOneToMany$OrderColumn(context, fieldOutline, oneToMany);
		createOneToMany$TargetEntity(context, fieldOutline, oneToMany);

		// Join columns must not be overridden for 1:X
		if ( oneToMany.getMappedBy() != null )
		{
			oneToMany.getJoinColumn().clear();
			createOneToMany$MappedByMethods(fieldOutline, oneToMany);
		}
		else
		{
			createOneToMany$JoinTableOrJoinColumn(context, fieldOutline, oneToMany);
		}

		return oneToMany;
	}

	public void createOneToMany$MappedByMethods(FieldOutline fieldOutline, final OneToMany oneToMany)
	{
		JDefinedClass oneSideClass = fieldOutline.parent().implClass;
		if ( fieldOutline.getRawType() instanceof JClass )
		{
			JClass valueType = (JClass) fieldOutline.getRawType();
			List<JClass> typeParameters = valueType.getTypeParameters();
			if ( !typeParameters.isEmpty() )
			{
				if ( typeParameters.get(0) instanceof JDefinedClass )
				{
					JDefinedClass manySideClass = (JDefinedClass) typeParameters.get(0);
					String mappedBySetterName = "set" + capitalize(oneToMany.getMappedBy());
					JMethod mappedBySetter = manySideClass.getMethod(mappedBySetterName, new JType[] { oneSideClass });
					if ( mappedBySetter != null )
					{
						createOneToMany$AfterUnmarshalMethod(oneSideClass, manySideClass, mappedBySetter);
						createOneToMany$RemoveItemMethod(oneSideClass, manySideClass, mappedBySetter, false);
						createOneToMany$RemoveItemMethod(oneSideClass, manySideClass, mappedBySetter, true);
						createOneToMany$AddItemMethod(oneSideClass, manySideClass, mappedBySetter, false);
						createOneToMany$AddItemMethod(oneSideClass, manySideClass, mappedBySetter, true);
						createOneToMany$TieItemMethod(oneSideClass, manySideClass, mappedBySetter);
					}
				}
			}
		}
	}

	public void createOneToMany$AfterUnmarshalMethod(JDefinedClass oneSideClass, JDefinedClass manySideClass, JMethod mappedBySetter)
	{
		// Prepare many-side class references.
		JCodeModel manySideOwner = manySideClass.owner();
		JClass refUnmarshaller = manySideOwner.ref(Unmarshaller.class);
		JClass refObject = manySideOwner.ref(Object.class);

		// Look for an existing "afterUnmarshal" method
		JMethod afterUnmarshalMethod =
			manySideClass.getMethod("afterUnmarshal", new JType[] {refUnmarshaller, refObject});

		// Otherwise, add a new method to the many-side class.
		if ( afterUnmarshalMethod == null  )
		{
			afterUnmarshalMethod = manySideClass.method(JMod.NONE, manySideOwner.VOID, "afterUnmarshal");
		}

		// Add mapped-by setter statement to the method.
		@SuppressWarnings("unused")
		JVar aumTarget = afterUnmarshalMethod.param(Unmarshaller.class, "target");
		JVar aumParent = afterUnmarshalMethod.param(Object.class, "parent");
		JBlock aumBody = afterUnmarshalMethod.body();
		aumBody.
			_if(aumParent._instanceof(oneSideClass)).
			_then().invoke(mappedBySetter).arg(cast(oneSideClass, aumParent));
		afterUnmarshalMethod.javadoc()
			.add("Callback method invoked after unmarshalling XML data into target. ");
	}

	private void createOneToMany$TieItemMethod(JDefinedClass oneSideClass, JDefinedClass manySideClass, JMethod mappedBySetter)
	{
		String getItemName = "get" + manySideClass.name();
		String tieItemName = "tie" + manySideClass.name();

		JMethod getItemMethod = oneSideClass.getMethod(getItemName, new JType[0]);
		JMethod tieItemMethod = oneSideClass.method(PUBLIC, oneSideClass, tieItemName);
		{
			JBlock tieItemMethodBody = tieItemMethod.body();
			{
				JForEach forEach = tieItemMethodBody.forEach(manySideClass, "item", invoke(getItemMethod));
				JFieldRef refItem = ref("item");
				forEach.body().add(refItem.invoke(mappedBySetter).arg(_this()));
			}
			tieItemMethodBody._return(_this());
		}
		tieItemMethod.javadoc().add("OneToMany tie each {@code " + manySideClass.name() + "} item to this entity.");

		groupMethods(oneSideClass, getItemMethod, tieItemMethod);
	}

	private void createOneToMany$AddItemMethod(JDefinedClass oneSideClass, JDefinedClass manySideClass, JMethod mappedBySetter, boolean varargs)
	{
		String getItemName = "get" + manySideClass.name();
		String addItemName = "add" + manySideClass.name();

		JMethod getItemMethod = oneSideClass.getMethod(getItemName, new JType[0]);
		JMethod addItemMethod = oneSideClass.method(PUBLIC, oneSideClass, addItemName);

		JVar parmItems = null;
		if ( varargs )
		{
			parmItems = addItemMethod.varParam(manySideClass, "items");
			addItemMethod.javadoc().addParam(parmItems).append("A vararg(s) of many-side entities to add.");
		}
		else
		{
			JClass manySideCollection= oneSideClass.owner().ref(Collection.class).narrow(manySideClass);
			parmItems = addItemMethod.param(manySideCollection, "items");
			addItemMethod.javadoc().addParam(parmItems).append("A collection of many-side entities to add.");
		}

		addItemMethod.javadoc().add("OneToMany add {@code " + manySideClass.name() + "} item(s) method.");
		{
			JBlock addItemMethodBody = addItemMethod.body();
			{
				JConditional cond = addItemMethodBody._if(parmItems.ne(_null()));
				{
					JForEach forEach = cond._then().forEach(manySideClass, "item", parmItems);
					JFieldRef refItem = ref("item");
					forEach.body().add(invoke(invoke(getItemMethod), "add").arg(refItem));
					forEach.body().add(refItem.invoke(mappedBySetter).arg(_this()));
				}
			}
			addItemMethodBody._return(_this());
		}

		groupMethods(oneSideClass, getItemMethod, addItemMethod);
	}

	private void createOneToMany$RemoveItemMethod(JDefinedClass oneSideClass, JDefinedClass manySideClass, JMethod mappedBySetter, boolean varargs)
	{
		String getItemName = "get" + manySideClass.name();
		String remItemName = "remove" + manySideClass.name();

		JMethod getItemMethod = oneSideClass.getMethod(getItemName, new JType[0]);
		JMethod remItemMethod = oneSideClass.method(PUBLIC, oneSideClass, remItemName);

		JVar parmItems = null;
		if ( varargs )
		{
			parmItems = remItemMethod.varParam(manySideClass, "items");
			remItemMethod.javadoc().addParam(parmItems).append("A vararg(s) of many-side entities to remove.");
		}
		else
		{
			JClass manySideCollection= oneSideClass.owner().ref(Collection.class).narrow(manySideClass);
			parmItems = remItemMethod.param(manySideCollection, "items");
			remItemMethod.javadoc().addParam(parmItems).append("A collection of many-side entities to remove.");
		}

		remItemMethod.javadoc().add("OneToMany remove {@code " + manySideClass.name() + "} item(s) method.");
		{
			JBlock remItemMethodBody = remItemMethod.body();
			{
				JConditional cond = remItemMethodBody._if(parmItems.ne(_null()));
				{
					JForEach forEach = cond._then().forEach(manySideClass, "item", parmItems);
					JFieldRef refItem = ref("item");
					forEach.body().add(invoke(invoke(getItemMethod), "remove").arg(refItem));
					forEach.body().add(refItem.invoke(mappedBySetter).arg(_null()));
				}
			}
			remItemMethodBody._return(_this());
		}

		groupMethods(oneSideClass, getItemMethod, remItemMethod);
	}

	public void createOneToMany$Name(Mapping context, FieldOutline fieldOutline, final OneToMany oneToMany)
	{
		oneToMany.setName(context.getNaming().getPropertyName(context, fieldOutline));
	}

	public void createOneToMany$OrderColumn(Mapping context, FieldOutline fieldOutline, final OneToMany source)
	{
		if ( source.getOrderColumn() != null )
		{
			context.getAssociationMapping().createOrderColumn(context, fieldOutline, source.getOrderColumn());
		}
	}

	public void createOneToMany$TargetEntity(Mapping context, FieldOutline fieldOutline, final OneToMany oneToMany)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		final CTypeInfo type = CTypeInfoUtils.getCommonBaseTypeInfo(types);

		assert type != null;
		assert type instanceof CClass;

		final CClass childClassInfo = (CClass) type;
		oneToMany.setTargetEntity
		(
			context.getNaming().getEntityClass
			(
				context,
				fieldOutline.parent().parent(),
				childClassInfo.getType()
			)
		);
	}

	public void createOneToMany$JoinTableOrJoinColumn(Mapping context, FieldOutline fieldOutline, OneToMany oneToMany)
	{
		if ( !oneToMany.getJoinColumn().isEmpty() )
		{
			final Collection<FieldOutline> idFieldsOutline = context.getAssociationMapping()
				.getSourceIdFieldsOutline(context, fieldOutline);

			// if ( idFieldsOutline.isEmpty() )
			//     oneToMany.getJoinColumn().clear();

			context.getAssociationMapping()
				.createJoinColumns(context, fieldOutline, idFieldsOutline, oneToMany.getJoinColumn());
		}
		else if ( oneToMany.getJoinTable() != null )
		{
			final Collection<FieldOutline> sourceIdFieldOutlines = context.getAssociationMapping()
				.getSourceIdFieldsOutline(context, fieldOutline);
			final Collection<FieldOutline> targetIdFieldOutlines = context.getAssociationMapping()
				.getTargetIdFieldsOutline(context, fieldOutline);

			// if ( sourceIdFieldOutlines.isEmpty() )
			//     oneToMany.setJoinTable(null);
			// else
			context.getAssociationMapping().createJoinTable(context, fieldOutline, sourceIdFieldOutlines,
				targetIdFieldOutlines, oneToMany.getJoinTable());
		}
	}
}
