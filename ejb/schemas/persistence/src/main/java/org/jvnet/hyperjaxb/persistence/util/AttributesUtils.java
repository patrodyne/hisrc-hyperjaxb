package org.jvnet.hyperjaxb.persistence.util;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.Basic;
import ee.jakarta.xml.ns.persistence.orm.ElementCollection;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import ee.jakarta.xml.ns.persistence.orm.Embedded;
import ee.jakarta.xml.ns.persistence.orm.EmbeddedId;
import ee.jakarta.xml.ns.persistence.orm.Id;
import ee.jakarta.xml.ns.persistence.orm.ManyToMany;
import ee.jakarta.xml.ns.persistence.orm.ManyToOne;
import ee.jakarta.xml.ns.persistence.orm.OneToMany;
import ee.jakarta.xml.ns.persistence.orm.OneToOne;
import ee.jakarta.xml.ns.persistence.orm.Transient;
import ee.jakarta.xml.ns.persistence.orm.Version;

public class AttributesUtils {

	private AttributesUtils() {

	}

	public static Object getAttribute(Object attributes, String name) {
		if (attributes == null || name == null) {
			return null;
		} else if (attributes instanceof Attributes) {
			return getAttribute((Attributes) attributes, name);
		} else if (attributes instanceof EmbeddableAttributes) {
			return getAttribute((EmbeddableAttributes) attributes, name);
		} else {
			throw new IllegalArgumentException(
					"Illegal attributes object class [" + attributes.getClass()
							+ "].");
		}
	}

	public static Object getAttribute(Attributes attributes, String name) {
		if (attributes == null || name == null) {
			return null;
		} else {
			for (final Id attribute : attributes.getId()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}

			{
				final EmbeddedId attribute = attributes.getEmbeddedId();
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}

			for (final Basic attribute : attributes.getBasic()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}

			for (final Version attribute : attributes.getVersion()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final ManyToOne attribute : attributes.getManyToOne()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final OneToMany attribute : attributes.getOneToMany()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final OneToOne attribute : attributes.getOneToOne()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final ManyToMany attribute : attributes.getManyToMany()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final ElementCollection attribute : attributes
					.getElementCollection()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final Embedded attribute : attributes.getEmbedded()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final Transient attribute : attributes.getTransient()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			return null;
		}
	}

	public static Object getAttribute(EmbeddableAttributes attributes,
			String name) {
		if (attributes == null || name == null) {
			return null;
		} else {
			for (final Basic attribute : attributes.getBasic()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}

			for (final ManyToOne attribute : attributes.getManyToOne()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final OneToMany attribute : attributes.getOneToMany()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final OneToOne attribute : attributes.getOneToOne()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final ManyToMany attribute : attributes.getManyToMany()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final ElementCollection attribute : attributes
					.getElementCollection()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final Embedded attribute : attributes.getEmbedded()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			for (final Transient attribute : attributes.getTransient()) {
				if (attribute != null && name.equals(attribute.getName())) {
					return attribute;
				}
			}
			return null;
		}
	}

}
