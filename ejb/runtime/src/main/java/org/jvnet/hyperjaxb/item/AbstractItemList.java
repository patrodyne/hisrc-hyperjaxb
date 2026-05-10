package org.jvnet.hyperjaxb.item;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

/**
 * An abstract base class for {@link ItemList} implementations that bridges
 * a high-level JAXB-managed list with a low-level persistent list of
 * {@link Item} wrappers.
 * <p>
 * This class extends {@link AbstractList} to provide a skeleton
 * implementation of the {@link List} interface, reducing the effort
 * required to implement persistent collection synchronization.
 * </p>
 *
 * @param <ListType> generic type of the user-visible elements in the JAXB list.
 * @param <ItemType> generic type of the internal {@link Item} entity used
 *                   for JPA persistence.
 */
public abstract class AbstractItemList<ListType, ItemType extends Item<ListType>>
	extends AbstractList<ListType>
	implements ItemList<ListType, ItemType>, Serializable
{
	private static final long serialVersionUID = -6512320214488719797L;

    /**
     * The underlying list of {@link Item} wrappers that are directly
     * managed by the JPA provider.
     */
	protected final List<ItemType> itemTypeList;

    /**
     * Constructs a new abstract list using the specified ItemType
     * persistence list.
     *
     * @param itemList the persistent collection to be wrapped.
     */
	public AbstractItemList(final List<ItemType> itemList)
	{
		super();
		if ( itemList == null )
			throw new IllegalArgumentException("ItemType list must not be null.");
		this.itemTypeList = itemList;
	}

	@Override
	public ListType get(int index)
	{
		final ItemType item = itemTypeList.get(index);
		return item.getItem();
	}

	@Override
	public ListType set(int index, ListType element)
	{
		final ItemType oldItem = itemTypeList.get(index);
		final ListType oldValue = oldItem.getItem();
		oldItem.setItem(element);
		return oldValue;
	}

	@Override
	public void add(int index, ListType element)
	{
		final ItemType item = create(element);
		itemTypeList.add(index, item);
	}

	@Override
	public ListType remove(int index)
	{
		final ItemType item = itemTypeList.remove(index);
		return item.getItem();
	}

	@Override
	public int size()
	{
		return itemTypeList.size();
	}
}
