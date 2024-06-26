package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
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

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "M", namespace = "")
@XmlType(name = "M", propOrder = { "id", "one", "two", "mthree" })
@Entity
@Table(name = "table_m")
public class M implements Equals
{
	private String id;
	@Id
	@XmlAttribute
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	@Transient
	private boolean isIdSet() { return (id != null); }

	@Transient
	public String getOne()
	{
		if (getOneTwo() == null)
			setOneTwo(new OneTwo());
		return getOneTwo().getOne();
	}
	public void setOne(String one)
	{
		if (getOneTwo() == null)
			setOneTwo(new OneTwo());
		getOneTwo().setOne(one);
	}

	@Transient
	public String getTwo()
	{
		if (getOneTwo() == null)
			setOneTwo(new OneTwo());
		return getOneTwo().getTwo();
	}
	public void setTwo(String two)
	{
		if (getOneTwo() == null)
			setOneTwo(new OneTwo());
		getOneTwo().setTwo(two);
	}

	private OneTwo oneTwo;
	@ManyToOne(targetEntity = OneTwo.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@XmlTransient
	public OneTwo getOneTwo() { return oneTwo; }
	public void setOneTwo(OneTwo oneTwo) { this.oneTwo = oneTwo; }
	@Transient
	private boolean isOneTwoSet() { return (oneTwo != null); }

	private String mthree;
	@Basic
	public String getMthree() { return mthree; }
	public void setMthree(String mthree) { this.mthree = mthree; }
	@Transient
	private boolean isMthreeSet() { return (mthree != null); }

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
		if (!(object instanceof M))
			return false;

		if (this == object)
			return true;

		final M that = (M) object;
		return strategy.equals(thisLocator, thatLocator, this.getId(), that.getId(), this.isIdSet(), that.isIdSet())
			&& strategy.equals(thisLocator, thatLocator, this.getOne(), that.getOne(), this.isOneTwoSet(), that.isOneTwoSet())
			&& strategy.equals(thisLocator, thatLocator, this.getTwo(), that.getTwo(), this.isOneTwoSet(), that.isOneTwoSet())
			&& strategy.equals(thisLocator, thatLocator, this.getMthree(), that.getMthree(), this.isMthreeSet(), that.isMthreeSet());
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
