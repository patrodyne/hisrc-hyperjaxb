package org.jvnet.hyperjaxb.item;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import jakarta.xml.bind.JAXBElement;

/**
 * Utility class providing static methods for managing {@link MixedItem} collections
 * and {@link MixedItemList} instances.
 * <p>
 * This class facilitates the persistence of XML mixed content by providing
 * centralized logic to wrap and unwrap lists that contain both raw strings
 * (text nodes) and {@link JAXBElement} objects. It is used by generated
 * code to initialize synchronized, database-backed mixed collections.
 * </p>
 */
public class MixedItemUtils
{
	/**
	 * Creates and initializes a new {@link MixedItem} entity instance for the
	 * provided value of type {@code T}.
	 * <p>
	 * This utility method reflectively instantiates the specified {@code itemClass}
	 * and populates it using the {@link Item#setItem(Object)} method. It serves as
	 * a generic factory for mixed item entities when the specific XML node type
	 * (text vs. element) is handled by the underlying item's primary data field.
	 * </p>
	 *
	 * @param <T> the type of the value being wrapped.
	 * @param <ItemType> the persistent entity type implementing {@link MixedItem}.
	 * @param itemClass the concrete class literal of the persistent entity to instantiate.
	 * @param value the value to be stored within the item.
	 *
	 * @return a new, initialized persistent {@code ItemType} instance.
	 *
	 * @throws IllegalArgumentException if the {@code itemClass} cannot be
	 *                                  instantiated (e.g., missing default constructor).
	 * @see Item#setItem(Object)
	 */
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

	/**
	 * Creates and initializes a new {@link MixedItem} entity instance specifically
	 * for a text node (String value).
	 * <p>
	 * This utility method reflectively instantiates the provided {@code itemClass}
	 * and populates it using {@link MixedItem#setText(String)}. This distinguishes
	 * the data as raw text content rather than a structured XML element, allowing
	 * the persistence layer to store it in the appropriate column or with the
	 * correct discriminator.
	 * </p>
	 *
	 * @param <ItemType> the persistent entity type implementing {@link MixedItem}.
	 * @param itemClass the concrete class of the item entity to instantiate (e.g., {@code MyMixedItem.class}).
	 * @param value the raw string content (text node) to be wrapped.
	 *
	 * @return a new, initialized persistent {@code ItemType} instance containing the text.
	 *
	 * @throws IllegalArgumentException if the {@code itemClass} cannot be instantiated.
	 * @see MixedItem#setText(String)
	 */
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

	/**
	 * Determines whether the given list requires wrapping in a specialized
	 * ItemList implementation.
	 * <p>
	 * This check is primarily used in generated getters to verify if a
	 * collection property has already been synchronized with the JPA-managed
	 * "core" list. If the list is not null and does not already implement
	 * {@link ItemList}, it indicates that the list is a standard JAXB collection
	 * (e.g., from unmarshalling) that must be transitioned to the persistent
	 * wrapper structure.
	 * </p>
	 *
	 * @param <T> the type of elements contained in the list.
	 * @param core the list to be checked; may be {@code null}.
	 *
	 * @return {@code true} if the list is non-null and is NOT an instance
	 *         of {@link ItemList}; {@code false} otherwise.
	 */
	public static <T> boolean shouldBeWrapped(List<T> core)
	{
		return (core == null || !(core instanceof MixedItemList));
	}

	/**
	 * Synchronizes and wraps a list of mixed content values into a core list of
	 * persistent item entities.
	 * <p>
	 * This method iterates through the source list {@code list}, which typically
	 * contains a mixture of {@link String} objects (text nodes) and
	 * {@link jakarta.xml.bind.JAXBElement} objects (element nodes). Each value is
	 * converted into a persistent {@code ItemType} instance using the specified
	 * {@code itemClass} and then added to the {@code core} list.
	 * </p>
	 * <p>
	 * This process ensures that the transient JAXB collection is correctly
	 * mirrored in the database-backed "core" collection while maintaining
	 * the relative order of text and elements.
	 * </p>
	 *
	 * @param <V> the type of values in the source list (typically {@code Object}).
	 * @param <ItemType> the type of the persistent entity implementing {@link MixedItem}.
	 * @param items the source list of mixed content to be wrapped.
	 * @param core the target persistent list (JPA core) to be populated.
	 * @param itemClass the concrete class of the mixed item entity used for instantiation.
	 *
	 * @return the populated {@code core} list containing the persistent wrappers.
	 *
	 * @throws IllegalArgumentException if the {@code itemClass} cannot be instantiated.
	 *
	 * @see #create(Class, String)
	 */
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
