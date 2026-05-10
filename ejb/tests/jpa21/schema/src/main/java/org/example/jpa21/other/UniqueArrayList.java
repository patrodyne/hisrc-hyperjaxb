package org.example.jpa21.other;

import java.util.ArrayList;

/**
 * An implementation of {@code List} that maintains
 * an unique list of elements.
 *
 * @param <E> The generic element type.
 */
public class UniqueArrayList<E> extends ArrayList<E>
{
	private static final long serialVersionUID = 20260401L;

	/**
	 * This method preserves uniqueness.
	 *
	 * <p>This method does not append an element when
	 * it is already contained in the list.</p>
	 */
	@Override
	public boolean add(E element)
	{
		// If element already exists, don't add
		if ( this.contains(element) )
			return false;
		return super.add(element);
	}

	/**
	 * This method preserves uniqueness.
	 *
	 * <p>This method does not insert an element when
	 * it is already contained in the list.</p>
	 */
	@Override
	public void add(int index, E element)
	{
		if ( !this.contains(element) )
			super.add(index, element);
	}
}
