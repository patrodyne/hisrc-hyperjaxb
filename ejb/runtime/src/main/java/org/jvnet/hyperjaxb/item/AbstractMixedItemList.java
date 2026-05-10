package org.jvnet.hyperjaxb.item;

import java.util.AbstractList;
import java.util.List;

/**
 * An abstract base class for {@link ItemList} implementations that support
 * mixed content persistence.
 *
 * <p>Mixed content occurs in XML when an element contains both text and
 * child elements:
 * </p>
 *
 * {@code <note>Important: <urgent>now</urgent></note>}
 *
 * <p>
 * This class bridges the gap between a "mixed" JAXB list (which may contain
 * both strings and JAXB elements) and a persistent collection of
 * {@link Item} wrappers. It ensures that both text nodes and element nodes
 * are correctly wrapped, unwrapped, and synchronized with the database.
 * </p>
 *
 * <p><b>Key Differences from {@code AbstractItemList}</b></p>
 * <ul>
 * <li><b>EffectiveListType:</b> In standard lists, the item in the list is the same as the
 * item in the wrapper. In mixed content, the list might be defined as {{@code List<Object>},
 * where some objects are {@code String} and others are {@code JAXBElement}.
 * {@code EffectiveListType} captures this common denominator.</li>
 * <li><b>Heterogeneous Handling:</b> While {@code AbstractItemList} typically handles one-to-one
 * value-to-item mapping, {@code AbstractMixedItemList} is designed for scenarios where the runtime
 * must distinguish between different types of content stored in the same database table (often
 * using JPA's Single Table Inheritance).</li>
 * </ul>
 *
 * @param <EffectiveListType> the common super-type of the values in the
 *                            JAXB list (e.g., {@code Object} or {@code Serializable}).
 * @param <ListType> generic specific type of the data being managed.
 * @param <ItemType> generic persistent {@link Item} entity that wraps the
 *                   {@code ListType} for JPA storage.
 */
public abstract class AbstractMixedItemList<EffectiveListType, ListType extends EffectiveListType, ItemType extends MixedItem<ListType>>
	extends AbstractList<EffectiveListType>
	implements MixedItemList<EffectiveListType, ListType, ItemType>
{
    /**
     * The underlying list of {@link Item} entities that are directly
     * persisted in the relational database.
     */
	protected final List<ItemType> itemTypeList;

    /**
     * Constructs a new mixed list backed by the provided core
     * persistent collection.
     *
     * @param itemTypeList the ItemType list of items managed by the JPA provider.
     */
	public AbstractMixedItemList(final List<ItemType> itemTypeList)
	{
		super();

		if ( itemTypeList == null )
			throw new IllegalArgumentException("ItemType list must not be null.");

		this.itemTypeList = itemTypeList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EffectiveListType get(int index)
	{
		final ItemType item = itemTypeList.get(index);
		if ( item.getText() != null )
			return (EffectiveListType) item.getText();
		else
			return item.getItem();
	}

	@Override
	@SuppressWarnings("unchecked")
	public EffectiveListType set(int index, EffectiveListType element)
	{
		final ItemType oldItem = itemTypeList.get(index);
		final EffectiveListType oldValue;

		if ( oldItem.getText() != null )
			oldValue = (EffectiveListType) oldItem.getText();
		else
			oldValue = oldItem.getItem();

		if ( element instanceof String )
			oldItem.setText((String) element);
		else
			oldItem.setItem((ListType) element);

		return oldValue;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(int index, EffectiveListType element)
	{
		final ItemType item;
		if ( element instanceof String )
			item = create((String) element);
		else
			item = create((ListType) element);
		itemTypeList.add(index, item);
	}

	@Override
	public EffectiveListType remove(int index)
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
