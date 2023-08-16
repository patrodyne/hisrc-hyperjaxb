package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.outline.Aspect;

public class SingleWrappingElementField extends SingleWrappingField {

	private final CTypeRef typeRef;

	public SingleWrappingElementField(ClassOutlineImpl context,
			CPropertyInfo prop, CPropertyInfo core, CTypeRef typeRef) {
		super(context, prop, core);
		this.typeRef = typeRef;
	}

	public CTypeRef getTypeRef() {
		return typeRef;
	}

	@Override
	public JExpression wrapCondition(JExpression source) {
		return source.ne(JExpr._null());
	}
	
	@Override
	public JExpression unwrapCondition(JExpression source) {
		
		final JType type = getTypeRef().getTarget().toType(outline.parent(),
				Aspect.EXPOSED);
		return JOp._instanceof(source, type);
	}
	
	@Override
	protected JExpression unwrap(JExpression source) {
		final JType type = getTypeRef().getTarget().toType(outline.parent(),
				Aspect.EXPOSED);
		return JExpr.cast(type, super.unwrap(source));
	}

}
