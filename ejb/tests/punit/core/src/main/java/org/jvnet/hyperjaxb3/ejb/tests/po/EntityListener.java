package org.jvnet.hyperjaxb3.ejb.tests.po;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityListener {

	protected static Logger logger = LoggerFactory.getLogger(EntityListener.class);

	public void prePersist(Object object) {
		logger.debug("Pre-persisting [" + object + "].");

	}
}
