package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
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
@XmlType(name = "L", propOrder = { "id", "one", "two", "lthree" })
@Entity
@Table(name = "table_kl")
@SecondaryTable(name = "table_l", pkJoinColumns = { @PrimaryKeyJoinColumn(name = "id") })
public class L implements Equals
{
	@XmlAttribute
	private String id;
	@Id
	// @Column(table="table_l")
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	@Transient
	private boolean isIdSet() { return (id != null); }

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

	private String lthree;
	@Basic
	@Column(table = "table_l")
	public String getLthree() { return lthree; }
	public void setLthree(String lthree) { this.lthree = lthree; }
	@Transient
	private boolean isLthreeSet() { return (lthree != null); }

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
		if (!(object instanceof L))
			return false;
		
		if (this == object)
			return true;
		
		final L that = (L) object;
		return strategy.equals(thisLocator, thatLocator, this.getId(), that.getId(), this.isIdSet(), that.isIdSet())
			&& strategy.equals(thisLocator, thatLocator, this.getOne(), that.getOne(), this.isOneSet(), that.isOneSet())
			&& strategy.equals(thisLocator, thatLocator, this.getTwo(), that.getTwo(), this.isTwoSet(), that.isTwoSet())
			&& strategy.equals(thisLocator, thatLocator, this.getLthree(), that.getLthree(), this.isLthreeSet(), that.isLthreeSet());
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
