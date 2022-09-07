package org.jvnet.hyperjaxb3.xsom.tests;

import jakarta.xml.bind.annotation.adapters.NormalizedStringAdapter;

import org.junit.jupiter.api.Test;

import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;

public class TypeUseTest {

	@Test
	public void testEquals() throws Exception {

		final CAdapter adapter = new CAdapter(NormalizedStringAdapter.class,
				false);

		final CAdapter adapter1 = new CAdapter(NormalizedStringAdapter.class,
				false);

		final TypeUse left = CBuiltinLeafInfo.NORMALIZED_STRING;

		final TypeUse right = TypeUseFactory.adapt(CBuiltinLeafInfo.STRING,
				adapter);

//		assertTrue(adapter.equals(adapter1));
//		assertTrue(left.equals(right));
//		assertTrue(left.hashCode() == right.hashCode());

	}

}
