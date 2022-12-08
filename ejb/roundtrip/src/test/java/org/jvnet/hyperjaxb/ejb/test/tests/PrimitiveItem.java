package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import org.jvnet.hyperjaxb.item.Item;
import org.jvnet.basicjaxb.lang.Equals2;
import org.jvnet.basicjaxb.lang.EqualsStrategy2;
import org.jvnet.basicjaxb.lang.HashCode2;
import org.jvnet.basicjaxb.lang.HashCodeStrategy2;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBHashCodeStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.jvnet.basicjaxb.locator.util.LocatorUtils;

@MappedSuperclass
public abstract class PrimitiveItem<T, V> implements Equals2, HashCode2, Item<V>
{
	private T value;
	// Note: "value" renamed to "content" because it is an SQL keyword.
	@Basic
	@Column(name = "content")
	public T getValue() { return value; }
	public void setValue(T value) { this.value = value; }
	@Transient
	private boolean isValueSet() { return (value != null); }

	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy2 strategy)
	{
		if (!(object instanceof PrimitiveItem))
			return false;
		
		if (this == object)
			return true;
		
		@SuppressWarnings("unchecked")
		final PrimitiveItem<T, V> that = ((PrimitiveItem<T, V>) object);
		
		Object lhsValue = this.getValue();
		Object rhsValue = that.getValue();
		
		ObjectLocator lhsValueLocator = LocatorUtils.property(thisLocator, "value", lhsValue);
		ObjectLocator rhsValueLocator = LocatorUtils.property(thatLocator, "value", rhsValue);
		
		boolean lhsValueSet = this.isValueSet();
		boolean rhsValueSet = that.isValueSet();
		
		return strategy.equals(lhsValueLocator, rhsValueLocator, lhsValue, rhsValue, lhsValueSet, rhsValueSet);
	}

	public boolean equals(Object object)
	{
		final EqualsStrategy2 strategy = JAXBEqualsStrategy.getInstance();
		return equals(null, null, object, strategy);
	}

	public int hashCode(ObjectLocator locator, HashCodeStrategy2 hashCodeStrategy)
	{
		T theValue = this.getValue();
		ObjectLocator theValueLocator = LocatorUtils.property(locator, "value", theValue);
		boolean theValueSet = this.isValueSet();
		return hashCodeStrategy.hashCode(theValueLocator, 0, theValue, theValueSet);
	}

	public int hashCode()
	{
		return hashCode(null, JAXBHashCodeStrategy.getInstance());
	}
}
