package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import java.util.Collection;
import java.util.Collections;

import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Customizations;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.FieldOutline;

public abstract class AbstractAdaptEnumPropertyInfo extends
		AbstractAdaptPropertyInfo {

	@Override
	public final PropertyKind getDefaultGeneratedPropertyKind(ProcessModel context,
			CPropertyInfo propertyInfo) {
		return PropertyKind.ATTRIBUTE;
	}

	@Override
	public String getDefaultGeneratedPropertyName(ProcessModel context,
			CPropertyInfo propertyInfo) {
		return propertyInfo.getName(true) + "Item";
	}

	public Collection<CPropertyInfo> process(ProcessModel context,
			final CPropertyInfo core) {
		final CPropertyInfo newPropertyInfo = createPropertyInfo(context, core);

		newPropertyInfo.realization = new FieldRenderer() {

			public FieldOutline generate(ClassOutlineImpl classOutline,
					CPropertyInfo propertyInfo) {
				return generateField(core, classOutline, propertyInfo);
			}

		};
		Customizations.markIgnored(core);
		return Collections.singletonList(newPropertyInfo);
	}

	protected abstract FieldOutline generateField(final CPropertyInfo core,
			ClassOutlineImpl classOutline, CPropertyInfo propertyInfo);

}