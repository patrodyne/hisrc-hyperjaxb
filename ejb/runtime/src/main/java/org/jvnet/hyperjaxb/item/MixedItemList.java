package org.jvnet.hyperjaxb.item;

import java.util.List;

/**
 * A specialized {@link ItemList} designed to manage XML mixed content collections.
 * <p>
 * Mixed content collections are unique because they may contain heterogeneous data types,
 * such as raw {@link String} objects (representing text nodes) and {@link jakarta.xml.bind.JAXBElement}
 * instances (representing child elements). This interface ensures that these diverse
 * types are correctly transformed into a uniform set of {@link Item} or {@link MixedItem}
 * persistent wrappers.
 * </p>
 *
 * <p><b>Key Differences from Standard {@code ItemList}</b></p>
 * <ul>
 * <li><b>Polymorphic Handling:</b> While a standard {@code ItemList} usually handles a single type
 * like {@code String}, a {@code MixedItemList} handles the transition from a multi-type list
 * (the {@code EffectiveListType} list) to a single-type entity list (the {@code ItemType} list).</li>
 * <li><b>Runtime Logic:</b> Implementation of this interface (such as {@code DefaultMixedItemList})
 * contains the logic to inspect whether an incoming object is a {@code String} or a {@code JAXBElement}
 * and route it to the appropriate persistence field in the wrapper.</li>
 * </ul>
 *
 * @param <EffectiveListType> the common type of elements as they appear in the
 *                            high-level JAXB list (typically {@code Object}).
 * @param <ListType> the underlying data type held by the wrapper.
 * @param <ItemType> the persistent entity type implementing {@link Item}
 *                   that wraps the {@code ListType}.
 */
public interface MixedItemList<EffectiveListType, ListType, ItemType extends MixedItem<ListType>>
	extends List<EffectiveListType>
{
	/**
	 * Creates a new persistent {@link Item} instance to wrap a specific data value.
	 * <p>
	 * This method is part of the factory pattern used by the mixed list to
	 * transform "inner" data types (the raw values stored within the JAXB elements
	 * or as text nodes) into the persistent {@code ItemType} entities that
	 * JPA can track in the database.
	 * </p>
	 *
	 * @param value the raw data value to be wrapped for persistence.
	 *
	 * @return a new instance of {@code ItemType} initialized with the provided value.
	 *
	 * @see MixedItem
	 * @see #create(String)
	 */
	public ItemType create(ListType value);

	/**
	 * Creates a new persistent {@link Item} instance specifically for a
	 * raw text node within a mixed content collection.
	 * <p>
	 * This method is invoked by the list's synchronization logic when it
	 * encounters a {@code String} in the high-level JAXB list. It wraps
	 * the text into an {@code ItemType} so that it can be persisted in
	 * the same "core" collection as the element-based items.
	 * </p>
	 *
	 * @param value the text content (text node) to be wrapped.
	 *
	 * @return a new persistent {@code ItemType} instance containing
	 *         the string value.
	 * @see MixedItem#setText(String)
	 */
	public ItemType create(String value);
}
