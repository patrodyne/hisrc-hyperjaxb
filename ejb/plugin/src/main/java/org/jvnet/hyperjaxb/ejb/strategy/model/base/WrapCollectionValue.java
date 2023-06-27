package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap.JavaType.BuiltIn;
import static org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap.Plurality.Collection;
import static org.jvnet.hyperjaxb.ejb.strategy.model.base.ModelWrap.SchemaType.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
import org.jvnet.hyperjaxb.xjc.generator.bean.field.WrappedCollectionField;
import org.jvnet.hyperjaxb.xjc.generator.bean.field.WrappingCollectionField;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.field.FieldRenderer;
import com.sun.tools.xjc.generator.bean.field.SingleField;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.Aspect;
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
 * <p>Wrap a simple homogeneous property value in a collection.</p>
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class WrapCollectionValue implements CreatePropertyInfos
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, final CPropertyInfo propertyInfo)
	{
		setPlugin(context.getPlugin());
		
		assert propertyInfo instanceof CValuePropertyInfo;
		final CValuePropertyInfo wrappedPropertyInfo = (CValuePropertyInfo) propertyInfo;
		final CClassInfo classInfo = (CClassInfo) wrappedPropertyInfo.parent();
		final String propertyName = wrappedPropertyInfo.getName(true);

		final CClassInfoParent parent =
			(Ring.get(BGMBuilder.class).getGlobalBinding().getFlattenClasses() == LocalScoping.NESTED)
			? classInfo
			: classInfo.parent();

		final CClassInfo itemClassInfo = new CClassInfo
		(
			classInfo.model,
			parent,
			classInfo.shortName + propertyName + "Item",
			null,
			new QName(propertyName),
			null,
			propertyInfo.getSchemaComponent(),
			new CCustomizations()
		);

		Customizations.markGenerated(itemClassInfo);

		final CElementPropertyInfo itemPropertyInfo = new CElementPropertyInfo
		(
			"Item",
			CollectionMode.NOT_REPEATED,
			ID.NONE,
			wrappedPropertyInfo.getExpectedMimeType(),
			wrappedPropertyInfo.getSchemaComponent(),
			new CCustomizations(CustomizationUtils.getCustomizations(wrappedPropertyInfo)),
			wrappedPropertyInfo.getLocator(),
			false
		);
		
		final CTypeRef typeRef = new CTypeRef
		(
			context.getGetTypes().getTarget(context, wrappedPropertyInfo),
			new QName(propertyName),
			wrappedPropertyInfo.getSchemaType(),
			false,
			null
		);

		itemPropertyInfo.getTypes().add(typeRef);
		if (wrappedPropertyInfo.getAdapter() != null) 
			itemPropertyInfo.setAdapter(wrappedPropertyInfo.getAdapter());

		final FieldRenderer fieldRenderer = new FieldRenderer()
		{
			@Override
			public FieldOutline generate(ClassOutlineImpl classOutline,	CPropertyInfo propertyInfo)
			{
				final FieldOutline fieldOutline = new SingleField(classOutline, propertyInfo)
				{
					@Override
					protected String getGetterMethod()
					{
						return "get" + prop.getName(true);
					}

					@Override
					protected JType getType(Aspect aspect)
					{
						return super.getType(aspect).boxify();
					}
				};

				final JClass itemClass = classOutline.implClass.owner()
					.ref(Item.class).narrow(fieldOutline.getRawType().boxify());
				
				classOutline.implClass._implements(itemClass);
				if (classOutline.parent().getModel().serializable)
					classOutline.implClass._implements(Serializable.class);

				final JMethod isGetter = FieldAccessorUtils.getter(fieldOutline);

				if (isGetter.name().startsWith("is"))
				{
					final JMethod getter = classOutline.implClass.method
					(
						JMod.PUBLIC,
						isGetter.type(),
						"get" + isGetter.name().substring(2)
					);
					getter.body()._return(JExpr._this().invoke(isGetter));
				}

				return fieldOutline;
			}
		};

		itemPropertyInfo.realization = fieldRenderer;
		itemClassInfo.addProperty(itemPropertyInfo);

		context.getProcessClassInfo().process(context, itemClassInfo);

		final CElementPropertyInfo wrappingPropertyInfo =
			new CElementPropertyInfo
			(
				propertyName + "Items",
				CollectionMode.REPEATED_ELEMENT,
				ID.NONE,
				wrappedPropertyInfo.getExpectedMimeType(),
				null,
				new CCustomizations(),
				null,
				false
			);

//		for (final CTypeRef typeRef : wrappedPropertyInfo.getTypes())
//		{
			wrappingPropertyInfo.getTypes().add
			(
				new CTypeRef
				(
					itemClassInfo,
					new QName
					(
						typeRef.getTagName().getNamespaceURI(),
						typeRef.getTagName().getLocalPart() + "Items"
					),
					null,
					false,
					null
				)
			);
//		}

		// WRAPPING
		wrappingPropertyInfo.realization = new FieldRenderer()
		{
			@Override
			public FieldOutline generate(ClassOutlineImpl outline, CPropertyInfo wrappingPropertyInfo)
			{
				return new WrappingCollectionField(outline, wrappedPropertyInfo, wrappingPropertyInfo);
			}
		};

		// WRAPPED
		wrappedPropertyInfo.realization = new FieldRenderer()
		{
			@Override
			public FieldOutline generate(ClassOutlineImpl outline, CPropertyInfo wrappedPropertyInfo)
			{
				return new WrappedCollectionField(outline, wrappedPropertyInfo, wrappingPropertyInfo);
			}
		};

		Customizations.markGenerated(wrappingPropertyInfo);
		Customizations.markIgnored(wrappedPropertyInfo);

		final List<CPropertyInfo> propertyInfoList = new ArrayList<CPropertyInfo>(1);
		propertyInfoList.add(wrappingPropertyInfo);
		propertyInfoList.add(itemPropertyInfo);
		
		getPlugin().debug("{}, WrapCollectionValue: class={}, property={}, <{},{},{}>.",
			toLocation(propertyInfo), classInfo.shortName, propertyName, Collection, BuiltIn, Value);

		return propertyInfoList;
	}
}
