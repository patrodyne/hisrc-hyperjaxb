package org.jvnet.hyperjaxb.xsom.tests;

import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;

public class TypeUseTest
{
	@Test
	public void testEquals() throws Exception
	{
		final CAdapter adapter = new CAdapter(NormalizedStringAdapter.class, false);
		final CAdapter adapter1 = new CAdapter(NormalizedStringAdapter.class, false);
		
		assertNotNull(adapter, "CAdapter instance should be constructed.");
		assertNotNull(adapter1, "CAdapter instance 1 should be constructed.");
		
		assertEquals(adapter.isWhitespaceAdapter(), adapter1.isWhitespaceAdapter(), "'adapter' and 'adapter1' should be Whitespace equivalent.");
		assertEquals(adapter.getAdapterIfKnown(), adapter1.getAdapterIfKnown(), "'adapter' and 'adapter1' should be AdapterIfKnown equivalent.");
		
		final TypeUse left = CBuiltinLeafInfo.NORMALIZED_STRING;
		final TypeUse right = TypeUseFactory.adapt(CBuiltinLeafInfo.STRING, adapter);
		
		assertNotNull(left, "TypeUse 'left' should exist.");
		assertNotNull(right, "TypeUse 'right' should exist.");
		
		assertEquals(left.idUse(), right.idUse(), "TypeUse 'left' and 'right' should be Collection equivalent.");
		assertEquals(left.idUse(), right.idUse(), "TypeUse 'left' and 'right' should be idUse equivalent.");
	}
}
