package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
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
@XmlType(name = "OneTwo", propOrder = { "one", "two" })
@Entity
@Table(name = "table_onetwo")
public class OneTwo implements Equals
{
	@XmlAttribute(name = "Hjid")
	protected Long hjid;
	@Id
	@Column(name = "HJID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getHjid() { return hjid; }
	public void setHjid(Long value) { this.hjid = value; }

	private String one;
	@Basic
	public String getOne() { return one; }
	public void setOne(String one) { this.one = one; }
	@Transient
	private boolean isOneSet() { return (one != null); }

	private String two;
	@Basic
	public String getTwo() { return two; }
	public void setTwo(String two) { this.two = two; }
	@Transient
	private boolean isTwoSet() { return (two != null); }

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
		if (!(object instanceof OneTwo))
			return false;
		
		if (this == object)
			return true;
		
		final OneTwo that = (OneTwo) object;
		return strategy.equals(thisLocator, thatLocator, this.getOne(), that.getOne(), this.isOneSet(), that.isTwoSet())
			&& strategy.equals(thisLocator, thatLocator, this.getTwo(), that.getTwo(), this.isTwoSet(), that.isTwoSet());
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
