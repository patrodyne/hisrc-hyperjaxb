package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.jvnet.hyperjaxb.item.Item;
import org.jvnet.basicjaxb.lang.Equals;
import org.jvnet.basicjaxb.lang.EqualsStrategy;
import org.jvnet.basicjaxb.lang.HashCode;
import org.jvnet.basicjaxb.lang.HashCodeStrategy;
import org.jvnet.basicjaxb.lang.JAXBEqualsStrategy;
import org.jvnet.basicjaxb.lang.JAXBHashCodeStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;
import org.jvnet.basicjaxb.locator.util.LocatorUtils;

@MappedSuperclass
public abstract class PrimitiveItem<T, V> implements Equals, HashCode, Item<V> {

	private T value;

	// Note: "value" renamed to "content" because it is an SQL keyword.
	@Basic
	@Column(name = "content")
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
			Object object, EqualsStrategy strategy) {
		if (!(object instanceof PrimitiveItem)) {
			return false;
		}
		if (this == object) {
			return true;
		}
		final PrimitiveItem that = ((PrimitiveItem) object);
		{
			Object lhsValue;
			lhsValue = this.getValue();
			Object rhsValue;
			rhsValue = that.getValue();
			if (!strategy.equals(LocatorUtils.property(thisLocator, "value",
					lhsValue), LocatorUtils.property(thatLocator, "value",
					rhsValue), lhsValue, rhsValue)) {
				return false;
			}
		}
		return true;
	}

	public boolean equals(Object object) {
		final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
		return equals(null, null, object, strategy);
	}

	public int hashCode(ObjectLocator locator, HashCodeStrategy hashCodeStrategy) {

		final T theValue;
		theValue = this.getValue();
		return hashCodeStrategy.hashCode(LocatorUtils.property(locator, "value",
				theValue), 0, theValue);
	}

	public int hashCode() {
		return hashCode(null, JAXBHashCodeStrategy.INSTANCE);
	}

}
