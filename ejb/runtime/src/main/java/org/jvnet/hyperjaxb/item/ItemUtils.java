package org.jvnet.hyperjaxb.item;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ItemUtils
{
	public static <T, ItemType extends Item<T>> ItemType create(Class<? extends ItemType> itemClass, T value)
	{
		try
		{
			final ItemType item = itemClass.getDeclaredConstructor().newInstance();
			item.setItem(value);
			return item;
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
		{
			throw new IllegalArgumentException("Error in default constructor.", ex);
		}
	}

	public static <T> boolean shouldBeWrapped(List<T> core)
	{
		if (core == null || !(core instanceof ItemList))
			return true;
		else
			return false;
	}

	public static <T, ItemType extends Item<T>> List<T> wrap(List<T> core, List<ItemType> items,
		Class<? extends ItemType> itemClass)
	{
		if (core == null || !(core instanceof ItemList))
		{
			final List<T> newCore = new DefaultItemList<T, ItemType>(items, itemClass);
			if (core != null)
				newCore.addAll(core);
			return newCore;
		}
		else
			return core;
	}
}
