package org.jvnet.hyperjaxb3.util.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.jvnet.hyperjaxb3.item.DefaultItemList;

public class ItemListTest {

	@Test
	public void testItemList() throws Exception {

		final List<StringItem> items = new ArrayList<StringItem>();

		final List<String> strings = new DefaultItemList<String, StringItem>(
				items, StringItem.class);

		strings.add("a");
		strings.add("b");

		assertEquals(2, items.size(), "Wrong number of items.");
		final Iterator<StringItem> iterator = items.iterator();
		assertEquals("a", iterator.next().getItem(), "Wrong value.");
		assertEquals("b", iterator.next().getItem(), "Wrong value.");

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				bos);

		objectOutputStream.writeObject(strings);

		final ObjectInputStream objectInputStream = new ObjectInputStream(
				new ByteArrayInputStream(bos.toByteArray()));

		objectInputStream.close();

		@SuppressWarnings("unchecked")
		final List<String> sgnirts = (List<String>) objectInputStream
				.readObject();

		assertEquals(strings, sgnirts);

	}

}
