package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;
import static org.jvnet.hyperjaxb.ejb.Constants.TODO_LOG_LEVEL;

import java.util.Collection;
import java.util.Collections;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.TypeUse;

public abstract class AbstractWrapBuiltin implements CreatePropertyInfos
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }

	protected abstract Collection<CPropertyInfo> wrapAnyType(ProcessModel context, CPropertyInfo propertyInfo);

	public abstract CBuiltinLeafInfo getTypeUse(ProcessModel context, CPropertyInfo propertyInfo);

	public abstract CreatePropertyInfos getCreatePropertyInfos(ProcessModel context, CPropertyInfo propertyInfo);
	
	@Override
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		setPlugin(context.getPlugin());
		
		// Single Builtin
		// assert !propertyInfo.isCollection();
		Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		assert types.size() == 1;
		CTypeInfo type = types.iterator().next();
		assert (type instanceof CBuiltinLeafInfo) || (type instanceof CElementInfo);
		final CBuiltinLeafInfo originalTypeUse = getTypeUse(context, propertyInfo);
		if (propertyInfo.getAdapter() != null)
		{
			if ( getPlugin().isTraceEnabled() && propertyInfo.parent() instanceof CClassInfo )
			{
				CClassInfo parent = (CClassInfo) propertyInfo.parent();
				getPlugin().trace("{}, {}: class={}, property={}; not wrapping a property info adapter",
					toLocation(propertyInfo), getClass().getSimpleName(),
					parent.shortName, propertyInfo.getName(false));
			}
			return Collections.emptyList();
		}
		else if (originalTypeUse == CBuiltinLeafInfo.DATA_HANDLER)
		{
			// TODO #42
			todo("Data handler is currently not supported. See issue #88 (http://java.net/jira/browse/HYPERJAXB3-88).");
			return Collections.emptyList();
		}
		else if (originalTypeUse == CBuiltinLeafInfo.ANYTYPE)
		{
			return wrapAnyType(context, propertyInfo);
		}
		else
		{
			final TypeUse adaptedTypeUse = context.getAdaptBuiltinTypeUse().process(context, propertyInfo);
			if (adaptedTypeUse == null)
			{
				todo("Unsupported builtin type ["	+ originalTypeUse.getTypeName() + "] in property ["
					+ propertyInfo.getName(true) + "].");
				return Collections.emptyList();
			}
			else
			{
				final CreatePropertyInfos createPropertyInfos = getCreatePropertyInfos(context, propertyInfo);
				final Collection<CPropertyInfo> newPropertyInfos = createPropertyInfos.process(context, propertyInfo);
				// Customizations.markIgnored(propertyInfo);
				return newPropertyInfos;
			}
		}
	}

	protected void todo(String comment)
	{
		String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
		String level = System.getProperty(TODO_LOG_LEVEL);
		if ("DEBUG".equalsIgnoreCase(level))
			getPlugin().debug(msg);
		else if ("INFO".equalsIgnoreCase(level))
			getPlugin().info(msg);
		else if ("WARN".equalsIgnoreCase(level))
			getPlugin().warn(msg);
		else if ("ERROR".equalsIgnoreCase(level))
			getPlugin().error(msg);
		else
			getPlugin().error(msg);
	}
}
