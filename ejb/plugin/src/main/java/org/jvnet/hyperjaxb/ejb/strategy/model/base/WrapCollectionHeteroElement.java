package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap.JavaType.Hetero;
import static org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap.Plurality.Collection;
import static org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap.SchemaType.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.glassfish.jaxb.core.v2.model.core.ID;
import org.jvnet.basicjaxb.util.CustomizationUtils;
import org.jvnet.basicjaxb.util.FieldAccessorUtils;
import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb.item.Item;
import org.jvnet.hyperjaxb.jpa.Customizations;
import org.jvnet.hyperjaxb.jpa.GeneratedProperty;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.SingleField;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.WrappedCollectionField;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.WrappingCollectionField;
import org.jvnet.hyperjaxb.xjc.model.CTypeInfoUtils;
import org.w3c.dom.Element;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.LocalScoping;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

/**
 * <p>Implementation to create a {@link Collection} of {@link CPropertyInfo}s for the
 * given {@link CPropertyInfo} instance in the given {@link ProcessModel} context.</p>
 * 
 * <p>Wrap a heterogeneous element property in a collection.</p>
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapCollectionHeteroElement implements CreatePropertyInfos
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, final CPropertyInfo propertyInfo)
	{
		setPlugin(context.getPlugin());
		
		assert propertyInfo instanceof CElementPropertyInfo;
		final CElementPropertyInfo wrappedPropertyInfo = (CElementPropertyInfo) propertyInfo;
		final Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, wrappedPropertyInfo);
		final CTypeInfo commonBaseTypeInfo = CTypeInfoUtils.getCommonBaseTypeInfo(types);
		if (commonBaseTypeInfo != null)
		{
			// We have a common base type here, no wrapping is required.
			return Collections.<CPropertyInfo> emptyList();
		}
		final String propertyName = wrappedPropertyInfo.getName(true);
		final CClassInfo classInfo = (CClassInfo) wrappedPropertyInfo.parent();
		
		getPlugin().debug("{}, WrapCollectionHeteroElement: class={}, property={}, <{},{},{}>.",
			toLocation(propertyInfo), classInfo.shortName, propertyName, Collection, Hetero, Element);
		
		final CClassInfoParent parent = Ring.get(BGMBuilder.class).getGlobalBinding()
			.getFlattenClasses() == LocalScoping.NESTED ? classInfo : classInfo.parent();
		final CClassInfo itemClassInfo = new CClassInfo(classInfo.model, parent,
			classInfo.shortName + propertyName + "Item", null, new QName(propertyName), null,
			propertyInfo.getSchemaComponent(), new CCustomizations());
		Customizations.markGenerated(itemClassInfo);
		final CElementPropertyInfo itemPropertyInfo = new CElementPropertyInfo("Item", CollectionMode.NOT_REPEATED,
			wrappedPropertyInfo.id(), wrappedPropertyInfo.getExpectedMimeType(),
			wrappedPropertyInfo.getSchemaComponent(),
			new CCustomizations(CustomizationUtils.getCustomizations(wrappedPropertyInfo)),
			wrappedPropertyInfo.getLocator(), wrappedPropertyInfo.isRequired());
		itemPropertyInfo.getTypes().addAll(context.getGetTypes().getTypes(context, wrappedPropertyInfo));
		itemClassInfo.addProperty(itemPropertyInfo);
		context.getProcessClassInfo().process(context, itemClassInfo);
		itemPropertyInfo.realization = new ItemFieldRenderer(propertyInfo);
		final GeneratedProperty generatedProperty = context.getCustomizing().getGeneratedProperty(propertyInfo);
		final String wrappingPropertyName = (generatedProperty == null || generatedProperty.getPropertyName() == null)
			? (propertyName + "Items")
			: generatedProperty .getPropertyName();
		final QName wrappingPropertyQName = (generatedProperty == null || generatedProperty.getPropertyQName() == null)
			? new QName(propertyName + "Items")
			: generatedProperty.getPropertyQName();
		final CCustomizations wrappingPropertyCustomizations;
		if (generatedProperty != null && !generatedProperty.getAny().isEmpty())
		{
			final Collection<CPluginCustomization> cPluginCustomizations = new ArrayList<CPluginCustomization>(
				generatedProperty.getAny().size());
			for (Element element : generatedProperty.getAny())
				cPluginCustomizations.add(CustomizationUtils.createCustomization(element, propertyInfo.getLocator()));
			wrappingPropertyCustomizations = new CCustomizations(cPluginCustomizations);
		}
		else
			wrappingPropertyCustomizations = new CCustomizations();
		final CElementPropertyInfo wrappingPropertyInfo = new CElementPropertyInfo(wrappingPropertyName,
			CollectionMode.REPEATED_ELEMENT, ID.NONE, wrappedPropertyInfo.getExpectedMimeType(), null,
			wrappingPropertyCustomizations, null, false);
		wrappingPropertyInfo.getTypes().add(new CTypeRef(itemClassInfo, wrappingPropertyQName, null, false, null));
		wrappingPropertyInfo.realization = new FieldRenderer()
		{
			@Override
			public FieldOutline generate(ClassOutlineImpl outline, CPropertyInfo wrappingPropertyInfo)
			{
				return new WrappingCollectionField(outline, wrappedPropertyInfo, wrappingPropertyInfo);
			}
		};
		wrappedPropertyInfo.realization = new FieldRenderer()
		{
			@Override
			public FieldOutline generate(ClassOutlineImpl outline, CPropertyInfo wrappedPropertyInfo)
			{
				return new WrappedCollectionField(outline, wrappedPropertyInfo, wrappingPropertyInfo);
			}
		};
		Customizations.markIgnored(wrappedPropertyInfo);
		Customizations.markGenerated(wrappingPropertyInfo);
		final List<CPropertyInfo> propertyInfos = new ArrayList<CPropertyInfo>(2);
		propertyInfos.add(wrappingPropertyInfo);
		propertyInfos.add(itemPropertyInfo);
		return propertyInfos;
	}

	private class ItemFieldRenderer implements FieldRenderer
	{
		// private final CPropertyInfo core;
		public ItemFieldRenderer(final CPropertyInfo core)
		{
			super();
			// this.core = core;
		}

		@Override
		public FieldOutline generate(ClassOutlineImpl classOutline, CPropertyInfo propertyInfo)
		{
			// final FieldOutline coreFieldOutline =
			// classOutline.parent().getField(core);
			final FieldOutline fieldOutline = new SingleField(classOutline, propertyInfo)
			{
				// @Override
				// protected JType getType(CPropertyInfo prop, Aspect aspect) {
				// return coreFieldOutline.getRawType().boxify();
				// }
				// @Override
				// protected JType getType(CPropertyInfo prop, Aspect aspect) {
				// return super.getType(prop, aspect);
				// }
				@Override
				protected String getGetterMethod()
				{
					return "get" + prop.getName(true);
				}
			};
			final JClass itemClass = classOutline.implClass.owner().ref(Item.class)
				.narrow(fieldOutline.getRawType().boxify());
			classOutline.implClass._implements(itemClass);
			if (classOutline.parent().getModel().serializable)
				classOutline.implClass._implements(Serializable.class);
			final JMethod isGetter = FieldAccessorUtils.getter(fieldOutline);
			if (isGetter.name().startsWith("is"))
			{
				final JMethod getter = classOutline.implClass.method(JMod.PUBLIC, isGetter.type(),
					"get" + isGetter.name().substring(2));
				getter.body()._return(JExpr._this().invoke(isGetter));
			}
			return fieldOutline;
		}
	}
}
