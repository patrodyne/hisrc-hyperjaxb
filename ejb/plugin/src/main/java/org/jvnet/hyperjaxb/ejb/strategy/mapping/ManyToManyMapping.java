package org.jvnet.hyperjaxb.ejb.strategy.mapping;

import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._this;
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
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;

import ee.jakarta.xml.ns.persistence.orm.ManyToMany;

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
		if ( manyToMany.getMappedBy() != null )
		{
			manyToMany.setJoinTable(null);
			createManyToMany$MappedByMethods(fieldOutline, manyToMany);
		}
		else
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
				// Infer target type and name.
				// Note: Type is A JDirectClass from 'jaxb:class ref="..."'.
				JClass targetType = typeParameters.get(0);
				String targetSideClassName = targetType.fullName();

				// Attempt to resolve the target side defined class
				JDefinedClass targetSideClass = null;
				Outline outline = fieldOutline.parent().parent();
				for ( ClassOutline co : outline.getClasses() )
				{
					JDefinedClass coi = co.implClass;
					if ( targetSideClassName.equals(coi.fullName()) )
					{
						targetSideClass = coi;
						break;
					}
				}

				if ( targetSideClass != null )
				{
					String targetSidePropName = capitalize(manyToMany.getMappedBy());
					String targetGetterName = "get" + targetSidePropName;
					JMethod targetGetter = targetSideClass.getMethod(targetGetterName, new JType[0]);

					if ( targetGetter != null )
					{
						JDefinedClass sourceSideClass = fieldOutline.parent().implClass;
						String sourceSidePropName = fieldOutline.getPropertyInfo().getName(true);
						String sourceGetterName = "get" + sourceSidePropName;
						JMethod sourceGetter = sourceSideClass.getMethod(sourceGetterName, new JType[0]);

						// source to target
						createManyToMany$ActionItemMethod("remove", sourceSideClass, sourceGetter, sourceSidePropName, targetSideClass, targetGetter, false);
						createManyToMany$ActionItemMethod("remove", sourceSideClass, sourceGetter, sourceSidePropName, targetSideClass, targetGetter, true);
						createManyToMany$ActionItemMethod("add", sourceSideClass, sourceGetter, sourceSidePropName, targetSideClass, targetGetter, false);
						createManyToMany$ActionItemMethod("add", sourceSideClass, sourceGetter, sourceSidePropName, targetSideClass, targetGetter, true);
						createManyToMany$TieItemMethod(sourceSideClass, sourceGetter, sourceSidePropName, targetSideClass, targetGetter);

						// target to source
						createManyToMany$ActionItemMethod("remove", targetSideClass, targetGetter, targetSidePropName, sourceSideClass, sourceGetter, false);
						createManyToMany$ActionItemMethod("remove", targetSideClass, targetGetter, targetSidePropName, sourceSideClass, sourceGetter, true);
						createManyToMany$ActionItemMethod("add", targetSideClass, targetGetter, targetSidePropName, sourceSideClass, sourceGetter, false);
						createManyToMany$ActionItemMethod("add", targetSideClass, targetGetter, targetSidePropName, sourceSideClass, sourceGetter, true);
						createManyToMany$TieItemMethod(targetSideClass, targetGetter, targetSidePropName, sourceSideClass, sourceGetter);
					}
				}
			}
		}
	}

	private void createManyToMany$TieItemMethod(JDefinedClass sourceSideClass, JMethod sourceGetter,
		String sourceSidePropName, JDefinedClass targetSideClass, JMethod targetGetter)
	{
		String tieItemName = "tie" + sourceSidePropName;
		JMethod tieItemMethod = sourceSideClass.method(PUBLIC, sourceSideClass, tieItemName);

		tieItemMethod.javadoc().add("ManyToMany tie {@code " + targetSideClass.name() + "} item(s) method.");
		{
			JBlock tieItemMethodBody = tieItemMethod.body();
			{
				JFieldRef refItem = ref("item");
				JForEach forEach = tieItemMethodBody.forEach(targetSideClass, "item", invoke(sourceGetter));
				forEach.body()
					._if(invoke(refItem.invoke(targetGetter),"contains").arg(_this()).not())
					._then().invoke(refItem.invoke(targetGetter), "add").arg(_this());
			}
			tieItemMethodBody._return(_this());
		}

		groupMethods(sourceSideClass, sourceGetter, tieItemMethod);
	}

	private void createManyToMany$ActionItemMethod(String action, JDefinedClass sourceSideClass, JMethod sourceGetter,
        String sourceSidePropName, JDefinedClass targetSideClass, JMethod targetGetter, boolean varargs)
    {
        String actionItemName = action + sourceSidePropName;
        JMethod actionItemMethod = sourceSideClass.method(PUBLIC, sourceSideClass, actionItemName);

        JVar parmItems = null;
        if ( varargs )
        {
            parmItems = actionItemMethod.varParam(targetSideClass, "items");
            actionItemMethod.javadoc().addParam(parmItems).append("A vararg(s) of entities to "+action+".");
        }
        else
        {
            JClass targetSideCollection = sourceSideClass.owner().ref(Collection.class).narrow(targetSideClass);
            parmItems = actionItemMethod.param(targetSideCollection, "items");
            actionItemMethod.javadoc().addParam(parmItems).append("A collection of entities to "+action+".");
        }

        actionItemMethod.javadoc().add("ManyToMany "+action+" {@code " + targetSideClass.name() + "} item(s) method.");
        {
            JBlock actionItemMethodBody = actionItemMethod.body();
            {
                JConditional cond = actionItemMethodBody._if(parmItems.ne(_null()));
                {
                    JForEach forEach = cond._then().forEach(targetSideClass, "item", parmItems);
                    JFieldRef refItem = ref("item");
                    forEach.body()
                        .add(invoke(invoke(sourceGetter), action).arg(refItem))
                        .add(invoke(refItem.invoke(targetGetter), action).arg(_this()));
                }
            }
            actionItemMethodBody._return(_this());
        }

        groupMethods(sourceSideClass, sourceGetter, actionItemMethod);
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
