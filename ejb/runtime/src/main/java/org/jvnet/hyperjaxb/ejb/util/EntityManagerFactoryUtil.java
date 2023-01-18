package org.jvnet.hyperjaxb.ejb.util;

import static org.jvnet.hyperjaxb.ejb.Constants.PERSISTENCE_PROPERTIES_BASE_FILE;
import static org.jvnet.hyperjaxb.ejb.Constants.PERSISTENCE_PROPERTIES_MORE_FILE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

import jakarta.persistence.EntityManagerFactory;

/**
 * Utility methods for EntityManagerFactory.
 * 
 * @author Rick O'Sullivan
 *
 */
public class EntityManagerFactoryUtil
{
	private static final Logger logger = LoggerFactory.getLogger(EntityManagerFactoryUtil.class);
	
	/**
	 * Filter the properties from an EntityManagerFactory instance to include 
	 * and exclude keys containing the standard sub-strings.
	 * 
	 * Property values representing classes are translated to the class name.
	 * 
	 * @return A map of filtered and normalized (EntityManagerFactory) properties.
	 */
	public static Map<String, Object> filterProperties(EntityManagerFactory emf)
	{
		return filterProperties(emf,
			new String[] { "hibernate", "persistence" },
			new String[] { "hibernate.boot", "hibernate.bytecode" });
	}

	/**
	 * Filter the properties from an EntityManagerFactory instance to include 
	 * and exclude keys containing the given sub-strings.
	 * 
	 * Property values representing classes are translated to the class name.
	 * 
	 * @param includeKeys An array of sub-strings to include. 
	 * @param excludeKeys An array of sub-strings to exclude.
	 * 
	 * @return A map of filtered and normalized (EntityManagerFactory) properties.
	 */
	public static Map<String, Object> filterProperties(EntityManagerFactory emf, String[] includeKeys, String[] excludeKeys)
	{
		return filterProperties(emf.getProperties(), includeKeys, excludeKeys);
	}

	/**
	 * Filter the source properties to include and exclude keys containing the
	 * given sub-strings.
	 * 
	 * Property values representing classes are translated to the class name.
	 * 
	 * @param srcProperties The (EntityManagerFactory) source properties to filetr.
	 * @param includeKeys An array of sub-strings to include. 
	 * @param excludeKeys An array of sub-strings to exclude.
	 * 
	 * @return A map of filtered and normalized (EntityManagerFactory) properties.
	 */
	public static Map<String, Object> filterProperties(Map<String, Object> srcProperties, String[] includeKeys, String[] excludeKeys)
	{
		Map<String, Object> emfProperties = new TreeMap<>();
		for ( Entry<String, Object> entry : srcProperties.entrySet() )
		{
			if ( containsSubString(includeKeys, entry) )
			{
				if ( ! containsSubString(excludeKeys, entry) )
				{
					if ( entry.getValue() instanceof Class )
						emfProperties.put(entry.getKey(), ((Class<?>) entry.getValue()).getName());
					else if ( entry.getValue() instanceof String )
						emfProperties.put(entry.getKey(), entry.getValue());
					else if ( entry.getValue() != null )
						emfProperties.put(entry.getKey(), entry.getValue().getClass().getName());
				}
			}
		}
		return emfProperties;
	}

	/**
	 * A predicate to determine if an Entry's key contains given substrings.
	 * 
	 * @param substrings An array of substrings to check.
	 * @param entry A map entry to examine.
	 * 
	 * @return True when the entry's key contains one of the substrings.
	 */
	private static boolean containsSubString(String[] substrings, Entry<String, Object> entry)
	{
		boolean result = false;
		for ( String includeKey : substrings)
		{
			if ( entry.getKey().contains(includeKey) )
			{
				result = true;
				break;
			}
		}
		return result;
	}
	
	public static String getPersistencePropertiesBaseFile()
	{
		return getPersistencePropertiesBaseFile("persistence.properties");
	}

	public static String getPersistencePropertiesBaseFile(String baseFile)
	{
		return System.getProperty(PERSISTENCE_PROPERTIES_BASE_FILE, baseFile);
	}
	
	public static String getPersistencePropertiesMoreFile()
	{
		return getPersistencePropertiesMoreFile("persistence-more.properties");
	}

	public static String getPersistencePropertiesMoreFile(String moreFile)
	{
		return System.getProperty(PERSISTENCE_PROPERTIES_MORE_FILE, moreFile);
	}
	
	public static Map<String, String> createEntityManagerFactoryProperties(Class<?> clazz)
	{
		try
		{
			final Enumeration<URL> resourcesBase =
				clazz.getClassLoader().getResources(getPersistencePropertiesBaseFile());
			if (resourcesBase.hasMoreElements())
			{
				logger.debug("Loading entity manager factory properties.");
				Properties properties = new Properties();
				properties = loadResources(properties, resourcesBase);
				if (properties != null)
				{
					final Enumeration<URL> resourcesMore =
						clazz.getClassLoader().getResources(getPersistencePropertiesMoreFile());
					if (resourcesMore.hasMoreElements())
					{
						logger.debug("Loading more entity manager factory properties.");
						properties = loadResources(properties, resourcesMore);
					}
				}
				return toMap(properties);
			}
			else
			{
				logger.debug("Entity manager factory properties are not set.");
				return null;
			}
		}
		catch (IOException ex)
		{
			return null;
		}
	}
	
	private static Properties loadResources(Properties properties, final Enumeration<URL> resourcesBase)
	{
		while (resourcesBase.hasMoreElements())
		{
			final URL resource = resourcesBase.nextElement();
			logger.info("EMF: Loading [" + resource + "].");
			if (resource == null)
			{
				properties = null;
				break;
			}
			else
			{
				try (InputStream is = resource.openStream())
				{
					properties.load(is);
				}
				catch (IOException ex)
				{
					properties = null;
					break;
				}
			}
		}
		return properties;
	}
	
	private static Map<String, String> toMap(Properties prop)
	{
		return prop.entrySet().stream().collect(
			Collectors.toMap(
				e -> String.valueOf(e.getKey()),
				e -> String.valueOf(e.getValue()),
				(prev, next) -> next, HashMap::new
			));
	}
}
