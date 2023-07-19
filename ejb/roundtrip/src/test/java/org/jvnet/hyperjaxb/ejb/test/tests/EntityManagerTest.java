package org.jvnet.hyperjaxb.ejb.test.tests;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.ejb.test.AbstractEntityManagerTest;

public class EntityManagerTest extends AbstractEntityManagerTest
{
	@Test
	public void testIt() throws Exception
	{
		final A a1 = new A();
		final B b1 = new B();
		a1.setId("A");
		a1.setB(b1);
		a1.setD("d1");
		b1.setId("B");
		b1.setC("c1");
		
		getLogger().debug("a1: {}", a1);
		save(a1);
		getLogger().debug("a1: {}", a1);

		// Eager load A after B version incremented.
		final A av = load("A", true);
		getLogger().debug("av: {}", av);
		
		// Original Code generates Optimistic Lock Exception for
		// EclipseLink but Hibernate wildly succeeds!
//		final A a2 = new A();
//		final B b2 = new B();
//		a2.setId("A");
//		a2.setB(b2);
//		a2.setD("d2");
//		b2.setId("B");
//		b2.setC("c2");

		final A a2 = av;
		final B b2 = av.getB();
		a2.setB(b2);
		a2.setD("d2");
		b2.setC("c2");
		
		getLogger().debug("a2: {}", a2);
		save(a2);
		getLogger().debug("a2: {}", a2);
		
		final A a3 = load("A", true);
		getLogger().debug("a3: {}", a3);

		assertEquals(a3.getD(), a2.getD());
		assertEquals(a3.getB().getC(), a2.getB().getC());
	}

	public void save(A a)
	{
		try ( EntityManager em = createEntityManager() )
		{
			em.getTransaction().begin();
			em.merge(a);
			em.getTransaction().commit();
			em.clear();
		}
	}

	public A load(String id, boolean eager)
	{
		try ( EntityManager em = createEntityManager() )
		{
			final A a = em.find(A.class, id);
			if ( eager )
			{
				a.getB1().size();
				a.getB2().size();
				a.getE().size();
				a.getEItems().size();
				a.getENillable().size();
				a.getENillableItems().size();
				a.getF().size();
				a.getFItems().size();
				a.getFNillable().size();
				a.getFNillableItems().size();
				a.getG().size();
				a.getGItems().size();
			}
			return a;
		}
	}
}
