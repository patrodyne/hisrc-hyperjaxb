package org.jvnet.hyperjaxb.xjc.adapters;

import java.util.List;

import javax.xml.namespace.QName;

import org.jvnet.hyperjaxb.codemodel.util.JExprUtils;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XmlAdapterUtils;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;

public class XmlAdapterXjcUtils
{
	public static JExpression isJAXBElement(JCodeModel codeModel, JClass declaredType, QName name, JClass scope,
		JExpression value)
	{
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("isJAXBElement")
			.arg(declaredType.dotclass())
			.arg(JExprUtils.newQName(codeModel, name))
			.arg(scope.dotclass())
			.arg(value);
	}

	public static JExpression marshall(JCodeModel codeModel, JClass xmlAdapterClass, JExpression value)
	{
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("marshall")
			.arg(xmlAdapterClass.dotclass())
			.arg(value);
	}

	public static JExpression marshall(JCodeModel codeModel, JExpression value)
	{
		return value;
	}

	public static JExpression marshallJAXBElement(JCodeModel codeModel, JClass declaredType, QName name, JClass scope,
		JExpression value)
	{
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("marshallJAXBElement")
			.arg(declaredType.dotclass())
			.arg(JExprUtils.newQName(codeModel, name))
			.arg(scope.dotclass())
			.arg(value);
	}

	public static JExpression marshallJAXBElement(JCodeModel codeModel, JClass xmlAdapterClass, JClass declaredType,
		QName name, JClass scope, JExpression value)
	{
		JClass xae = xmlAdapterClass._extends();
		List<JClass> xaep = xae.getTypeParameters();
		if ( xaep.size() > 1 )
		{
			String dtfn = declaredType.fullName();
			String btfn = xaep.get(1).fullName();
			if ( btfn.equals(dtfn) )
				return marshallJAXBElement(codeModel, declaredType, name, scope, value);
		}
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("marshallJAXBElement")
			.arg(xmlAdapterClass.dotclass())
			.arg(declaredType.dotclass())
			.arg(JExprUtils.newQName(codeModel, name))
			.arg(scope.dotclass())
			.arg(value);
	}

	public static JExpression unmarshall(JCodeModel codeModel, JExpression value)
	{
		return value;
	}

	public static JExpression unmarshall(JCodeModel codeModel, JClass xmlAdapterClass, JExpression value)
	{
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("unmarshall")
			.arg(xmlAdapterClass.dotclass())
			.arg(value);
	}

	public static JExpression unmarshallJAXBElement(JCodeModel codeModel, JClass xmlAdapterClass, JExpression value)
	{
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("unmarshallJAXBElement")
			.arg(xmlAdapterClass.dotclass())
			.arg(value);
	}

	public static JExpression unmarshallJAXBElement(JCodeModel codeModel, JExpression value)
	{
		final JExpression argument = value;
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("unmarshallJAXBElement")
			.arg(argument);
	}

	public static JExpression unmarshallSource(JCodeModel codeModel, JType declaredType, JExpression source)
	{
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("unmarshallSource")
			.arg(declaredType.boxify().dotclass())
			.arg(source);
	}

	public static JExpression unmarshallSource(JCodeModel codeModel, JClass xmlAdapterClass, JType declaredType, JExpression source)
	{
		JClass xae = xmlAdapterClass._extends();
		List<JClass> xaep = xae.getTypeParameters();
		if ( xaep.size() > 1 )
		{
			String dtfn = declaredType.fullName();
			String btfn = xaep.get(1).fullName();
			if ( btfn.equals(dtfn) )
				return unmarshallSource(codeModel, declaredType, source);
		}
		return codeModel.ref(XmlAdapterUtils.class).staticInvoke("unmarshallSource")
			.arg(xmlAdapterClass.dotclass())
			.arg(declaredType.boxify().dotclass())
			.arg(source);
	}
}
