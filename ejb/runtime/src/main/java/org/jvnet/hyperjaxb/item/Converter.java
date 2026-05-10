package org.jvnet.hyperjaxb.item;

/**
 * Defines a strategy for converting values between an internal persistence
 * representation and an external object model representation.
 * <p>
 * This interface is typically used in conjunction with {@link ConvertedList}
 * to handle collections where the database type (Inner) differs from the
 * JAXB-bound Java type (Outer).
 * </p>
 *
 * @param <I> the Inner type: the representation used by the persistence
 *            back-end (e.g., {@code java.util.Date}).
 * @param <O> the Outer type: the representation used by the JAXB
 *            application (e.g., {@code java.util.Calendar}).
 */
public interface Converter<I, O>
{
    /**
     * Converts a value from the outer (application) type to the
     * inner (persistence) type.
     * <p>
     * This is called when writing data into the underlying list.
     * </p>
     *
     * @param outer the value to convert from the application model.
     *
     * @return the converted value in the inner representation.
     */
	public I direct(O outer);

	/**
     * Converts a value from the inner (persistence) type to the
     * outer (application) type.
     * <p>
     * This is called when reading data from the underlying list.
     * </p>
     *
     * @param inner the value to convert from the core list.
     *
     * @return the converted value in the outer representation.
     */
	public O inverse(I inner);
}
