package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import jakarta.xml.bind.JAXBElement;

import org.jvnet.hyperjaxb.xml.bind.JAXBElementUtils;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.outline.Aspect;

public class JAXBElementValueField extends AbstractWrappingField
{
	@SuppressWarnings("unused")
	private final CPropertyInfo nameProperty;
	
	@SuppressWarnings("unused")
	private final JFieldRef nameField;
	
	private final CNonElement type;

	public JAXBElementValueField(ClassOutlineImpl context, CPropertyInfo prop, CReferencePropertyInfo core,
		CPropertyInfo nameProperty, CNonElement type)
	{
		super(context, prop, core);
		this.nameProperty = nameProperty;
		this.nameField = JExpr.refthis(nameProperty.getName(false));
		this.type = type;
	}

	@Override
	public JExpression unwrapCondition(JExpression source)
	{
		return source._instanceof(codeModel.ref(JAXBElement.class));
	}

	@Override
	public JExpression wrapCondition(JExpression source)
	{
		return source.ne(JExpr._null());
	}

	@Override
	protected JExpression unwrap(JExpression source)
	{
		JType declaredType = type.toType(outline.parent(), Aspect.EXPOSED);
		JClass declaredClass = declaredType.boxify();
		if ( CENTRAL_CASTING )
		{
			return codeModel.ref(JAXBElementUtils.class).staticInvoke("getValue")
				.arg(declaredClass.dotclass())
				.arg(source);
		}
		else
		{
			return codeModel.ref(JAXBElementUtils.class).staticInvoke("getValue")
				.arg(JExpr.cast(codeModel.ref(JAXBElement.class).narrow(declaredClass), source));
		}
	}

	@Override
	protected JExpression wrap(JExpression source)
	{
		final JExpression core = getCore();
		return codeModel.ref(JAXBElementUtils.class).staticInvoke("wrap").arg(core).arg(source);
	}
}
