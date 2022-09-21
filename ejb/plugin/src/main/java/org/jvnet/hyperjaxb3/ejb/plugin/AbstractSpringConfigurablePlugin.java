package org.jvnet.hyperjaxb3.ejb.plugin;

import org.apache.commons.lang3.ArrayUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jvnet.jaxb2_commons.plugin.AbstractParameterizablePlugin;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.Outline;

public abstract class AbstractSpringConfigurablePlugin
	extends AbstractParameterizablePlugin
{
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private AbstractXmlApplicationContext applicationContext;
	public AbstractXmlApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	protected String[] getDefaultConfigLocations()
	{
		return null;
	}

	private String[] configLocations = getDefaultConfigLocations();
	public String[] getConfigLocations()
	{
		return configLocations;
	}
	public void setConfigLocations(String[] configLocations)
	{
		this.configLocations = configLocations;
	}

	protected int getAutowireMode()
	{
		return AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
	}

	protected boolean isDependencyCheck()
	{
		return false;
	}

	public void init(Options options) throws Exception
	{

	}

	@Override
	protected void beforeRun(Outline outline, Options options) throws Exception
	{
		super.beforeRun(outline, options);
		final String[] configLocations = getConfigLocations();
		if (!ArrayUtils.isEmpty(configLocations))
		{
			final String configLocationsString = ArrayUtils.toString(configLocations);
			
			logger.debug("Loading application context from [" + configLocationsString + "].");
			
			try
			{
				applicationContext = new FileSystemXmlApplicationContext(configLocations, false);
				applicationContext.setClassLoader(Thread.currentThread().getContextClassLoader());
				applicationContext.refresh();
				
				if (getAutowireMode() != AutowireCapableBeanFactory.AUTOWIRE_NO)
				{
					applicationContext.getBeanFactory()
						.autowireBeanProperties(this, getAutowireMode(), isDependencyCheck());
				}
			}
			catch (Exception ex)
			{
				String msg = "Error loading applicaion context from [" + configLocationsString + "].";
				logger.error(msg, ex);
				throw new BadCommandLineException(msg, ex);
			}
		}
	}

}
