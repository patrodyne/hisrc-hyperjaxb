package org.jvnet.hyperjaxb3.ejb.tests.po.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityListener {

	protected static Logger logger = LoggerFactory.getLogger(EntityListener.class);

	public void prePersist(Object object) {
		logger.debug("Extended pre-persisting [" + object + "].");

	}
}
