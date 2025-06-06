package org.jvnet.hyperjaxb.item;

import java.util.List;

public interface MixedItemList<EffectiveListType, ListType, ItemType extends MixedItem<ListType>> extends
		List<EffectiveListType> {

	public ItemType create(ListType item);

	public ItemType create(String item);
}
