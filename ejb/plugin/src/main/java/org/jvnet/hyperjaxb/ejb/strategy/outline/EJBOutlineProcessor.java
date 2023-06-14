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
	private EJBPlugin xjcPlugin;
	protected EJBPlugin getXjcPlugin()
	{
		return xjcPlugin;
	}
	protected void setXjcPlugin(EJBPlugin xjcPlugin)
	{
		this.xjcPlugin = xjcPlugin;
	}
	
	private Logger logger;
	protected Logger getLogger()
	{
		if ( logger == null )
		{
			if ( getXjcPlugin() != null )
				setLogger(getXjcPlugin().getLogger());
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
		if ( getXjcPlugin() != null )
			return getXjcPlugin().isTraceEnabled();
		else
			return getLogger().isTraceEnabled();
	}
	
	public boolean isDebugEnabled()
	{
		if ( getXjcPlugin() != null )
			return getXjcPlugin().isDebugEnabled();
		else
			return getLogger().isDebugEnabled();
	}
	
	public boolean isInfoEnabled()
	{
		if ( getXjcPlugin() != null )
			return getXjcPlugin().isInfoEnabled();
		else
			return getLogger().isInfoEnabled();
	}
	
	public boolean isWarnEnabled()
	{
		if ( getXjcPlugin() != null )
			return getXjcPlugin().isWarnEnabled();
		else
			return getLogger().isWarnEnabled();
	}
	
	public boolean isErrorEnabled()
	{
		if ( getXjcPlugin() != null )
			return getXjcPlugin().isErrorEnabled();
		else
			return getLogger().isErrorEnabled();
	}
	
	public void trace(String msg, Object... args)
	{
		if ( getXjcPlugin() != null )
			getXjcPlugin().trace(msg, args);
		else
			getLogger().trace(msg, args);
	}

	public void debug(String msg, Object... args)
	{
		if ( getXjcPlugin() != null )
			getXjcPlugin().debug(msg, args);
		else
			getLogger().debug(msg, args);
	}

	public void info(String msg, Object... args)
	{
		if ( getXjcPlugin() != null )
			getXjcPlugin().info(msg, args);
		else
			getLogger().info(msg, args);
	}

	public void warn(String msg, Object... args)
	{
		if ( getXjcPlugin() != null )
			getXjcPlugin().warn(msg, args);
		else
			getLogger().warn(msg, args);
	}

	public void error(String msg, Object... args)
	{
		if ( getXjcPlugin() != null )
			getXjcPlugin().error(msg, args);
		else
			getLogger().error(msg, args);
	}
}
