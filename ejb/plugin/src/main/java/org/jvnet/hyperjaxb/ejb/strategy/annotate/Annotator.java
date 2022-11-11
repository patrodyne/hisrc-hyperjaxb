package org.jvnet.hyperjaxb.ejb.strategy.annotate;

import org.jvnet.basicjaxb_annox.model.XAnnotationFieldVisitor;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;

public class Annotator extends
		org.jvnet.hyperjaxb_annox.plugin.annotate.Annotator {

	@Override
	protected XAnnotationFieldVisitor<?> createAnnotationFieldVisitor(
			JCodeModel codeModel, JAnnotationUse annotationUse) {
		return new AnnotatingVisitor(codeModel, annotationUse);
	}

}
