package org.jvnet.hyperjaxb.item;

import java.util.List;

/**
 * A specialized list interface that synchronizes a high-level JAXB
 * list with a low-level persistent list of {@link Item} wrappers.
 * <p>
 * This class facilitates "Roundtrip" persistence where a list of simple
 * types (e.g., {@code List<String>}) must be persisted as a collection
 * of entities (e.g., {@code List<Item<String>>}) to satisfy JPA
 * requirements.
 * </p>
 *
 * It is an interface to manage the synchronization between a
 * JAXB-managed list (the {@code ListType)} and the JPA-persisted
 * collection of wrapper objects (the {@code ItemType} wrappers).
 *
 * <p>It ensures that when you add or remove an item from a
 * Java list, the corresponding database records are updated
 * correctly during the JPA flush process.</p>
 *
 * @param <ListType> generic type of elements in the JAXB-visible list.
 * @param <ItemType> generic type of the {@link Item} wrapper used for persistence.
 */
public interface ItemList<ListType, ItemType extends Item<ListType>>
	extends List<ListType>
{
	/**
	 * Factory method to create a new instance of an ItemList,
	 * wrapping an existing JAXB-managed list.
	 * <p>
	 * This method is used by generated schema-derived classes to
	 * initialize collection properties that require JPA-compatible
	 * item wrapping. It establishes the bidirectional link between
	 * the visible list of values and the internal list of persistent
	 * {@link Item} entities.
	 * </p>
	 *
	 * @param item the underlying list of persistent items to be managed.
	 *
	 * @return a new {@code ItemList} instance synchronized with the
	 *         provided item list.
	 *
	 * @see Item
	 */
	public ItemType create(ListType item);
}
