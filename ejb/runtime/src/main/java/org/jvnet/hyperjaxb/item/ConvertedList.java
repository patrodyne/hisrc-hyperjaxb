package org.jvnet.hyperjaxb.item;

import java.util.AbstractList;
import java.util.List;

/**
 * A decorating list that provides a converted view of an underlying
 * "inner" list.
 * <p>
 * This class is used when the object model requires a specific Java type
 * (the Outer type {@code O}) while the persistence layer requires a
 * different type (the Inner type {@code I}). It performs on-the-fly
 * conversion during access and modification.
 * </p>
 *
 * <p>This is typically used in conjunction with <b>JAXB Adapters</b> or custom
 * type converters. It ensures that when you modify the "Outer" list, the
 * "Inner" persistent list is automatically updated with the converted values.</p>
 *
 * <p><b>Note:</b> Unlike {@code ItemList}, this class doesn't necessarily
 * wrap data in an {@code Item<T>} entity. It simply converts between two types.</p>
 *
 * @param <O> generic Outer type: the type exposed by the JAXB object model.
 * @param <I> generic Inner type: the type stored in the inner persistent list.
 */
public class ConvertedList<O, I>
	extends AbstractList<O>
{
	/**
	 * The underlying list containing the persistent "Inner" values.
	 */
	private final List<I> inner;
	private final Converter<I, O> converter;

    /**
     * Constructs a new converted list backed by the provided inner list.
     *
	 * @param inner the list of inner values to be wrapped and converted.
	 * @param converter
	 */
	public ConvertedList(List<I> inner, Converter<I, O> converter)
	{
		super();
		this.inner = inner;
		this.converter = converter;
	}

	@Override
	public O get(int index)
	{
		return converter.inverse(inner.get(index));
	}

	@Override
	public O set(int index, O element)
	{
		return converter.inverse(inner.set(index, converter.direct(element)));
	}

	@Override
	public void add(int index, O element)
	{
		inner.add(index, converter.direct(element));
	}

	@Override
	public O remove(int index)
	{
		return converter.inverse(inner.remove(index));
	}

	@Override
	public int size()
	{
		return inner.size();
	}
}
