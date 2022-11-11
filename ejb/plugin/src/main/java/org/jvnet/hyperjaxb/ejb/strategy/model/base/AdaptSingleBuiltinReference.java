package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleWrappingReferenceField;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.outline.FieldOutline;

public class AdaptSingleBuiltinReference extends AbstractAdaptBuiltinPropertyInfo {

	public AdaptSingleBuiltinReference(TypeUse type,
			Class<? extends XmlAdapter<?, ?>> adapterClass) {
		super(type, adapterClass);
	}

	public AdaptSingleBuiltinReference(TypeUse propertyType) {
		super(propertyType);
	}

	protected FieldOutline generateField(final ProcessModel context,
			final CPropertyInfo core, ClassOutlineImpl classOutline,
			CPropertyInfo propertyInfo) {
		assert core instanceof CReferencePropertyInfo;
		final CReferencePropertyInfo referencePropertyInfo = (CReferencePropertyInfo) core;
		// referencePropertyInfo.gete
		SingleWrappingReferenceField fieldOutline = new SingleWrappingReferenceField(
				classOutline, propertyInfo, referencePropertyInfo);
		fieldOutline.generateAccessors();
		return fieldOutline;
	}

}
