package org.jvnet.hyperjaxb.ejb.tests.issues;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jvnet.basicjaxb.lang.Equals;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.HashCode;
import org.jvnet.basicjaxb.lang.HashCodeStrategy;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBHashCodeStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.jvnet.basicjaxb.locator.util.LocatorUtils;

/**
 * <p>
 * Java class for issueXXXType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="issueXXXType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "myIssueXXXType")
@Entity(name = "org.jvnet.hyperjaxb.ejb.tests.issues.MyIssueXXXType")
@Table(name = "MYISSUEXXXTYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(length = 63)
public class MyIssueXXXType implements Serializable, Equals, HashCode
{
	private static final long serialVersionUID = 20221201L;
	
	@XmlAttribute
	protected String value;
	@XmlAttribute(name = "Hjid")
	protected Long hjid;

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	@Basic
	@Column(name = "VALUE_")
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
	@Transient
	public boolean isSetValue()
	{
		return (value != null);
	}

	/**
	 * Gets the value of the hjid property.
	 * 
	 * @return possible object is {@link Long }
	 * 
	 */
	@Id
	@Column(name = "HJID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getHjid()
	{
		return hjid;
	}

	/**
	 * Sets the value of the hjid property.
	 * 
	 * @param value
	 *            allowed object is {@link Long }
	 * 
	 */
	public void setHjid(Long value)
	{
		this.hjid = value;
	}

	@Override
	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
			Object object, EqualsStrategy strategy) {
		if (!(object instanceof MyIssueXXXType)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		final MyIssueXXXType that = ((MyIssueXXXType) object);
		{
			String lhsValue = this.getValue();
			String rhsValue = that.getValue();
			boolean lhsValueIsSet = this.isSetValue();
			boolean rhsValueIsSet = that.isSetValue();
			if (!strategy.equals(LocatorUtils.property(thisLocator,
					"simpleSingle", lhsValue), LocatorUtils.property(thatLocator,
					"simpleSingle", rhsValue), lhsValue, rhsValue, lhsValueIsSet, rhsValueIsSet)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object object) {
		final EqualsStrategy strategy = JAXBEqualsStrategy.getInstance();
		return equals(null, null, object, strategy);
	}

	public void hashCode(HashCodeBuilder hashCodeBuilder) {
		hashCodeBuilder.append(this.getValue());
	}

	@Override
	public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
		int currentHashCode = 1;
		{
			String theValue = this.getValue();
			boolean theValueIsSet = this.isSetValue();
			currentHashCode = strategy.hashCode(LocatorUtils.property(locator,
					"value", theValue), currentHashCode, theValue, theValueIsSet);
		}

		return currentHashCode;
	}

	@Override
	public int hashCode() {
		final HashCodeStrategy strategy = JAXBHashCodeStrategy.getInstance();
		return this.hashCode(null, strategy);
	}

}
