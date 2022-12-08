package org.jvnet.hyperjaxb.item;

import java.util.List;

/**
 * An interface to create an ItemType instance from an item of ListType.
 * 
 * @param <ListType> Generic for type of list.
 * @param <ItemType> Generic for type of item.
 */
public interface ItemList<ListType, ItemType extends Item<ListType>> extends List<ListType>
{
	public ItemType create(ListType item);
}
