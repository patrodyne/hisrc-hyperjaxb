package org.jvnet.hyperjaxb.item;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MixedItemUtils
{
	public static <T, ItemType extends MixedItem<T>> ItemType create(Class<? extends ItemType> itemClass, T value)
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

	public static <T, ItemType extends MixedItem<T>> ItemType create(Class<? extends ItemType> itemClass, String value)
	{
		try
		{
			final ItemType item = itemClass.getDeclaredConstructor().newInstance();
			item.setText(value);
			return item;
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
		{
			throw new IllegalArgumentException("Error in default constructor.", ex);
		}
	}

	public static <T> boolean shouldBeWrapped(List<T> core)
	{
		if (core == null || !(core instanceof MixedItemList))
			return true;
		else
			return false;
	}

	public static <V, T extends V, ItemType extends MixedItem<T>> List<V> wrap(List<V> core, List<ItemType> items,
		Class<? extends ItemType> itemClass)
	{
		if (core == null || !(core instanceof MixedItemList))
		{
			final List<V> newCore = new DefaultMixedItemList<V, T, ItemType>(items, itemClass);
			if (core != null)
				newCore.addAll(core);
			return newCore;
		}
		else
			return core;
	}
}
