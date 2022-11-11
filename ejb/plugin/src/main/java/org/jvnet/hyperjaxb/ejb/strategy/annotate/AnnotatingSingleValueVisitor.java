package org.jvnet.hyperjaxb.ejb.strategy.annotate;

import org.jvnet.basicjaxb_annox.model.annotation.value.XStringAnnotationValue;
import org.jvnet.hyperjaxb.xsd.util.StringUtils;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;

public class AnnotatingSingleValueVisitor extends
		org.jvnet.hyperjaxb_annox.plugin.annotate.AnnotatingSingleValueVisitor {

	private String name;
	private final JAnnotationUse annotationUse;

	public AnnotatingSingleValueVisitor(JCodeModel codeModel, String name,
			JAnnotationUse annotationUse) {
		super(codeModel, name, annotationUse);
		this.name = name;
		this.annotationUse = annotationUse;
	}

	@Override
	public JAnnotationUse visit(XStringAnnotationValue value) {
		return annotationUse.param(this.name,
				StringUtils.normalizeString(value.getValue()));
	}

}
