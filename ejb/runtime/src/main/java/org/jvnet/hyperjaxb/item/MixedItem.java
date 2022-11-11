package org.jvnet.hyperjaxb.item;

public interface MixedItem<T> extends Item<T> {

	public String getText();

	public void setText(String text);

}
