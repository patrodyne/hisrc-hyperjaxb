package org.jvnet.hyperjaxb.item;

import jakarta.xml.bind.JAXBElement;

/**
 * Represents a persistent wrapper for items within a mixed content collection.
 * <p>
 * In XML, mixed content allows for a sequence of text nodes and child elements.
 * This interface extends the standard {@link Item} to provide specific
 * accessors for both raw string content and structured {@link JAXBElement}
 * content, enabling JPA to persist heterogeneous XML data.
 * </p>
 *
 * @param <T> the base type of the item being wrapped (often {@code Object}
 *            or {@code Serializable}).
 */
public interface MixedItem<T> extends Item<T>
{
    /**
     * Gets the text content of the mixed item.
     * <p>
     * This is typically used when the item represents a raw text node
     * within the XML structure.
     * </p>
     *
     * @return the string value of the text node, or {@code null} if this
     *         item represents an element node.
     */
	public String getText();

    /**
     * Sets the text content for this mixed item.
     *
     * @param text the text content to set.
     */
	public void setText(String text);
}
