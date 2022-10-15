package org.jvnet.hyperjaxb3.ejb.strategy.naming.impl;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.glassfish.jaxb.core.api.impl.NameConverter;
import org.jvnet.hyperjaxb3.ejb.strategy.ignoring.Ignoring;
import org.jvnet.hyperjaxb3.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.Naming;
import org.jvnet.hyperjaxb3.ejb.strategy.naming.ReservedNames;
import org.jvnet.jaxb2_commons.util.CodeModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassRef;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

/**
 * The JPA entity, table, etc. default naming strategy.
 * 
 * Injected: Ignoring, ReservedNames
 * Instantiated: NameKeyMap, KeyNameMap, ForwardTableNamesMap, ReverseTableNamesMap
 */
@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
public class DefaultNaming implements Naming, InitializingBean
{
	protected Logger logger = LoggerFactory.getLogger(Naming.class);
	private static final Pattern CAMELCASE_PATTERN = Pattern.compile("\\p{Lower}\\p{Upper}|\\D\\d");

	@Inject
	private Ignoring ignoring;
	public Ignoring getIgnoring() { return ignoring; }
	public void setIgnoring(Ignoring ignoring) { this.ignoring = ignoring; }
	
	@Inject
	private ReservedNames reservedNamesCDI;
	public ReservedNames getReservedNamesCDI()
	{
		return reservedNamesCDI;
	}
	public void setReservedNamesCDI(ReservedNames reservedNamesCDI)
	{
		this.reservedNamesCDI = reservedNamesCDI;
	}

	private Properties reservedNames;
	public Properties getReservedNames()
	{
		if ( reservedNames == null )
		{
			reservedNames = new Properties();
			for ( Entry<Object, Object> entry : getReservedNamesCDI().entrySet() )
				reservedNames.put(entry.getKey(), entry.getValue());
		}
		return reservedNames;
	}
	@Required
	public void setReservedNames(Properties reservedNames)
	{
		this.reservedNames = reservedNames;
	}

	private int maxIdentifierLength = 30;
	public int getMaxIdentifierLength() { return maxIdentifierLength; }
	public void setMaxIdentifierLength(int maxIdentifierLength) { this.maxIdentifierLength = maxIdentifierLength; }

	private boolean updated = false;
	@SuppressWarnings("unused")
	private boolean isUpdated() { return updated; }
	private void setUpdated(boolean updated) { this.updated = updated; }

	private Map<String, String> nameKeyMap = new TreeMap<String, String>();
	private Map<String, String> getNameKeyMap() { return nameKeyMap; }

	private Map<String, String> keyNameMap = new TreeMap<String, String>();
	private Map<String, String> getKeyNameMap() { return keyNameMap; }

	private Map<String, String> forwardTableNamesMap = Collections.synchronizedMap(new HashMap<String, String>());
	private Map<String, String> getForwardTableNamesMap() { return forwardTableNamesMap; }

	private Map<String, String> reverseTableNamesMap = Collections.synchronizedMap(new HashMap<String, String>());
	private Map<String, String> getReverseTableNamesMap() { return reverseTableNamesMap; }

	public void afterPropertiesSet() throws Exception
	{
		final Set<Entry<Object, Object>> entries = getReservedNames().entrySet();
		
		for (final Entry<Object, Object> entry : entries)
		{
			final Object entryKey = entry.getKey();
			if (entryKey != null)
			{
				final String key = entryKey.toString().toUpperCase();
				final Object entryValue = entry.getValue();
				final String value = 
					( (entryValue == null) || "".equals(entryValue.toString().trim()) )
					? key + "_"
					: entryValue.toString();
				getNameKeyMap().put(key, value);
				getKeyNameMap().put(value, key);
			}
		}
	}

	public String getName(Mapping context, final String draftName)
	{
		Validate.notNull(draftName, "Name must not be null.");
		// final String name = draftName.replace('$', '_').toUpperCase();
		String intermediateName = draftName.replace('$', '_');
		
		final Matcher camelCaseMatcher = CAMELCASE_PATTERN.matcher(intermediateName);
		while (camelCaseMatcher.find())
		{
			final String regFusion = camelCaseMatcher.group(0);
			String snakeName = regFusion.charAt(0) + "_" + regFusion.charAt(1);
			intermediateName = intermediateName.replace(regFusion, snakeName);
		}

		final String name = intermediateName.toUpperCase();

		if (getNameKeyMap().containsKey(name))
			return (String) getNameKeyMap().get(name);
		else if (name.length() >= getMaxIdentifierLength())
		{
			for (int i = 0;; i++)
			{
				final String suffix = Integer.toString(i);
				final String prefix = name.substring(0, getMaxIdentifierLength() - suffix.length() - 1);
				final String identifier = prefix + "_" + suffix;
				
				if (!getKeyNameMap().containsKey(identifier))
				{
					getNameKeyMap().put(name, identifier);
					getKeyNameMap().put(identifier, name);
					setUpdated(true);
					return identifier;
				}
			}
		}
		else if (getReservedNames().containsKey(name.toUpperCase()))
			return name + "_";
		else
			return name;
	}

	public String getColumn$Name(Mapping context, FieldOutline fieldOutline)
	{
		final String fieldName = fieldOutline.getPropertyInfo().getName(true);
		return getName(context, context.getNaming().getColumn$Name$Prefix(context) + fieldName);
	}

	public String getOrderColumn$Name(Mapping context, FieldOutline fieldOutline)
	{
		final String fieldColumnName = getColumn$Name(context, fieldOutline);
		return getName(context, fieldColumnName + "_" + "HJORDER");
	}

	public String getJoinTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		final String targetEntityTableName = getTargetEntityTable$Name(context, fieldOutline);
		final String entityTableName = getEntityTable$Name(context, fieldOutline.parent());
		final String fieldColumnName = getColumn$Name(context, fieldOutline);
		return getName(context, entityTableName + "_" + fieldColumnName + "_" + targetEntityTableName);
	}

	public String getEntityTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		return getEntityTable$Name(context, fieldOutline.parent());
	}

	public String getEntityTable$Name(Mapping context, ClassOutline classOutline)
	{
		return getEntityTableName(context, classOutline.target);
	}

	public String getTableName(String qualifiedName)
	{
		if (getForwardTableNamesMap().containsKey(qualifiedName))
			return getForwardTableNamesMap().get(qualifiedName);
		else
		{
			final String shortName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);

			if (!getReverseTableNamesMap().containsKey(shortName))
			{
				getForwardTableNamesMap().put(qualifiedName, shortName);
				getReverseTableNamesMap().put(shortName, qualifiedName);
				return shortName;
			}
			else
			{
				for (int index = 0; index < Integer.MAX_VALUE; index++)
				{
					final String name = shortName + "_" + index;
					if (!getReverseTableNamesMap().containsKey(name))
					{
						getForwardTableNamesMap().put(qualifiedName, name);
						getReverseTableNamesMap().put(name, qualifiedName);
						return name;
					}
				}
				throw new AssertionError("Could not create a table name for the qualified name [" +
					qualifiedName + "]");
			}
		}
	}

	public String getEntityTableName(Mapping context, final CClass classInfo)
	{
		if (classInfo instanceof CClassInfo)
		{
			return getName(context, getTableName(((CClassInfo) classInfo).fullName()));
		}
		else if (classInfo instanceof CClassRef)
		{
			final String fullName = ((CClassRef) classInfo).fullName();
			return getName(context, getTableName(fullName));
		}
		else
			throw new AssertionError("Unexpected type.");
	}

	private String getTargetEntityTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		final CPropertyInfo propertyInfo = fieldOutline.getPropertyInfo();
		final Collection<? extends CTypeInfo> types = context.getGetTypes() .process(context, propertyInfo);
		assert types.size() == 1;
		final CTypeInfo type = types.iterator().next();
		assert type instanceof CClass;
		final CClass childClassInfo = (CClass) type;
		return getEntityTableName(context, childClassInfo);
	}

	public String getJoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline)
	{
		final String entityTableName = getEntityTable$Name(context, fieldOutline.parent());
		final String fieldColumnName = getName(context, fieldOutline .getPropertyInfo().getName(true));
		final String idFieldColumnName = getName(context, idFieldOutline .getPropertyInfo().getName(true));
		return getName(context, context.getNaming().getColumn$Name$Prefix(context)
			+ fieldColumnName + "_" + entityTableName + "_" + idFieldColumnName);
	}

	public String getJoinTable$JoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline)
	{
		final String entityTableName = getEntityTable$Name(context, fieldOutline.parent());
		final String idFieldColumnName = getColumn$Name(context, idFieldOutline);
		return getName(context, "PARENT_" + entityTableName + "_" + idFieldColumnName);
	}

	public String getJoinTable$InverseJoinColumn$Name(Mapping context, FieldOutline fieldOutline,
		FieldOutline idFieldOutline)
	{
		final String idFieldColumnName = getColumn$Name(context, idFieldOutline);
		return getName(context,
			"CHILD_" + getTargetEntityTable$Name(context, fieldOutline)
			+ "_" + idFieldColumnName);
	}

	public String getPersistenceUnitName(Mapping context, Outline outline)
	{
		final StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (final Iterator<? extends PackageOutline> packageOutlines = outline
			.getAllPackageContexts().iterator(); packageOutlines.hasNext();)
		{
			final PackageOutline packageOutline = packageOutlines.next();
			if (!getIgnoring().isPackageOutlineIgnored(context, outline, packageOutline))
			{
				if (!first)
					sb.append(':');
				else
					first = false;
				final String packageName = packageOutline._package().name();
				sb.append(packageName);
			}
		}
		return sb.toString();
	}

	public String getEntityName(Mapping context, Outline outline, NType type)
	{
		final JType theType = type.toType(outline, Aspect.EXPOSED);
		assert theType instanceof JClass;
		final JClass theClass = (JClass) theType;
		return CodeModelUtils.getLocalClassName(theClass);
	}

	public String getEntityClass(Mapping context, Outline outline, NType type)
	{
		final JType theType = type.toType(outline, Aspect.EXPOSED);
		assert theType instanceof JClass;
		final JClass theClass = (JClass) theType;
		return CodeModelUtils.getPackagedClassName(theClass);
	}

	public String getPropertyName(Mapping context, FieldOutline fieldOutline)
	{
		return NameConverter.standard.toVariableName(fieldOutline.getPropertyInfo().getName(true));
	}

	public Naming createEmbeddedNaming(Mapping context, FieldOutline fieldOutline)
	{
		return new EmbeddedNamingWrapper(this, fieldOutline);
	}

	public String getColumn$Name$Prefix(Mapping context)
	{
		return "";
	}

	public String getElementCollection$CollectionTable$Name(Mapping context, FieldOutline fieldOutline)
	{
		final String entityTableName = getEntityTable$Name(context, fieldOutline.parent());
		final String fieldColumnName = getColumn$Name(context, fieldOutline);
		return getName(context, entityTableName + "_" + fieldColumnName);
	}

	public String getElementCollection$CollectionTable$JoinColumn$Name(
			Mapping context, FieldOutline fieldOutline,
			FieldOutline idFieldOutline)
	{
		final String idFieldColumnName = getName(context, idFieldOutline.getPropertyInfo().getName(true));

		return getName(context,
			context.getNaming().getColumn$Name$Prefix(context)
			// + fieldColumnName + "_" + entityTableName + "_"
			+ idFieldColumnName);
	}

	public String getElementCollection$OrderColumn$Name(Mapping context, FieldOutline fieldOutline)
	{
		final String columnName = /* collectionTableName + "_" + */
			context.getNaming().getColumn$Name$Prefix(context) + "HJINDEX";
		return getName(context, columnName);
	}

	public String getElementCollection$Column$Name(Mapping context, FieldOutline fieldOutline)
	{
		final String columnName = /* collectionTableName + "_" + */
			context.getNaming().getColumn$Name$Prefix(context) + "HJVALUE";
		return getName(context, columnName);
	}
}
