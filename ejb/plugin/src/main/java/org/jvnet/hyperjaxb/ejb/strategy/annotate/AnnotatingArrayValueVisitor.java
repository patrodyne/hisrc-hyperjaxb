package org.jvnet.hyperjaxb.ejb.strategy.annotate;

import org.jvnet.basicjaxb_annox.model.annotation.value.XStringAnnotationValue;
import org.jvnet.hyperjaxb.xsd.util.StringUtils;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JCodeModel;

public class AnnotatingArrayValueVisitor extends
		org.jvnet.hyperjaxb_annox.plugin.annotate.AnnotatingArrayValueVisitor {

	private JAnnotationArrayMember annotationArrayMember;

	public AnnotatingArrayValueVisitor(JCodeModel codeModel,
			JAnnotationArrayMember annotationArrayMember) {
		super(codeModel, annotationArrayMember);
		this.annotationArrayMember = annotationArrayMember;
	}

	@Override
	public JAnnotationArrayMember visit(XStringAnnotationValue value) {
		return annotationArrayMember.param(StringUtils.normalizeString(value
				.getValue()));

	}

}
