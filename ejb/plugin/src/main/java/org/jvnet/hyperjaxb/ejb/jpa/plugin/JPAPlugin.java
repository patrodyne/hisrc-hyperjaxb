package org.jvnet.hyperjaxb.ejb.jpa.plugin;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;

public class JPAPlugin extends EJBPlugin
{
	public String getOptionName()
	{
		return "Xhyperjaxb-jpa";
	}

	public String getUsage()
	{
		return "  -Xhyperjaxb-jpa: Hyperjaxb JPA plugin";
	}
}
