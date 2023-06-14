package org.jvnet.hyperjaxb.ejb.jpa.plugin;

import static java.lang.String.format;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;

/**
 * An XJC plugin to add JPA annotations to JAXB generated classes.
 */
public class JPAPlugin extends EJBPlugin
{
	/** Name of Option to enable this plugin. */
	private static final String OPTION_NAME = "Xhyperjaxb-jpa";
	
	/** Description of Option to enable this plugin. */
	private static final String OPTION_DESC = "add JPA annotations to JAXB generated classes";
	
	@Override
	public String getOptionName()
	{
		return OPTION_NAME;
	}

	@Override
	public String getUsage()
	{
		return format(USAGE_FORMAT, OPTION_NAME, OPTION_DESC);
	}
}
