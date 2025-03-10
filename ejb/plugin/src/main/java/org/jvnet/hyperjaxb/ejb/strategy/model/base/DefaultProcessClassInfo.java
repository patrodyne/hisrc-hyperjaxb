package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static org.jvnet.basicjaxb.util.LocatorUtils.toLocation;

import java.util.Collection;
import java.util.HashSet;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessClassInfo;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@ModelBase
public class DefaultProcessClassInfo implements ProcessClassInfo
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public Collection<CClassInfo> process(ProcessModel context, CClassInfo classInfo)
	{
		setPlugin(context.getPlugin());
		
		final Collection<CPropertyInfo> newProperties = context.getProcessPropertyInfos().process(context, classInfo);
		final Collection<CClassInfo> classes = new HashSet<CClassInfo>(1);
		classes.add(classInfo);
		
		for (CPropertyInfo newProperty : newProperties)
		{
			if (newProperty.parent() == null)
				throw new IllegalStateException("Property [" + newProperty.getName(true) + "] does not have a parent.");
			classes.add((CClassInfo) newProperty.parent());
		}
		classes.addAll(context.getCreateIdClass().process(context, classInfo));
		
		getPlugin().debug("{}, DefaultProcessClassInfo: class={}", toLocation(classInfo), classInfo.shortName);

		return classes;
	}
}
