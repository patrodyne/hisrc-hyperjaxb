package org.jvnet.hyperjaxb3.ejb.jpa3.strategy.model.base;

import static org.jvnet.hyperjaxb3.ejb.Constants.TODO_LOG_LEVEL;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jvnet.hyperjaxb3.ejb.Constants;
import org.jvnet.hyperjaxb3.ejb.strategy.model.CreatePropertyInfos;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ProcessModel;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.AbstractWrapBuiltin;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.AdaptCollectionBuiltinNonReference;
import org.jvnet.hyperjaxb3.ejb.strategy.model.base.CreateNoPropertyInfos;
import org.springframework.beans.factory.annotation.Required;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;

public class WrapCollectionBuiltinNonReference extends AbstractWrapBuiltin {

	private CreatePropertyInfos fallback;

	public CreatePropertyInfos getFallback() {
		return fallback;
	}

	@Required
	public void setFallback(CreatePropertyInfos fallback) {
		this.fallback = fallback;
	}

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	public CBuiltinLeafInfo getTypeUse(ProcessModel context,
			CPropertyInfo propertyInfo) {
		return (CBuiltinLeafInfo) context.getGetTypes().process(context, propertyInfo).iterator().next();
	}

	@Override
	public CreatePropertyInfos getCreatePropertyInfos(ProcessModel context,
			CPropertyInfo propertyInfo) {

		final CBuiltinLeafInfo originalTypeUse = getTypeUse(context,
				propertyInfo);

		final TypeUse adaptingTypeUse = context.getAdaptBuiltinTypeUse()
				.process(context, propertyInfo);

		if (adaptingTypeUse == originalTypeUse
				|| adaptingTypeUse.getAdapterUse() == null) {
			logger.debug("No adaptation required.");
			return CreateNoPropertyInfos.INSTANCE;

		} else {
			return new AdaptCollectionBuiltinNonReference(adaptingTypeUse);
		}
	}

	protected Collection<CPropertyInfo> wrapAnyType(ProcessModel context,
			CPropertyInfo propertyInfo) {
		todo("Element collections of any type is not supported. See issue #HJIII-89 (http://jira.highsource.org/browse/HJIII-89)");
		return getFallback().process(context, propertyInfo);
	}

	private void todo(String comment) {
        String msg = "TODO " + (comment == null ? "Not yet supported." : comment);
		String level = System.getProperty(TODO_LOG_LEVEL);
		if ( "DEBUG".equalsIgnoreCase(level) ) logger.debug(msg);
		else if ( "INFO".equalsIgnoreCase(level) ) logger.info(msg);
		else if ( "WARN".equalsIgnoreCase(level) ) logger.warn(msg);
		else if ( "ERROR".equalsIgnoreCase(level) ) logger.error(msg);
		else logger.error(msg);
	}
}
