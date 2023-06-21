package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.ArrayList;
import java.util.Collection;

import org.glassfish.jaxb.core.v2.model.core.ID;
import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleWrappingElementField;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.outline.FieldOutline;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapSingleHeteroElement implements CreatePropertyInfos
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		setPlugin(context.getPlugin());
		
		assert propertyInfo instanceof CElementPropertyInfo;
		final CElementPropertyInfo elementPropertyInfo = (CElementPropertyInfo) propertyInfo;
		final Collection<CPropertyInfo> newPropertyInfos = new ArrayList<CPropertyInfo>(
			context.getGetTypes().getTypes(context, elementPropertyInfo).size());
		final Collection<CPropertyInfo> properties = createTypeProperties(context, elementPropertyInfo);
		if (properties != null)
			newPropertyInfos.addAll(properties);
		Customizations.markIgnored(propertyInfo);
		return newPropertyInfos;
	}

	protected Collection<CPropertyInfo> createTypeProperties(final ProcessModel context,
		final CElementPropertyInfo propertyInfo)
	{
		final Collection<? extends CTypeRef> types = context.getGetTypes().getTypes(context, propertyInfo);
		// Set<CElement> elements = propertyInfo.getElements();
		final Collection<CPropertyInfo> properties = new ArrayList<CPropertyInfo>(types.size());
		for (final CTypeRef type : types)
		{
			final CElementPropertyInfo itemPropertyInfo = new CElementPropertyInfo(
				propertyInfo.getName(true) + ((CClassInfo) propertyInfo.parent()).model.getNameConverter()
					.toPropertyName(type.getTagName().getLocalPart()),
				CollectionMode.NOT_REPEATED, ID.NONE, propertyInfo.getExpectedMimeType(),
				propertyInfo.getSchemaComponent(),
				new CCustomizations(CustomizationUtils.getCustomizations(propertyInfo)), propertyInfo.getLocator(),
				false);
			itemPropertyInfo.getTypes().add(type);
			itemPropertyInfo.realization = new FieldRenderer()
			{
				@Override
				public FieldOutline generate(ClassOutlineImpl classOutline, CPropertyInfo p)
				{
					final SingleWrappingElementField field = new SingleWrappingElementField(classOutline, p,
						propertyInfo, type);
					field.generateAccessors();
					return field;
				}
			};
			Customizations.markGenerated(itemPropertyInfo);
			properties.add(itemPropertyInfo);
			Collection<CPropertyInfo> newProperties = context.getProcessPropertyInfos().process(context,
				itemPropertyInfo);
			if (newProperties != null)
				properties.addAll(newProperties);
		}
		return properties;
	}
}
