package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import org.jvnet.hyperjaxb.xml.bind.JAXBContextUtils;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;

public class SingleMarshallingField extends AbstractWrappingField
{
	private final JExpression contextPath;

	public SingleMarshallingField(ClassOutlineImpl context, CPropertyInfo prop, CPropertyInfo core, String contextPath,
		boolean _final)
	{
		super(context, prop, core);
		this.contextPath = context.implClass.field(JMod.PUBLIC | JMod.STATIC | (_final ? JMod.FINAL : JMod.NONE),
			String.class, prop.getName(true) + "ContextPath", JExpr.lit(contextPath));
	}

	@Override
	public JExpression wrapCondition(JExpression source)
	{
		return source.ne(JExpr._null());
	}
	
	@Override
	public JExpression unwrapCondition(JExpression source)
	{
		final JExpression isElement = codeModel.ref(JAXBContextUtils.class)
			.staticInvoke("isMarshallable")
			.arg(contextPath).arg(source);
		return isElement;
	}

	@Override
	public JExpression unwrapElse(final JExpression source)
	{
		return codeModel.ref(JAXBContextUtils.class).staticInvoke("escalateNotNull")
			.arg(source).arg("Check property base type. Cannot marshall: ");
	}
	
	@Override
	protected JExpression wrap(final JExpression target)
	{
		return codeModel.ref(JAXBContextUtils.class).staticInvoke("unmarshal").arg(contextPath).arg(target);
	}

	@Override
	protected JExpression unwrap(JExpression source)
	{
		return codeModel.ref(JAXBContextUtils.class).staticInvoke("marshal").arg(contextPath).arg(source);
	}
}
