package org.jvnet.hyperjaxb.item;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Utility class providing static methods for managing {@link Item} collections
 * and {@link ItemList} instances.
 * <p>
 * This class contains the logic required to wrap and unwrap persistent lists,
 * helping bridge the gap between JAXB's object model and JPA's entity-based
 * persistence. It is used extensively by generated code to initialize
 * collection-based properties.
 * </p>
 * <p>
 * It primarily serves as the engine behind the {@code ItemList.create()} factory calls.
 * </p>
 * <p>
 * The actual wrappers ({@code itemClass}) depend on your XML schema and naming strategy,
 * but generally follow these patterns:
 * </p>
 * <ol>
 * <li><b>Simple Type Wrappers</b>
 *   <ul>
 *   <li><b>StringItem:</b> Wraps raw {@code java.lang.String} values.</li>
 *   <li><b>IntegerItem:</b> Wraps {@code java.lang.Integer} or {@code int}.</li>
 *   <li><b>DoubleItem:</b> Wraps {@code java.lang.Double}.</li>
 *   <li><b>DateItem:</b> Wraps {@code java.util.Date} or converted {@code XMLGregorianCalendar} objects.</li>
 *   </ul></li>
 * <li><b>Schema-Specific Names</b>
 *   <ul>
 *   <li><b>Element:</b> For an element named {@code <Tag>}, the generated wrapper might be {@code TagItem}.</li>
 *   <li><b>Property:</b> For a list property named categories, the wrapper might be {@code CategoriesItem}.</li>
 *   </ul></li>
 * <li><b>Mixed Content Wrappers</b>
 * For elements with mixed content (text and elements), a more generic base class is often used.
 *   <ul>
 *   <li><b>ObjectItem:</b> Used as a catch-all for mixed content where multiple different types are stored in the same collection.</li>
 *   </ul></li>
 * <li><b>Custom Naming</b>
 * If you have implemented a custom {@code Naming} strategy, your {@code itemClass} values will follow whatever
 * logic you defined (e.g., adding a specific prefix or suffix like {@code _PersistentWrapper}).</li>
 * </ol>
 */
public class ItemUtils
{
	/**
	 * Creates and initializes a single {@link Item} entity instance for the given value.
	 * <p>
	 * This utility method uses reflection to instantiate a new instance of the
	 * specified {@code itemClass} and populates it with the provided value.
	 * It is often used during the conversion process or when manually
	 * adding new records to a persistent collection.
	 * </p>
	 * <p>
	 * The {@code DefaultItemList.create(ListType)} method typically delegates its work to
	 * this utility method to perform the actual object creation.
	 * </p>
	 *
	 * @param <T> the type of the value to be wrapped.
	 * @param <ItemType> the type of the persistent entity implementing {@link Item}.
	 * @param itemClass the concrete class of the item entity to instantiate.
	 * @param value the value to be stored within the new item instance.
	 *
	 * @return a new, initialized instance of the specified item class containing the value.
	 *
	 * @throws IllegalArgumentException if the {@code itemClass} cannot be
	 *                                  instantiated (e.g., missing default constructor).
	 * @see Item#setItem(Object)
	 */
	public static <T, ItemType extends Item<T>> ItemType create(Class<? extends ItemType> itemClass, T value)
	{
		try
		{
			final ItemType item = itemClass.getDeclaredConstructor().newInstance();
			item.setItem(value);
			return item;
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
			NoSuchMethodException | SecurityException ex)
		{
			throw new IllegalArgumentException("Error in default constructor.", ex);
		}
	}

	/**
	 * Evaluates whether the provided list needs to be wrapped in an {@link ItemList}
	 * for persistence synchronization.
	 * <p>
	 * This check is typically performed in generated getters to determine if the
	 * current collection is a standard JAXB list that needs to be synchronized with
	 * a persistent "core" list of {@link Item} entities.
	 * </p>
	 * <p>
	 * It guards against redundant wrapping of lists that are already managed
	 * by the {@code org.jvnet.hyperjaxb.item} runtime.
	 * </p>
	 *
	 * @param <T> the type of elements in the list.
	 * @param core the list to evaluate; may be {@code null}.
	 *
	 * @return {@code true} if the list is not null and is not already an
	 *         instance of {@link ItemList}; {@code false} otherwise.
	 */
	public static <T> boolean shouldBeWrapped(List<T> core)
	{
		return (core == null || !(core instanceof ItemList));
	}

	/**
	 * Synchronizes an existing list of values with a core list of persistent
	 * item entities.
	 * <p>
	 * This method iterates through the provided {@code list} of raw values,
	 * wraps each value in an instance of {@code itemClass}, and populates
	 * the {@code core} list. This is primarily used when a detached or
	 * newly-unmarshalled JAXB object is being prepared for JPA persistence.
	 * </p>
	 *
	 * @param <T> the type of the raw values in the JAXB list.
	 * @param <ItemType> the type of the persistent {@link Item} wrappers.
	 * @param items the source list of raw values to be persisted.
	 * @param coreList the target list of JPA entities to be populated/synchronized.
	 * @param itemClass the concrete class of the item entity used for
	 *                  reflective instantiation.
	 *
	 * @return the updated {@code core} list containing the persistent wrappers.
	 *
	 * @throws IllegalArgumentException if the {@code itemClass} cannot be
	 *                                  instantiated.
	 * @see #create(Class, Object)
	 */
	public static <T, ItemType extends Item<T>> List<T> wrap(List<T> coreList, List<ItemType> items,
		Class<? extends ItemType> itemClass)
	{
		if (coreList == null || !(coreList instanceof ItemList))
		{
			final List<T> newCore = new DefaultItemList<T, ItemType>(items, itemClass);
			if (coreList != null)
				newCore.addAll(coreList);
			return newCore;
		}
		else
			return coreList;
	}
}
