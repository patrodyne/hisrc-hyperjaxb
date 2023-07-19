package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import static org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jvnet.basicjaxb.lang.Equals;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.locator.DefaultRootObjectLocator;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.jvnet.basicjaxb.locator.RootObjectLocator;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "B", propOrder = { "id", "c" })
@Entity
@Table(name = "table_b")
public class B implements Equals
{
	@XmlAttribute
	private String id;
	@Id
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	@Transient
	private boolean isIdSet() { return (id != null); }

	private String c;
	public String getC() { return c; }
	public void setC(String value) { this.c = value; }
	@Transient
	private boolean isCSet() { return (c != null); }

	@XmlTransient
	private int version;
	@Version
	public int getVersion() { return version; }
	public void setVersion(int version) { this.version = version; }
	@Transient
	private boolean isVersionSet() { return true; }

	@Override
	public boolean equals(Object obj)
	{
		RootObjectLocator thisLocator = new DefaultRootObjectLocator(this);
		RootObjectLocator thatLocator = new DefaultRootObjectLocator(obj);
		return equals(thisLocator, thatLocator, obj, JAXBEqualsStrategy.getInstance());
	}

	@Override
	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
		Object object, EqualsStrategy strategy)
	{
		if (!(object instanceof B))
			return false;
		
		if (this == object)
			return true;
		
		final B that = (B) object;
		return strategy.equals(thisLocator, thatLocator, this.getId(), that.getId(), this.isIdSet(), that.isIdSet())
			&& strategy.equals(thisLocator, thatLocator, this.getC(), that.getC(), this.isCSet(), that.isCSet());
	}

	@Override
	public int hashCode()
	{
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, SIMPLE_STYLE);
	}
}