package org.jvnet.hyperjaxb.opt.hibernate;

import static java.sql.Connection.TRANSACTION_NONE;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;
import static java.sql.Connection.TRANSACTION_SERIALIZABLE;
import static org.hibernate.cache.jcache.MissingCacheStrategy.interpretSetting;
import static org.hibernate.cfg.AvailableSettings.ALLOW_JTA_TRANSACTION_ACCESS;
import static org.hibernate.cfg.AvailableSettings.ALLOW_UPDATE_OUTSIDE_TRANSACTION;
import static org.hibernate.cfg.AvailableSettings.AUTOCOMMIT;
import static org.hibernate.cfg.AvailableSettings.AUTO_CLOSE_SESSION;
import static org.hibernate.cfg.AvailableSettings.AUTO_EVICT_COLLECTION_CACHE;
import static org.hibernate.cfg.AvailableSettings.BATCH_FETCH_STYLE;
import static org.hibernate.cfg.AvailableSettings.BATCH_VERSIONED_DATA;
import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_FACTORY;
import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_PREFIX;
import static org.hibernate.cfg.AvailableSettings.CHECK_NULLABILITY;
import static org.hibernate.cfg.AvailableSettings.CONNECTION_HANDLING;
import static org.hibernate.cfg.AvailableSettings.CONNECTION_PROVIDER;
import static org.hibernate.cfg.AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_BATCH_FETCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_CATALOG;
import static org.hibernate.cfg.AvailableSettings.DEFAULT_SCHEMA;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.FLUSH_BEFORE_COMPLETION;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_CONNECTION;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_DATABASE_ACTION;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_SCRIPTS_ACTION;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_SCRIPTS_CREATE_TARGET;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_SCRIPTS_DROP_TARGET;
import static org.hibernate.cfg.AvailableSettings.INTERCEPTOR;
import static org.hibernate.cfg.AvailableSettings.ISOLATION;
import static org.hibernate.cfg.AvailableSettings.JPAQL_STRICT_COMPLIANCE;
import static org.hibernate.cfg.AvailableSettings.JPA_JDBC_URL;
import static org.hibernate.cfg.AvailableSettings.JPA_JTA_DATASOURCE;
import static org.hibernate.cfg.AvailableSettings.JPA_NON_JTA_DATASOURCE;
import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_MODE;
import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_RETRIEVE_MODE;
import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_STORE_MODE;
import static org.hibernate.cfg.AvailableSettings.JTA_CACHE_TM;
import static org.hibernate.cfg.AvailableSettings.JTA_CACHE_UT;
import static org.hibernate.cfg.AvailableSettings.JTA_PLATFORM;
import static org.hibernate.cfg.AvailableSettings.JTA_PLATFORM_RESOLVER;
import static org.hibernate.cfg.AvailableSettings.JTA_TRACK_BY_THREAD;
import static org.hibernate.cfg.AvailableSettings.MAX_FETCH_DEPTH;
import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
import static org.hibernate.cfg.AvailableSettings.ORDER_INSERTS;
import static org.hibernate.cfg.AvailableSettings.ORDER_UPDATES;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.POOL_SIZE;
import static org.hibernate.cfg.AvailableSettings.PREFER_USER_TRANSACTION;
import static org.hibernate.cfg.AvailableSettings.QUERY_CACHE_FACTORY;
import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME;
import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI;
import static org.hibernate.cfg.AvailableSettings.SESSION_SCOPED_INTERCEPTOR;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_FETCH_SIZE;
import static org.hibernate.cfg.AvailableSettings.STATEMENT_INSPECTOR;
import static org.hibernate.cfg.AvailableSettings.URL;
import static org.hibernate.cfg.AvailableSettings.USER;
import static org.hibernate.cfg.AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES;
import static org.hibernate.cfg.AvailableSettings.USE_GET_GENERATED_KEYS;
import static org.hibernate.cfg.AvailableSettings.USE_IDENTIFIER_ROLLBACK;
import static org.hibernate.cfg.AvailableSettings.USE_MINIMAL_PUTS;
import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_SCROLLABLE_RESULTSET;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
import static org.hibernate.cfg.AvailableSettings.USE_SQL_COMMENTS;
import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;
import static org.hibernate.cfg.AvailableSettings.PERSISTENCE_UNIT_NAME;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.cache.CacheManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.jcache.ConfigSettings;
import org.hibernate.cache.jcache.internal.JCacheRegionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform;
import org.hibernate.engine.transaction.jta.platform.internal.StandardJtaPlatformResolver;
import org.hibernate.jdbc.ReturningWork;
import org.hibernate.jdbc.Work;
import org.hibernate.jpa.internal.util.CacheModeHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.stat.Statistics;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.ActionGrouping;
import org.jvnet.hyperjaxb.ejb.util.TransactionalSql;
import org.jvnet.basicjaxb.reflection.util.FieldAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for a SessionFactory.
 * 
 * @author Rick O'Sullivan
 */
public class SessionFactoryUtil
{
	private static Logger log = LoggerFactory.getLogger(SessionFactoryUtil.class);

	/**
	 * Gather configurable properties from the SessionFactory context for the given EntityManagerFactory.
	 * 
	 * @param emf An EntityManagerFactory instance.
	 * 
	 * @return A map of configurable properties with current values.
	 */
	public static Map<String, Object> gatherProperties(EntityManagerFactory emf)
	{
		SessionFactory sf = emf.unwrap(SessionFactory.class);
		return gatherProperties(sf, emf.getProperties());
	}

	/**
	 * Gather configurable properties from the SessionFactory context for the given EntityManagerFactory.
	 * 
	 * @param emf An EntityManagerFactory instance.
	 * @param emfProperties A map of the initial (or filtered) EntityManagerFactory properties.
	 * 
	 * @return A map of configurable properties with current values.
	 */
	public static Map<String, Object> gatherProperties(EntityManagerFactory emf, Map<String, Object> emfProperties)
	{
		SessionFactory sf = emf.unwrap(SessionFactory.class);
		return gatherProperties(sf, emfProperties);
	}

	/**
	 * Gather the Hibernate configurable SessionFactory properties from an SessionFactory instance.
	 * 
	 * @param sf A Hibernate SessionFactory instance.
	 * @param emfProperties A map of the initial (or filtered) EntityManagerFactory properties.
	 * 
	 * @return A map of properties representing the configurable Hibernate external properties.
	 */
	public static Map<String, Object> gatherProperties(SessionFactory sf, Map<String, Object> emfProperties)
	{
		// Hibernate Session Factory, map Hibernate Configuration Options
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) sf;
		SessionFactoryOptions sfo = sfi.getSessionFactoryOptions();
		StandardServiceRegistry ssr = sfo.getServiceRegistry();

		// Gather password and JPA specified properties.
		Map<String, Object> etc = new HashMap<>();
		etc.put(PASS, sf.getProperties().get(PASS));
		
		if ( emfProperties.containsKey(JPA_SHARED_CACHE_MODE) )
			etc.put(JPA_SHARED_CACHE_MODE, emfProperties.get(JPA_SHARED_CACHE_MODE));
		else
			etc.put(JPA_SHARED_CACHE_MODE, SharedCacheMode.UNSPECIFIED);
		
		if ( emfProperties.containsKey(JPA_SHARED_CACHE_RETRIEVE_MODE) )
			etc.put(JPA_SHARED_CACHE_RETRIEVE_MODE, emfProperties.get(JPA_SHARED_CACHE_RETRIEVE_MODE));
		else
			etc.put(JPA_SHARED_CACHE_RETRIEVE_MODE, CacheModeHelper.DEFAULT_RETRIEVE_MODE);
		
		if ( emfProperties.containsKey(JPA_SHARED_CACHE_STORE_MODE) )
			etc.put(JPA_SHARED_CACHE_STORE_MODE, emfProperties.get(JPA_SHARED_CACHE_STORE_MODE));
		else
			etc.put(JPA_SHARED_CACHE_STORE_MODE, CacheModeHelper.DEFAULT_STORE_MODE);
		
		if ( emfProperties.containsKey(JTA_CACHE_TM) )
			etc.put(JTA_CACHE_TM, emfProperties.get(JTA_CACHE_TM));
		else
			etc.put(JTA_CACHE_TM, true);
		
		if ( emfProperties.containsKey(JTA_CACHE_UT) )
			etc.put(JTA_CACHE_UT, emfProperties.get(JTA_CACHE_UT));
		else
			etc.put(JTA_CACHE_UT, false);

		if ( emfProperties.containsKey(JTA_PLATFORM) )
			etc.put(JTA_PLATFORM, emfProperties.get(JTA_PLATFORM));
		else
			etc.put(JTA_PLATFORM, NoJtaPlatform.class.getName());

		if ( emfProperties.containsKey(JTA_PLATFORM_RESOLVER))
			etc.put(JTA_PLATFORM_RESOLVER, emfProperties.get(JTA_PLATFORM_RESOLVER));
		else
			etc.put(JTA_PLATFORM_RESOLVER, StandardJtaPlatformResolver.class.getName());
		
		if ( emfProperties.containsKey(PERSISTENCE_UNIT_NAME))
			etc.put(PERSISTENCE_UNIT_NAME, emfProperties.get(PERSISTENCE_UNIT_NAME));
		
		if ( !emfProperties.containsKey(HBM2DDL_CONNECTION))
			etc.put(HBM2DDL_CONNECTION, emfProperties.get(JPA_JDBC_URL));
		
		ActionGrouping actionGrouping = SchemaManagementToolCoordinator.ActionGrouping.interpret(emfProperties);

		Action databaseAction = actionGrouping.getDatabaseAction();
		FieldAccessor<String> hbm2dllAutoAccessor = new FieldAccessor<>(Action.class, "externalHbm2ddlName", String.class);
		String hbm2dllAutoValue = hbm2dllAutoAccessor.get(databaseAction);
		etc.put(HBM2DDL_AUTO, hbm2dllAutoValue);
		FieldAccessor<String> hbm2dllDatabaseActionAccessor = new FieldAccessor<>(Action.class, "externalJpaName", String.class);
		String hbm2dllDatabaseActionValue = hbm2dllDatabaseActionAccessor.get(databaseAction);
		etc.put(HBM2DDL_DATABASE_ACTION, hbm2dllDatabaseActionValue);
		
		FieldAccessor<String> hbm2dllScriptsActionAccessor = new FieldAccessor<>(Action.class, "externalJpaName", String.class);
		String hbm2dllScriptsActionValue = hbm2dllScriptsActionAccessor.get(databaseAction);
		etc.put(HBM2DDL_SCRIPTS_ACTION, hbm2dllScriptsActionValue);

		if ( emfProperties.containsKey(HBM2DDL_SCRIPTS_CREATE_TARGET))
			etc.put(HBM2DDL_SCRIPTS_CREATE_TARGET, emfProperties.get(HBM2DDL_SCRIPTS_CREATE_TARGET));
		if ( emfProperties.containsKey(HBM2DDL_SCRIPTS_DROP_TARGET))
			etc.put(HBM2DDL_SCRIPTS_DROP_TARGET, emfProperties.get(HBM2DDL_SCRIPTS_DROP_TARGET));
		
		JdbcServices jdb = ssr.getService(JdbcServices.class);
		etc.put(DEFAULT_CATALOG, jdb.getJdbcEnvironment().getCurrentCatalog());
		etc.put(DEFAULT_SCHEMA, jdb.getJdbcEnvironment().getCurrentSchema());
		etc.put(DIALECT, jdb.getDialect());
		etc.put(FORMAT_SQL, jdb.getSqlStatementLogger().isFormat());
		etc.put(SHOW_SQL, jdb.getSqlStatementLogger().isLogToStdout());
		try ( Session session = sf.openSession() )
		{
			session.doWork(new Work()
			{
			    @Override
			    public void execute(Connection conn) throws SQLException
			    {
			    	if ( !conn.isValid(5) )
			    		log.warn("Connection may not support JDBC4!");
					DatabaseMetaData meta = conn.getMetaData();
					etc.put(URL, meta.getURL());
					etc.put(DRIVER, DriverManager.getDriver(meta.getURL()).getClass().getName());
					etc.put(USER, meta.getUserName());
					etc.put(ISOLATION, meta.getDefaultTransactionIsolation());
					etc.put(AUTOCOMMIT, conn.getAutoCommit());
					
					switch ( meta.getDefaultTransactionIsolation() )
					{
						case TRANSACTION_NONE: etc.put(ISOLATION, "NONE"); break;
						case TRANSACTION_READ_UNCOMMITTED: etc.put(ISOLATION, "READ_UNCOMMITTED"); break;
						case TRANSACTION_READ_COMMITTED: etc.put(ISOLATION, "READ_COMMITTED"); break;
						case TRANSACTION_REPEATABLE_READ: etc.put(ISOLATION, "REPEATABLE_READ"); break;
						case TRANSACTION_SERIALIZABLE: etc.put(ISOLATION, "SERIALIZABLE"); break;
						
					}
					try ( Statement stmt = conn.createStatement() )
					{
						etc.put(STATEMENT_FETCH_SIZE, stmt.getFetchSize());
					}
			    }
			});
		}

		RegionFactory crf = ssr.getService(RegionFactory.class);
		etc.put(CACHE_REGION_FACTORY, crf.getClass().getName());
		etc.put(DEFAULT_CACHE_CONCURRENCY_STRATEGY, crf.getDefaultAccessType().name());
		if ( crf instanceof JCacheRegionFactory )
		{
			FieldAccessor<CacheManager> cacheManagerAccessor = new FieldAccessor<>(JCacheRegionFactory.class, "cacheManager", CacheManager.class);
			CacheManager cacheManagerValue = cacheManagerAccessor.get(crf);
			etc.put(ConfigSettings.PROVIDER, cacheManagerValue.getCachingProvider().getClass().getName());
			etc.put(ConfigSettings.CONFIG_URI, cacheManagerValue.getURI());
			Object mcs = emfProperties.get(ConfigSettings.MISSING_CACHE_STRATEGY);
			etc.put(ConfigSettings.MISSING_CACHE_STRATEGY, interpretSetting(mcs));
		}
		
		// JndiService jndiService = ssr.getService(JndiService.class);
		
		Map<String, Object> hcoMap = new TreeMap<>();
		putSetting(hcoMap, sf, ALLOW_JTA_TRANSACTION_ACCESS, etc);
		putSetting(hcoMap, sf, ALLOW_JTA_TRANSACTION_ACCESS, etc);
		putSetting(hcoMap, sf, ALLOW_JTA_TRANSACTION_ACCESS, etc);
		putSetting(hcoMap, sf, ALLOW_UPDATE_OUTSIDE_TRANSACTION, etc);
		putSetting(hcoMap, sf, AUTO_CLOSE_SESSION, etc);
		putSetting(hcoMap, sf, AUTOCOMMIT, etc);
		putSetting(hcoMap, sf, AUTO_EVICT_COLLECTION_CACHE, etc);
		putSetting(hcoMap, sf, BATCH_FETCH_STYLE, etc);
		putSetting(hcoMap, sf, BATCH_VERSIONED_DATA, etc);
		putSetting(hcoMap, sf, CACHE_REGION_FACTORY, etc);
		putSetting(hcoMap, sf, CACHE_REGION_PREFIX, etc);
		putSetting(hcoMap, sf, CHECK_NULLABILITY, etc);
		putSetting(hcoMap, sf, CONNECTION_HANDLING, etc);
		putSetting(hcoMap, sf, CUSTOM_ENTITY_DIRTINESS_STRATEGY, etc);
		putSetting(hcoMap, sf, DEFAULT_BATCH_FETCH_SIZE, etc);
		putSetting(hcoMap, sf, DEFAULT_CACHE_CONCURRENCY_STRATEGY, etc);
		putSetting(hcoMap, sf, DEFAULT_CATALOG, etc);
		putSetting(hcoMap, sf, DEFAULT_SCHEMA, etc);
		putSetting(hcoMap, sf, DIALECT, etc);
		putSetting(hcoMap, sf, DRIVER, etc);
		putSetting(hcoMap, sf, FORMAT_SQL, etc);
		putSetting(hcoMap, sf, SHOW_SQL, etc);
		putSetting(hcoMap, sf, FLUSH_BEFORE_COMPLETION, etc);
		putSetting(hcoMap, sf, GENERATE_STATISTICS, etc);
		putSetting(hcoMap, sf, HBM2DDL_AUTO, etc);
		putSetting(hcoMap, sf, HBM2DDL_CONNECTION, etc);
		putSetting(hcoMap, sf, HBM2DDL_DATABASE_ACTION, etc);
		putSetting(hcoMap, sf, HBM2DDL_SCRIPTS_ACTION, etc);
		putSetting(hcoMap, sf, HBM2DDL_SCRIPTS_CREATE_TARGET, etc);
		putSetting(hcoMap, sf, HBM2DDL_SCRIPTS_DROP_TARGET, etc);
		putSetting(hcoMap, sf, INTERCEPTOR, etc);
		putSetting(hcoMap, sf, ISOLATION, etc);
		putSetting(hcoMap, sf, JPA_SHARED_CACHE_MODE, etc);
		putSetting(hcoMap, sf, JPA_SHARED_CACHE_RETRIEVE_MODE, etc);
		putSetting(hcoMap, sf, JPA_SHARED_CACHE_STORE_MODE, etc);
		putSetting(hcoMap, sf, JPA_JTA_DATASOURCE, etc);
		putSetting(hcoMap, sf, JPA_NON_JTA_DATASOURCE, etc);
		putSetting(hcoMap, sf, JTA_CACHE_TM, etc);
		putSetting(hcoMap, sf, JTA_CACHE_UT, etc);
		putSetting(hcoMap, sf, JTA_PLATFORM, etc);
		putSetting(hcoMap, sf, JTA_PLATFORM_RESOLVER, etc);
		putSetting(hcoMap, sf, JPAQL_STRICT_COMPLIANCE, etc);
		putSetting(hcoMap, sf, JTA_TRACK_BY_THREAD, etc);
		putSetting(hcoMap, sf, MAX_FETCH_DEPTH, etc);
		putSetting(hcoMap, sf, MULTI_TENANT_IDENTIFIER_RESOLVER, etc);
		putSetting(hcoMap, sf, ORDER_INSERTS, etc);
		putSetting(hcoMap, sf, ORDER_UPDATES, etc);
		putSetting(hcoMap, sf, PASS, etc);
		putSetting(hcoMap, sf, PERSISTENCE_UNIT_NAME, etc);
		putSetting(hcoMap, sf, PREFER_USER_TRANSACTION, etc);
		putSetting(hcoMap, sf, QUERY_CACHE_FACTORY, etc);
		putSetting(hcoMap, sf, QUERY_STARTUP_CHECKING, etc);
		putSetting(hcoMap, sf, SESSION_FACTORY_NAME, etc);
		putSetting(hcoMap, sf, SESSION_FACTORY_NAME_IS_JNDI, etc);
		putSetting(hcoMap, sf, SESSION_SCOPED_INTERCEPTOR, etc);
		putSetting(hcoMap, sf, STATEMENT_BATCH_SIZE, etc);
		putSetting(hcoMap, sf, STATEMENT_FETCH_SIZE, etc);
		putSetting(hcoMap, sf, STATEMENT_INSPECTOR, etc);
		putSetting(hcoMap, sf, URL, etc);
		putSetting(hcoMap, sf, USE_DIRECT_REFERENCE_CACHE_ENTRIES, etc);
		putSetting(hcoMap, sf, USE_GET_GENERATED_KEYS, etc);
		putSetting(hcoMap, sf, USE_IDENTIFIER_ROLLBACK, etc);
		putSetting(hcoMap, sf, USE_MINIMAL_PUTS, etc);
		putSetting(hcoMap, sf, USE_QUERY_CACHE, etc);
		putSetting(hcoMap, sf, USE_SCROLLABLE_RESULTSET, etc);
		putSetting(hcoMap, sf, USE_SECOND_LEVEL_CACHE, etc);
		putSetting(hcoMap, sf, USE_SQL_COMMENTS, etc);
		putSetting(hcoMap, sf, USE_STRUCTURED_CACHE, etc);
		putSetting(hcoMap, sf, USER, etc);
		putSetting(hcoMap, sf, ConfigSettings.PROVIDER, etc);
		putSetting(hcoMap, sf, ConfigSettings.CONFIG_URI, etc);
		putSetting(hcoMap, sf, ConfigSettings.MISSING_CACHE_STRATEGY, etc);

		ConnectionProvider cnp = ssr.getService(ConnectionProvider.class);
		hcoMap.put(CONNECTION_PROVIDER, cnp.getClass().getName());
		if ( cnp instanceof DriverManagerConnectionProviderImpl )
		{
			// Hibernate Non-production Connection Provider.
			if ( !emfProperties.containsKey(DriverManagerConnectionProviderImpl.INITIAL_SIZE) )
				hcoMap.put(DriverManagerConnectionProviderImpl.INITIAL_SIZE, 1);
			if ( !emfProperties.containsKey(DriverManagerConnectionProviderImpl.MIN_SIZE) )
				hcoMap.put(DriverManagerConnectionProviderImpl.MIN_SIZE, 1);
			if ( !emfProperties.containsKey(DriverManagerConnectionProviderImpl.VALIDATION_INTERVAL) )
				hcoMap.put(DriverManagerConnectionProviderImpl.VALIDATION_INTERVAL, 30);
			if ( !emfProperties.containsKey(POOL_SIZE) )
				hcoMap.put(POOL_SIZE, 20);
		}
//		else if ( cnp instanceof HikariCPConnectionProvider )
//		{
//			// HikariCP Connection Provider
//			HikariCPConnectionProvider hcp = (HikariCPConnectionProvider) cnp;
//			FieldAccessor<HikariConfig> hcAccessor = new FieldAccessor<>(HikariCPConnectionProvider.class, "hcfg", HikariConfig.class);
//			HikariConfig hc = hcAccessor.get(hcp);
//			Set<String> hcPropertyNames = PropertyElf.getPropertyNames(HikariConfig.class);
//			for ( String hcPropertyName : hcPropertyNames )
//			{
//				Object hcPropertyValue = PropertyElf.getProperty(hcPropertyName, hc);
//				String hhcPropertyName = HikariConfigurationUtil.CONFIG_PREFIX + hcPropertyName;
//				hcoMap.put(hhcPropertyName, hcPropertyValue);
//			}
//		}
		
		return hcoMap;
	}
	
	/**
	 * Put an option and set the value into the given map.
	 * 
	 * @param map A map to gather the option.
	 * @param sf The Hibernate SessionFactory.
	 * @param option The option to put.
	 * @param etc A map of reference options.
	 */
	private static void putSetting(Map<String,Object> map, SessionFactory sf, String option, Map<String, Object> etc)
	{
		map.put(option, settingValue(sf, option, etc));
	}
	
	/**
	 * Retrieve Hibernate value from property name.
	 * 
	 * @param sf Factory to create Hibernate sessions.
	 * @param name Setting property name.
	 * 
	 * @return The property value.
	 */
	private static Object settingValue(SessionFactory sf, String name, Map<String, Object> etc)
	{
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) sf;
		SessionFactoryOptions sfo = sfi.getSessionFactoryOptions();
		return settingValue(sfo, etc, name);
	}

	/**
	 * Retrieve Hibernate value from property name.
	 * 
	 * @param sfo Aggregator of special options used to build the SessionFactory.
	 * @param jdb 
	 * @param name Setting property name.
	 * 
	 * @return The property value.
	 */
	private static Object settingValue(SessionFactoryOptions sfo, Map<String, Object> etc, String name)
	{
		Object value = "";
		switch ( name )
		{
			case ALLOW_JTA_TRANSACTION_ACCESS: value = sfo.isJtaTransactionAccessEnabled(); break;
			case ALLOW_UPDATE_OUTSIDE_TRANSACTION: value = sfo.isAllowOutOfTransactionUpdateOperations(); break;
			case AUTO_CLOSE_SESSION: value = sfo.isAutoCloseSessionEnabled(); break;
			case AUTOCOMMIT: value = etc.get(AUTOCOMMIT); break;
			case AUTO_EVICT_COLLECTION_CACHE: value = sfo.isAutoEvictCollectionCache(); break;
			case BATCH_FETCH_STYLE: value = sfo.getBatchFetchStyle(); break;
			case BATCH_VERSIONED_DATA: value = sfo.isJdbcBatchVersionedData(); break;
			case CACHE_REGION_FACTORY: value = etc.get(CACHE_REGION_FACTORY); break;
			case CACHE_REGION_PREFIX: value = sfo.getCacheRegionPrefix(); break;
			case CHECK_NULLABILITY: value = sfo.isCheckNullability(); break;
			case CONNECTION_HANDLING: value = sfo.getPhysicalConnectionHandlingMode(); break;
//			case CONNECTION_PROVIDER: value = etc.get(CONNECTION_PROVIDER); break;
			case CUSTOM_ENTITY_DIRTINESS_STRATEGY: value = sfo.getCustomEntityDirtinessStrategy().getClass().getName(); break;
			case DEFAULT_BATCH_FETCH_SIZE: value = sfo.getDefaultBatchFetchSize(); break;
			case DEFAULT_CACHE_CONCURRENCY_STRATEGY: value = etc.get(DEFAULT_CACHE_CONCURRENCY_STRATEGY); break;
			case DEFAULT_CATALOG: value = etc.get(DEFAULT_CATALOG); break;
			case DEFAULT_SCHEMA: value = etc.get(DEFAULT_SCHEMA); break;
			case DIALECT: value = etc.get(DIALECT); break;
			case DRIVER: value = etc.get(DRIVER); break;
			case FORMAT_SQL: value = etc.get(FORMAT_SQL); break;
			case FLUSH_BEFORE_COMPLETION: value = sfo.isFlushBeforeCompletionEnabled(); break;
			case GENERATE_STATISTICS: value = sfo.isStatisticsEnabled(); break;
			case HBM2DDL_AUTO: value = etc.get(HBM2DDL_AUTO); break;
			case HBM2DDL_CONNECTION: value = etc.get(HBM2DDL_CONNECTION); break;
			case HBM2DDL_DATABASE_ACTION: value = etc.get(HBM2DDL_DATABASE_ACTION); break;
			case HBM2DDL_SCRIPTS_ACTION: value = etc.get(HBM2DDL_SCRIPTS_ACTION); break;
			case HBM2DDL_SCRIPTS_CREATE_TARGET: value = etc.get(HBM2DDL_SCRIPTS_CREATE_TARGET); break;
			case HBM2DDL_SCRIPTS_DROP_TARGET: value = etc.get(HBM2DDL_SCRIPTS_DROP_TARGET); break;
			case INTERCEPTOR: value = sfo.getInterceptor().getClass().getName(); break;
			case ISOLATION: value = etc.get(ISOLATION); break;
			case JPA_SHARED_CACHE_MODE: value = etc.get(JPA_SHARED_CACHE_MODE); break;
			case JPA_SHARED_CACHE_RETRIEVE_MODE: value = etc.get(JPA_SHARED_CACHE_RETRIEVE_MODE); break;
			case JPA_SHARED_CACHE_STORE_MODE: value = etc.get(JPA_SHARED_CACHE_STORE_MODE); break;
			case JPA_JTA_DATASOURCE: value = null; break; // JNDI Only
			case JPA_NON_JTA_DATASOURCE: value = null; break; // JNDI Only
			case JTA_CACHE_TM: value = etc.get(JTA_CACHE_TM); break;
			case JTA_CACHE_UT: value = etc.get(JTA_CACHE_UT); break;
			case JTA_PLATFORM: value = etc.get(JTA_PLATFORM); break;
			case JTA_PLATFORM_RESOLVER: value = etc.get(JTA_PLATFORM_RESOLVER); break;
//			case JPAQL_STRICT_COMPLIANCE: value = sfo.isStrictJpaQueryLanguageCompliance(); break;
			case JTA_TRACK_BY_THREAD: value = sfo.isJtaTrackByThread(); break;
			case MAX_FETCH_DEPTH: value = sfo.getMaximumFetchDepth(); break;
			case MULTI_TENANT_IDENTIFIER_RESOLVER: value = sfo.getCurrentTenantIdentifierResolver(); break;
			case ORDER_INSERTS: value = sfo.isOrderInsertsEnabled(); break;
			case ORDER_UPDATES: value = sfo.isOrderUpdatesEnabled(); break;
			case PASS: value = etc.get(PASS); break;
			case PERSISTENCE_UNIT_NAME: value = etc.get(PERSISTENCE_UNIT_NAME); break;
			case PREFER_USER_TRANSACTION: value = sfo.isPreferUserTransaction(); break;
			case QUERY_CACHE_FACTORY: value = sfo.getTimestampsCacheFactory().getClass().getName(); break;
			case QUERY_STARTUP_CHECKING: value = sfo.isNamedQueryStartupCheckingEnabled(); break;
			case SESSION_FACTORY_NAME: value = sfo.getSessionFactoryName(); break;
			case SESSION_FACTORY_NAME_IS_JNDI: value = sfo.isSessionFactoryNameAlsoJndiName(); break;
			case SESSION_SCOPED_INTERCEPTOR: value = sfo.getStatelessInterceptorImplementorSupplier(); break;
			case SHOW_SQL: value = etc.get(SHOW_SQL); break;
			case STATEMENT_BATCH_SIZE: value = sfo.getJdbcBatchSize(); break;
			case STATEMENT_FETCH_SIZE: value = etc.get(STATEMENT_FETCH_SIZE); break;
			case STATEMENT_INSPECTOR: value = sfo.getStatementInspector(); break;
			case URL: value = etc.get(URL); break;
			case USE_DIRECT_REFERENCE_CACHE_ENTRIES: value = sfo.isDirectReferenceCacheEntriesEnabled(); break;
			case USE_GET_GENERATED_KEYS: value = sfo.isGetGeneratedKeysEnabled(); break;
			case USE_IDENTIFIER_ROLLBACK: value = sfo.isIdentifierRollbackEnabled(); break;
			case USE_MINIMAL_PUTS: value = sfo.isMinimalPutsEnabled(); break;
			case USE_QUERY_CACHE: value = sfo.isQueryCacheEnabled(); break;
			case USE_SCROLLABLE_RESULTSET: value = sfo.isScrollableResultSetsEnabled(); break;
			case USE_SECOND_LEVEL_CACHE: value = sfo.isSecondLevelCacheEnabled(); break;
			case USE_SQL_COMMENTS: value = sfo.isCommentsEnabled(); break;
			case USE_STRUCTURED_CACHE: value = sfo.isStructuredCacheEntriesEnabled(); break;
			case USER: value = etc.get(USER); break;
			case ConfigSettings.PROVIDER: value = etc.get(ConfigSettings.PROVIDER); break;
			case ConfigSettings.CONFIG_URI: value = etc.get(ConfigSettings.CONFIG_URI); break;
			case ConfigSettings.MISSING_CACHE_STRATEGY: value = etc.get(ConfigSettings.MISSING_CACHE_STRATEGY); break;
		}
		return value;
	}

    /**
     * Check if the proxy or persistent collection is initialized.
     *
     * @param proxy a persistable object, proxy, persistent collection or null.
     * @return true if the argument is already initialized, or is not a proxy or collection
     */
    public static boolean isInitialized(Object proxy)
    {
        return Hibernate.isInitialized(proxy);
    }

    /**
     * Is this entity a proxy?
     *
     * @param entity The entity to examine.
     *
     * @return True when entity is proxied; otherwise, false.
     */
    public static boolean isProxy(Object entity)
    {
        return ( entity instanceof HibernateProxy );
    }

    /**
	 * Exposes statistics for a particular {@link org.hibernate.SessionFactory}.
	 * Beware of milliseconds metrics, they are dependent of the JVM precision:
	 * you may then encounter a 10 ms approximation depending on you OS
	 * platform.  Please refer to the JVM documentation for more information.
     * 
     * @param emf An Entity Manager Factory.
     */
    public static boolean logSummaryStatistics(EntityManagerFactory emf)
    {
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.logSummary();
        return stats.isStatisticsEnabled();
    }

    /**
     * Execute a SQL action (insert, update, delete, etc.) using a connection
     * provided by a JPA EntityManagerFactory. The EntityManagerFactory is
     * used to unwrap a Hibernate SessionFactory. 
     * 
     * The batch is executed in a JDBC transaction.
     * 
     * @param emf The JPA EntityManagerFactory
     * @param sqlAction A SQL action.
     * 
     * @return The action count.
     */
	public static int sqlAction(EntityManagerFactory emf, String sqlAction)
	{
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        return sqlAction(sessionFactory, sqlAction);
	}
	
    /**
     * Execute a SQL action (insert, update, delete, etc.) using a connection
     * provided by a Hibernate SessionFactory. The batch is executed
     * in a JDBC transaction.
     * 
     * @param sf The Hibernate SessionFactory;
     * @param sqlAction A SQL action.
     * 
     * @return The action count.
     */
	public static int sqlAction(SessionFactory sf, String sqlAction)
	{
		try ( Session session = sf.openSession() )
		{
			ReturningWork<Integer> rw = (connection) -> {
				TransactionalSql<Integer> tx = (conn) ->
				{
					Statement stmt = conn.createStatement();
					return stmt.executeUpdate(sqlAction);
				};
				return tx.transact(connection);
			};
			return session.doReturningWork(rw);
		}
	}
	
    /**
     * Execute a JDBC batch of SQL actions (insert, update, delete, etc.) using a connection
     * provided by a Hibernate SessionFactory. The EntityManagerFactory is used to unwrap a
     * Hibernate SessionFactory.
     * 
     * The batch is executed in a JDBC transaction.
     * 
     * @param emf The JPA EntityManagerFactory
     * @param sqlBatch A batch of SQL actions.
     * 
     * @return A list of counts for each SQL is the batch.
     */
	public static int[] sqlBatch(EntityManagerFactory emf, List<String> sqlBatch)
	{
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        return sqlBatch(sessionFactory, sqlBatch);
	}
	
    /**
     * Execute a JDBC batch of SQL actions (insert, update, delete, etc.) using a connection
     * provided by a Hibernate SessionFactory. The batch is executed in a JDBC transaction.
     * 
     * @param sf The Hibernate SessionFactory;
     * @param sqlBatch A batch of SQL actions.
     * 
     * @return A list of counts for each SQL is the batch.
     */
	public static int[] sqlBatch(SessionFactory sf, List<String> sqlBatch)
	{
		try ( Session session = sf.openSession() )
		{
			ReturningWork<int[]> rw = (connection) -> {
				TransactionalSql<int[]> tx = (conn) ->
				{
					Statement stmt = conn.createStatement();
					for ( String sql : sqlBatch )
						stmt.addBatch(sql);
					return stmt.executeBatch();
				};
				return tx.transact(connection);
			};
			return session.doReturningWork(rw);
		}
	}

    /**
     * Execute a JDBC query using a connection provided by a JPA EntityManagerFactory.
     * The EntityManagerFactory is used to unwrap a Hibernate SessionFactory.
     * 
     * The query is executed in a JDBC transaction.
     * 
     * @param emf The JPA EntityManagerFactory.
     * @param sql The SQL to execute.
     * @return A map of column names and values.
     */
	public static Map<String,List<Object>> sqlQuery(EntityManagerFactory emf, String sql)
	{
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
		return sqlQuery(sessionFactory, sql);
	}

    /**
     * Execute a JDBC query using a connection provided by a Hibernate
     * SessionFactory. The query is executed in a JDBC transaction.
     * 
     * @param sf The Hibernate SessionFactory.
     * @param sql The SQL to execute.
     * @return A map of column names and values.
     */
	public static Map<String,List<Object>> sqlQuery(SessionFactory sf, String sql)
	{
		try ( Session session = sf.openSession() )
		{
			ReturningWork<Map<String,List<Object>>> rw = (connection) -> {
				TransactionalSql<Map<String,List<Object>>> tx = (conn) ->
				{
					Statement stmt = conn.createStatement();
					try ( ResultSet rs = stmt.executeQuery(sql) )
					{
						return toColumnMap(rs); 
					}
				};
				return tx.transact(connection);
			};
			return session.doReturningWork(rw);
		}
	}
	
	/**
	 * Convert a JDBC ResultSet into a Map of column names with row lists.
	 * @param rs A JDBC ResultSet
	 * @return A Map of column names with each name having a list of row values.
	 * @throws SQLException Information on a database access error or other errors.
	 */
	private static Map<String,List<Object>> toColumnMap(ResultSet rs) throws SQLException
	{
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    Map<String,List<Object>> columnMap = new HashMap<>(columns);
	    // Put column names and empty column lists into a Map.
	    for (int index = 1; index <= columns; ++index)
	        columnMap.put(md.getColumnName(index), new ArrayList<>());
	    // Iterate of the JDBC result set and add row values to column map.
	    while (rs.next())
	    {
	        for (int i = 1; i <= columns; ++i)
	            columnMap.get(md.getColumnName(i)).add(rs.getObject(i));
	    }
	    // Return map of column names and list of row values for each column.
	    return columnMap;
	}
}
