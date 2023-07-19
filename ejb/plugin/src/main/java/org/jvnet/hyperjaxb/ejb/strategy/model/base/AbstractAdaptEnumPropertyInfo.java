package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static java.util.Collections.singletonList;
import static org.jvnet.hyperjaxb.jpa.Customizations.markIgnored;

import java.util.Collection;

import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.FieldOutline;

public abstract class AbstractAdaptEnumPropertyInfo extends AbstractAdaptPropertyInfo
{
	protected abstract FieldOutline generateField(final CPropertyInfo core, ClassOutlineImpl classOutline, CPropertyInfo propertyInfo);
	
	@Override
	public final PropertyKind getDefaultGeneratedPropertyKind(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return PropertyKind.ATTRIBUTE;
	}

	@Override
	public String getDefaultGeneratedPropertyName(ProcessModel context, CPropertyInfo propertyInfo)
	{
		return propertyInfo.getName(true) + "Item";
	}

	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, final CPropertyInfo core)
	{
		final CPropertyInfo newPropertyInfo = createPropertyInfo(context, core);
		newPropertyInfo.realization = new FieldRenderer()
		{
			@Override
			public FieldOutline generate(ClassOutlineImpl classOutline, CPropertyInfo propertyInfo)
			{
				return generateField(core, classOutline, propertyInfo);
			}
		};
		
		markIgnored(core);
		return singletonList(newPropertyInfo);
	}
}
