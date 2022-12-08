package org.jvnet.hyperjaxb.item;

public interface Item<T>
{
	public T getItem();
	public void setItem(T value);

	// public T getValue();
	// public void setValue(T value);
}
