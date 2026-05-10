package org.jvnet.hyperjaxb.item;

/**
 * Represents a <em>wrapper</em> for an item of type {@code T} within
 * a JAXB collection, facilitating its persistence in a relational
 * database using JPA.
 *
 * <p>
 * This class is typically used during the generation of persistent entities
 * from XML schema definitions, allowing for the mapping of JAXB lists to
 * database tables.
 * </p>
 *
 * <p><b>Wrapper:</b> In the context of HyperJAXB, a wrapper is a structural
 * object used to bridge the gap between how JAXB handles XML lists and how
 * JPA handles database collections.</p>
 *
 * <p>Specifically, the {@code Item<T>} class acts as an adapter to solve three
 * main technical challenges:</p>
 *
 * <ol>
 * <li><b>Enabling JPA Persistence for Simple Types</b>
 * <p>JAXB often generates simple lists like {@code List<String>}.
 * However, JPA typically requires that items stored in a collection
 * are themselves entities with a primary key if they are to be stored
 * in a separate join table.</p>
 * <ul>
 * <li><b>The Problem:</b> You cannot add JPA annotations (like {@code @Id}
 * or {@code @Column)} directly to a raw {@code java.lang.String}.</li>
 * <li><b>The Wrapper Solution:</b> The {@code Item<String>} object "wraps"
 * the string. This allows HyperJAXB to annotate the {@code Item} class as a
 * JPA entity, giving that string a place to live in the database as a row
 * with its own identifier.</li>
 * </ul></li>
 * <li><b>Managing "Orphan" Elements</b>
 * <p>In XML, a list is just a sequence of elements. In a relational database,
 * those elements must often be managed as individual records. The Item wrapper
 * provides a stable <b>identity</b> for each member of the list, ensuring that
 * when you update one item in a collection of 100, JPA knows exactly which
 * database row to target.</p></li>
 * <li><b>Structural Compatibility (The "Impedance Mismatch")</b>
 * <p>JPA and JAXB have different rules for what is "valid":</p>
 * <ul>
 * <li><b>JAXB</b> is happy with any list of objects.</li>
 * <li><b>JPA</b> needs specific metadata to understand how to map those objects to
 * tables, columns, and foreign keys.</li>
 * <li><b>The Wrapper</b> acts as a middle-man that JAXB sees as a "natural" part
 * of the XML structure, but JPA sees as a fully-mapped database entity.</li>
 * </ul></li>
 * </ol>
 *
 * <p><b>Visual Summary</b></p>
 * <p>Instead of a direct (but un-mappable) relationship:</p>
 * {@code Parent --> List<String>}
 * <p>HyperJAXB creates a wrapped relationship:</p>
 * {@code Parent --> List<Item<String>> --> String}
 *
 * <p>This allows the HyperJAXB to add an @Id field to the
 * {@code Item} class so it can be saved to its parent table.</p>
 *
 * @param <T> the type of the item being wrapped.
 */
public interface Item<T>
{
    /**
     * Gets the item value wrapped by this item container.
     *
     * @return the wrapped item value.
     */
	public T getItem();

    /**
     * Sets the item value for this item container.
     *
     * @param value the item value to set.
     */
	public void setItem(T value);
}
