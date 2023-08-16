package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;

public class StringField extends SingleWrappingField {

	public StringField(ClassOutlineImpl context, CPropertyInfo prop,
			CReferencePropertyInfo core) {
		super(context, prop, core);
	}

	@Override
	public JExpression unwrapCondition(JExpression source) {
		return source._instanceof(codeModel.ref(String.class));
	}

	@Override
	public JExpression wrapCondition(JExpression source) {
		return source.ne(JExpr._null());
	}

	@Override
	protected JExpression unwrap(JExpression source) {
		return super.unwrap(JExpr.cast(codeModel.ref(String.class), source));
	}

}
