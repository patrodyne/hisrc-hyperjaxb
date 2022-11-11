package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static org.jvnet.hyperjaxb.ejb.Constants.TODO_LOG_LEVEL;

import java.util.Collection;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jvnet.hyperjaxb.ejb.Constants;
import org.jvnet.hyperjaxb.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb.ejb.strategy.model.ProcessModel;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.model.TypeUse;

public abstract class AbstractWrapBuiltin implements CreatePropertyInfos
{
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected abstract Collection<CPropertyInfo> wrapAnyType(ProcessModel context, CPropertyInfo propertyInfo);

	public abstract CBuiltinLeafInfo getTypeUse(ProcessModel context, CPropertyInfo propertyInfo);

	public abstract CreatePropertyInfos getCreatePropertyInfos(ProcessModel context, CPropertyInfo propertyInfo);
	
	public Collection<CPropertyInfo> process(ProcessModel context, CPropertyInfo propertyInfo)
	{
		// Single
		// assert !propertyInfo.isCollection();
		// Builtin
		Collection<? extends CTypeInfo> types = context.getGetTypes().process(context, propertyInfo);
		assert types.size() == 1;
		assert types.iterator().next() instanceof CBuiltinLeafInfo;
		final CBuiltinLeafInfo originalTypeUse = getTypeUse(context, propertyInfo);
		if (propertyInfo.getAdapter() != null)
		{
			logger.debug("Adapter property info is not wrapped");
			return Collections.emptyList();
		}
		else if (originalTypeUse == CBuiltinLeafInfo.DATA_HANDLER)
		{
			// TODO #42
			todo("Data handler is currently not supported. See issue #88 (http://java.net/jira/browse/HYPERJAXB3-88).");
			return Collections.emptyList();
		}
		else if (originalTypeUse == CBuiltinLeafInfo.ANYTYPE)
		{
			return wrapAnyType(context, propertyInfo);
		}
		else
		{
			final TypeUse adaptedTypeUse = context.getAdaptBuiltinTypeUse().process(context, propertyInfo);
			if (adaptedTypeUse == null)
			{
				todo("Unsupported builtin type ["	+ originalTypeUse.getTypeName() + "] in property ["
					+ propertyInfo.getName(true) + "].");
				return Collections.emptyList();
			}
			else
			{
				final CreatePropertyInfos createPropertyInfos = getCreatePropertyInfos(context, propertyInfo);
				final Collection<CPropertyInfo> newPropertyInfos = createPropertyInfos.process(context, propertyInfo);
				// Customizations.markIgnored(propertyInfo);
				return newPropertyInfos;
			}
		}
	}

	protected void todo(String comment)
	{
		String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
		String level = System.getProperty(TODO_LOG_LEVEL);
		if ("DEBUG".equalsIgnoreCase(level))
			logger.debug(msg);
		else if ("INFO".equalsIgnoreCase(level))
			logger.info(msg);
		else if ("WARN".equalsIgnoreCase(level))
			logger.warn(msg);
		else if ("ERROR".equalsIgnoreCase(level))
			logger.error(msg);
		else
			logger.error(msg);
	}
}
