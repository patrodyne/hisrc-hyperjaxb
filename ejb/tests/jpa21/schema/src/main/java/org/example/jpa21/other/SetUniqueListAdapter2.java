package org.example.jpa21.other;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.list.SetUniqueList;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class SetUniqueListAdapter2<E>
	extends XmlAdapter<List<E>, List<E>>
{
	/**
	 * Convert XML List to SetUniqueList during Unmarshalling
	 */
	@Override
	public List<E> unmarshal(List<E> xmlList)
	{
		if ( xmlList == null )
			return null;
        // Decorate a new ArrayList with SetUniqueList to ensure uniqueness
		return SetUniqueList.setUniqueList(new ArrayList<>(xmlList));
	}

	/**
	 * Convert SetUniqueList back to standard List during Marshalling
	 */
	@Override
	public List<E> marshal(List<E> suList)
	{
		// SetUniqueList already implements List
		return suList;
	}
}

