package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import org.jvnet.hyperjaxb.jpa.Customizations;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.FieldAccessor;

import jakarta.xml.bind.JAXBElement.GlobalScope;

public abstract class AbstractWrappingField extends AbstractField
{
	protected final static boolean CENTRAL_CASTING = true;
	
	protected final CPropertyInfo core;
	protected final JFieldRef coreField;

	protected JMethod getter;
	protected JMethod setter;
	public void generateAccessors()
	{
		getter = createGetter();
		setter = createSetter();
	}

	protected abstract JExpression unwrap(final JExpression source);
	protected abstract JExpression wrap(final JExpression target);
	
	public final JType getFieldType()
	{
		return implType;
	}

	@Override
	public final JType getRawType()
	{
		return exposedType;
	}

	public JExpression getCore()
	{
		if (coreField != null)
			return coreField;
		else
			return JExpr._this().invoke("get" + core.getName(true));
	}

	public void setCore(JBlock block, JExpression value)
	{
		if (coreField != null)
			block.assign(coreField, value);
		else
			block.invoke("set" + core.getName(true)).arg(value);
	}

	protected String getGetterName()
	{
		return (getFieldType().boxify().getPrimitiveType() == codeModel.BOOLEAN ? "is" : "get") + prop.getName(true);
	}

	protected String getSetterName()
	{
		return "set" + prop.getName(true);
	}
	
	public JClass getScope(CClassInfo scope)
	{
		if (scope == null)
			return codeModel.ref(GlobalScope.class);
		else
			return scope.toType(outline.parent(), Aspect.EXPOSED);
	}
	
	public AbstractWrappingField(ClassOutlineImpl context, CPropertyInfo prop, CPropertyInfo core)
	{
		super(context, prop);
		this.core = core;
		if (!Customizations.isGenerated(prop))
			this.coreField = JExpr.refthis(core.getName(false));
		else
			this.coreField = null;
		assert !exposedType.isPrimitive() && !implType.isPrimitive();
//		if (Customizations.isVersion(prop))
//		{
//			System.out.println("PROP IS VERSION");
//			System.out.println("PROP NAME: " + prop.getName(true));
//			System.out.println("PROP KIND: " + prop.kind());
//			for ( CTypeInfo ref : prop.ref() )
//				System.out.println("PROP REF TYPE: " + ref.getType().fullName());
//			if ( prop.getAdapter() != null )
//			{
//				System.out.println("PROP ADAPTER TYPE: " + prop.getAdapter().adapterType.fullName());
//				System.out.println("PROP ADAPTER CUSTOM TYPE: " + prop.getAdapter().customType.fullName());
//				System.out.println("PROP ADAPTER DEFAULT TYPE: " + prop.getAdapter().defaultType.fullName());
//			}
//			System.out.println("THIS FIELD TYPE: " + getFieldType());
//			System.out.println("THIS RAW TYPE: " + getRawType());
//			System.out.println("THIS CORE: " + getCore());
//			System.out.println("THIS GETTER NAME: " + getGetterName());
//			System.out.println("THIS SETTER NAME: " + getSetterName());
//		}
	}

	protected JMethod createGetter()
	{
		final MethodWriter writer = outline.createMethodWriter();
		final JMethod getter = writer.declareMethod(exposedType, getGetterName());
		JExpression source = getCore();
		final JExpression unwrapCondition = unwrapCondifiton(source);

		if (unwrapCondition == null)
			getter.body()._return(unwrap(source));
		else
		{
			final JConditional _if = getter.body()._if(unwrapCondition);
			_if._then()._return(unwrap(source));
			_if._else()._return(JExpr._null());
		}

		return getter;
	}
	
	protected JMethod createSetter()
	{
		final JMethod setter;
		final MethodWriter writer = outline.createMethodWriter();
		setter = writer.declareMethod(codeModel.VOID, getSetterName());
		final JVar target = writer.addParameter(exposedType, "target");
		final JExpression wrapCondition = wrapCondifiton(target);

		if (wrapCondition == null)
			setCore(setter.body(), wrap(target));
		else
		{
			final JConditional _if = setter.body()._if(wrapCondition);
			setCore(_if._then(), wrap(target));
		}

		return setter;
	}

	public JExpression unwrapCondifiton(final JExpression source)
	{
		return null;
	}

	public JExpression wrapCondifiton(final JExpression source)
	{
		return null;
	}
	
	@Override
	public FieldAccessor create(JExpression targetObject)
	{
		return new Accessor(targetObject);
	}

	class Accessor extends AbstractField.Accessor
	{
		// private final FieldAccessor core;
		private final JFieldRef coreField;

		Accessor(JExpression $target)
		{
			super($target);
			this.coreField = $target.ref(core.getName(false));
		}

		@Override
		public void unsetValues(JBlock body)
		{
			body.assign(coreField, JExpr._null());
		}

		@Override
		public JExpression hasSetValue()
		{
			return coreField.ne(JExpr._null());
		}

		@Override
		public final void toRawValue(JBlock block, JVar $var)
		{
			block.assign($var, $target.invoke(getter));
		}

		@Override
		public final void fromRawValue(JBlock block, String uniqueName, JExpression $var)
		{
			block.invoke($target, setter).arg($var);
		}
	}
}
