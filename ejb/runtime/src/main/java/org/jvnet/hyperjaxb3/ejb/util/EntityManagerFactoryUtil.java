package org.jvnet.hyperjaxb3.ejb.util;

import static org.jvnet.hyperjaxb3.ejb.Constants.PERSISTENCE_PROPERTIES_BASE_FILE;
import static org.jvnet.hyperjaxb3.ejb.Constants.PERSISTENCE_PROPERTIES_MORE_FILE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.persistence.EntityManagerFactory;

/**
 * Utility methods for EntityManagerFactory.
 * 
 * @author Rick O'Sullivan
 *
 */
public class EntityManagerFactoryUtil
{
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
}
