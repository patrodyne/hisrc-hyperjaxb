package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleWrappingField;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.outline.FieldOutline;

public class AdaptSingleBuiltinNonReference extends AbstractAdaptBuiltinPropertyInfo
{
	public AdaptSingleBuiltinNonReference(TypeUse propertyType)
	{
		super(propertyType);
	}

	@Override
	protected FieldOutline generateField(final ProcessModel context, CPropertyInfo core,
		ClassOutlineImpl classOutline, CPropertyInfo propertyInfo)
	{
		final SingleWrappingField fieldOutline = new SingleWrappingField(classOutline, propertyInfo, core);
		fieldOutline.generateAccessors();
		
		return fieldOutline;
	}
}
