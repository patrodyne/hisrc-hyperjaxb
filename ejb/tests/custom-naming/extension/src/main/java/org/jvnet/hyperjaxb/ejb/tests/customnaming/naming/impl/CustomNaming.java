package org.jvnet.hyperjaxb.ejb.tests.customnaming.naming.impl;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;
import org.jvnet.hyperjaxb.ejb.strategy.naming.impl.DefaultNaming;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 2)
public class CustomNaming extends DefaultNaming
{
	@Override
	public String getName(Mapping context, String draftName)
	{
		return "FOO_" + super.getName(context , draftName);
	}
}
