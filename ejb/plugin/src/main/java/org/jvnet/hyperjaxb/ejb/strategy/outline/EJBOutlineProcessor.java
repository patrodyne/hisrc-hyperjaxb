package org.jvnet.hyperjaxb.ejb.strategy.outline;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base for sub-classes that implement {@link OutlineProcessor} for
 * the {@link EJBPlugin} type. This base provides logging methods.  
 */
public abstract class EJBOutlineProcessor implements OutlineProcessor<EJBPlugin>
{
	private EJBPlugin plugin;
	public EJBPlugin getPlugin() { return plugin; }
	public void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	private Logger logger;
	protected Logger getLogger()
	{
		if ( logger == null )
		{
			if ( getPlugin() != null )
				setLogger(getPlugin().getLogger());
			else
				setLogger(LoggerFactory.getLogger(getClass()));
		}
		return logger;
	}
	protected void setLogger(Logger logger)
	{
		this.logger = logger;
	}
	
	public boolean isTraceEnabled()
	{
		if ( getPlugin() != null )
			return getPlugin().isTraceEnabled();
		else
			return getLogger().isTraceEnabled();
	}
	
	public boolean isDebugEnabled()
	{
		if ( getPlugin() != null )
			return getPlugin().isDebugEnabled();
		else
			return getLogger().isDebugEnabled();
	}
	
	public boolean isInfoEnabled()
	{
		if ( getPlugin() != null )
			return getPlugin().isInfoEnabled();
		else
			return getLogger().isInfoEnabled();
	}
	
	public boolean isWarnEnabled()
	{
		if ( getPlugin() != null )
			return getPlugin().isWarnEnabled();
		else
			return getLogger().isWarnEnabled();
	}
	
	public boolean isErrorEnabled()
	{
		if ( getPlugin() != null )
			return getPlugin().isErrorEnabled();
		else
			return getLogger().isErrorEnabled();
	}
	
	public void trace(String msg, Object... args)
	{
		if ( getPlugin() != null )
			getPlugin().trace(msg, args);
		else
			getLogger().trace(msg, args);
	}

	public void debug(String msg, Object... args)
	{
		if ( getPlugin() != null )
			getPlugin().debug(msg, args);
		else
			getLogger().debug(msg, args);
	}

	public void info(String msg, Object... args)
	{
		if ( getPlugin() != null )
			getPlugin().info(msg, args);
		else
			getLogger().info(msg, args);
	}

	public void warn(String msg, Object... args)
	{
		if ( getPlugin() != null )
			getPlugin().warn(msg, args);
		else
			getLogger().warn(msg, args);
	}

	public void error(String msg, Object... args)
	{
		if ( getPlugin() != null )
			getPlugin().error(msg, args);
		else
			getLogger().error(msg, args);
	}
}
