package org.patrodyne.jvnet.hyperjaxb.opt.hikaricp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hibernate.hikaricp.internal.HikariConfigurationUtil;
import org.jvnet.basicjaxb.reflection.util.FieldAccessor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.util.PropertyElf;

/**
 * Utility methods for a HikariCP pooled JDBC connection provider.
 * 
 * @author Rick O'Sullivan
 */
public class HikariCPUtil
{
	/**
	 * Gather HikariCP configurable properties for the given EntityManagerFactory.
	 * 
	 * @param emf A EntityManagerFactory instance.
	 * 
	 * @return A map of configurable properties with current values.
	 */
	public static Map<String, Object> gatherProperties(EntityManagerFactory emf)
	{
		SessionFactory sf = emf.unwrap(SessionFactory.class);
		return gatherProperties(sf);
	}

	/**
	 * Gather HikariCP configurable properties for the given SessionFactory.
	 * 
	 * @param sf A SessionFactory instance.
	 * 
	 * @return A map of configurable properties with current values.
	 */
	public static Map<String, Object> gatherProperties(SessionFactory sf)
	{
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) sf;
		SessionFactoryOptions sfo = sfi.getSessionFactoryOptions();
		StandardServiceRegistry ssr = sfo.getServiceRegistry();
		ConnectionProvider cnp = ssr.getService(ConnectionProvider.class);
		Map<String, Object> poolProperties = new HashMap<>();
		if ( cnp instanceof HikariCPConnectionProvider )
		{
			// HikariCP Connection Provider
			HikariCPConnectionProvider hcp = (HikariCPConnectionProvider) cnp;
			FieldAccessor<HikariConfig> hcAccessor =
				new FieldAccessor<>(HikariCPConnectionProvider.class, "hcfg", HikariConfig.class);
			HikariConfig hc = hcAccessor.get(hcp);
			Set<String> hcPropertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
			for ( String hcPropertyName : hcPropertyNames )
			{
				Object hcPropertyValue = PropertyElf.getProperty(hcPropertyName, hc);
				String hhcPropertyName = HikariConfigurationUtil.CONFIG_PREFIX + hcPropertyName;
				poolProperties.put(hhcPropertyName, hcPropertyValue);
			}
		}
		return poolProperties;
	}
}
