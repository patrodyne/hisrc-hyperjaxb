package org.jvnet.hyperjaxb.ejb.strategy.model.base;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER})
public @interface ModelWrap
{
	public enum Plurality { Single, Collection }
	public enum SchemaType { Attribute, Value, Element, Reference, ElementReference }
	public enum JavaType { BuiltIn, Enum, Hetero, Class, Substituted, Wildcard }
	
	public Plurality plurality();
	public SchemaType schemaType();
	public JavaType javaType();
}
