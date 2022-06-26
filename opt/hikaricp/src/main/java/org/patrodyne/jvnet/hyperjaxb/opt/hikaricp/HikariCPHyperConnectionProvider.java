package org.patrodyne.jvnet.hyperjaxb.opt.hikaricp;

import static org.hibernate.cfg.AvailableSettings.DEFAULT_CATALOG;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_SCHEMA;
import static org.hibernate.hikaricp.internal.HikariConfigurationUtil.CONFIG_PREFIX;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;

/**
 * A hyper HikariCPConnectionProvider supporting additional configuration properties.
 * 
 * This implementation maps additional properties from the Hibernate configuration
 * to the HikariCP configuration: catalog, schema, etc.
 * 
 * @see org.hibernate.hikaricp.internal.HikariCPConnectionProvider
 *
 * @author Rick O'Sullivan
 */
public class HikariCPHyperConnectionProvider extends HikariCPConnectionProvider
{
	private static final long serialVersionUID = 20220501L;

	/**
	 * Configure HikariCP connection provider with Hibernate JPA settings
	 * plus additional mappings.
	 * 
	 * @param props A map of hibernate property settings.
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void configure(Map props) throws HibernateException
	{
		putProperty(props, DEFAULT_CATALOG, "catalog");
		putProperty(props, DEFAULT_SCHEMA, "schema");
		super.configure(props);
	}

	private static void putProperty(Map<String, Object> props, String srcKey, String dstKey)
	{
		String hikariKey = CONFIG_PREFIX + dstKey;
		if ( props.containsKey(srcKey) && !props.containsKey(hikariKey) )
			props.put(hikariKey, props.get(srcKey));
	}
}
