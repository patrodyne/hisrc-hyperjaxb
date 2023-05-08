package org.jvnet.hyperjaxb.ejb.jpa.plugin;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;

public class JPAPlugin extends EJBPlugin
{
	@Override
	public String getOptionName()
	{
		return "Xhyperjaxb-jpa";
	}

	@Override
	public String getUsage()
	{
		return "  -Xhyperjaxb-jpa: Hyperjaxb JPA plugin";
	}
}
