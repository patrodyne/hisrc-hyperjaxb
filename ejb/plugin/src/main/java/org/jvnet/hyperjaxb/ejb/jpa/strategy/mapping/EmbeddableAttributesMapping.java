package org.jvnet.hyperjaxb.ejb.jpa.strategy.mapping;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;

import org.jvnet.hyperjaxb.ejb.plugin.EJBPlugin;
import org.jvnet.hyperjaxb.ejb.strategy.Variant;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.ClassOutlineMapping;
import org.jvnet.hyperjaxb.ejb.strategy.mapping.Mapping;

import com.sun.tools.xjc.outline.ClassOutline;

import ee.jakarta.xml.ns.persistence.orm.Attributes;
import ee.jakarta.xml.ns.persistence.orm.EmbeddableAttributes;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(APPLICATION + 1)
@Variant(type = Variant.Type.JPA)
public class EmbeddableAttributesMapping implements ClassOutlineMapping<EmbeddableAttributes>
{
	private EJBPlugin plugin;
	protected EJBPlugin getPlugin() { return plugin; }
	protected void setPlugin(EJBPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public EmbeddableAttributes process(Mapping context, ClassOutline classOutline)
	{
		final Attributes attributes = context.getAttributesMapping().process(context, classOutline);
		final EmbeddableAttributes embeddableAttributes = new EmbeddableAttributes();
		embeddableAttributes.getBasic().addAll(attributes.getBasic());
		embeddableAttributes.getManyToOne().addAll(attributes.getManyToOne());
		embeddableAttributes.getOneToMany().addAll(attributes.getOneToMany());
		embeddableAttributes.getOneToOne().addAll(attributes.getOneToOne());
		embeddableAttributes.getManyToMany().addAll(attributes.getManyToMany());
		embeddableAttributes.getElementCollection().addAll(attributes.getElementCollection());
		embeddableAttributes.getEmbedded().addAll(attributes.getEmbedded());
		embeddableAttributes.getTransient().addAll(attributes.getTransient());
		//	TODO Report errors
		//	for (Id id : attributes.getId())
		//	{
		//		final Basic basic = new Basic();
		//		basic.setName(id.getName());
		//		basic.setAccess(id.getAccess());
		//		basic.setColumn(id.getColumn());
		//		basic.setTemporal(id.getTemporal());
		//		embeddableAttributes.getBasic().add(basic);
		//	}
		//	for (Version version : attributes.getVersion())
		//	{
		//		final Basic basic = new Basic();
		//		basic.setName(version.getName());
		//		basic.setAccess(version.getAccess());
		//		basic.setColumn(version.getColumn());
		//		basic.setTemporal(version.getTemporal());
		//		embeddableAttributes.getBasic().add(basic);
		//	}
		//	if (attributes.getEmbeddedId() != null)
		//	{
		//		final EmbeddedId embeddedId = attributes.getEmbeddedId();
		//		final Embedded embedded = new Embedded();
		//		embedded.setName(embeddedId.getName());
		//		embedded.setAccess(embeddedId.getAccess());
		//		embedded.getAttributeOverride().addAll(
		//		embeddedId.getAttributeOverride());
		//		embeddableAttributes.getEmbedded().add(embedded);
		//	}
		return embeddableAttributes;
	}
}
