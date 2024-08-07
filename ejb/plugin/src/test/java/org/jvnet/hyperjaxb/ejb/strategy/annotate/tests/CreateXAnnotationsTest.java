package org.jvnet.hyperjaxb.ejb.strategy.annotate.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.ejb.strategy.annotate.DefaultCreateXAnnotations;

import ee.jakarta.xml.ns.persistence.orm.AttributeOverride;
import ee.jakarta.xml.ns.persistence.orm.Column;

public class CreateXAnnotationsTest {

	private final DefaultCreateXAnnotations createXAnnotations = new DefaultCreateXAnnotations();
	
	@Test
	public void createsAttributeOverrides()
	{
		AttributeOverride ao0 = new AttributeOverride();
		ao0.setName("a");
		ao0.setColumn(new Column());
		ao0.getColumn().setName("column_a");
		AttributeOverride ao1 = new AttributeOverride();
		ao1.setName("b");
		ao1.setColumn(new Column());
		ao1.getColumn().setName("column_b");
		List<AttributeOverride> aos = Arrays.asList(ao0, ao1);
		assertNotNull(createXAnnotations.createAttributeOverrides(aos));
	}
}
