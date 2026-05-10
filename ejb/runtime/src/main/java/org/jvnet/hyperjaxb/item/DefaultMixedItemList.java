package org.jvnet.hyperjaxb.item;

import java.io.Serializable;
import java.util.List;

/**
 * Default implementation for managing persistent mixed content collections.
 * <p>
 * This class handles the synchronization between a JAXB list containing mixed
 * types (e.g., {@code String} and {@code JAXBElement}) and a core list of
 * {@link Item} wrappers persisted via JPA. It leverages the {@link Item}
 * interface to bridge the gap between XML's flexible content model and
 * the relational database's structured schema.
 * </p>
 *
 * @param <EffectiveListType> the common super-type of the values in the JAXB
 *                            list (typically {@code Object} or {@code Serializable}).
 * @param <ListType> generic specific data type held by the wrapper.
 * @param <ItemType> generic persistent entity type implementing {@link Item}
 *                   that wraps the {@code ListType}.
 */
public class DefaultMixedItemList<EffectiveListType, ListType extends EffectiveListType, ItemType extends MixedItem<ListType>>
	extends AbstractMixedItemList<EffectiveListType, ListType, ItemType>
	implements Serializable
{
	private static final long serialVersionUID = 20260501L;
	private final Class<? extends ItemType> itemClass;

    /**
     * Constructs a new mixed item list.
     *
     * @param listItem the underlying list of persistent items managed by JPA.
     */
	public DefaultMixedItemList(List<ItemType> listItem, final Class<? extends ItemType> itemClass)
	{
		super(listItem);
		this.itemClass = itemClass;
	}

	/**
	 * Returns the concrete class of the {@link Item} implementation
	 * used to persist the mixed content in this list.
	 * <p>
	 * This metadata is used by the internal {@code wrap} and {@code create}
	 * logic to reflectively instantiate the appropriate entity wrapper
	 * for the database. In mixed content scenarios, this class often
	 * serves as the base entity for a inheritance hierarchy (e.g., using
	 * JPA {@code InheritanceType.SINGLE_TABLE}).
	 * </p>
	 *
	 * @return the {@link Class} literal representing the concrete
	 *         persistent item implementation.
	 */
	public Class<? extends ItemType> getItemClass()
	{
		return itemClass;
	}

	/**
	 * Creates and initializes a new persistent {@link Item} instance for a specific
	 * value within a mixed content collection.
	 * <p>
	 * This method uses the class literal provided by {@link #getItemClass()} to
	 * reflectively instantiate the JPA entity wrapper. It then populates the
	 * entity with the provided value using {@link Item#setItem(Object)}.
	 * </p>
	 * <p>
	 * In mixed content scenarios, this method ensures that regardless of whether
	 * the input is a simple string or a complex element, it is wrapped in a
	 * uniform persistent type that the JPA provider can manage in the core collection.
	 * </p>
	 *
	 * @param item the specific data value (of type {@code ListType}) to be wrapped
	 *             for persistence.
	 *
	 * @return a new, initialized persistent {@code ItemType} containing the value.
	 *
	 * @throws RuntimeException if the {@code ItemType} cannot be instantiated via
	 *                          its default constructor.
	 * @see #getItemClass()
	 */
	@Override
	public ItemType create(ListType item)
	{
		return MixedItemUtils.create(getItemClass(), item);
	}

	/**
	 * Creates a new persistent {@link Item} instance specifically for a
	 * {@code String} value (text node) within a mixed content collection.
	 * <p>
	 * This method is invoked by the runtime when a raw string is added to
	 * the mixed list. It transforms the transient string into a persistent
	 * entity using the class identified by {@link #getItemClass()}.
	 * </p>
	 *
	 * <p>
	 * Text node creation is a distinct method to ensure that even "loose" text
	 * in an XML structure {@code <note>This is text <urgent/> and more text</note>}
	 * is captured as a distinct row in the associated item table, usually marked
	 * with a specific discriminator value if using JPA inheritance.
	 * </p>
	 *
	 * @param item the text content to be wrapped for persistence.
	 *
	 * @return a new persistent {@code ItemType} instance containing
	 *         the string value.
	 *
	 * @throws RuntimeException if the item wrapper cannot be instantiated.
	 */
	@Override
	public ItemType create(String item)
	{
		return MixedItemUtils.create(getItemClass(), item);
	}
}
