package org.jvnet.hyperjaxb3.ejb.util;

import javax.persistence.EntityManager;

/**
 * Execute work within a transaction. 
 * The caller provides a lambda expression to define the work method.
 *
 * @param <R> The type returned by the work method.
 *
 * @author Rick O'Sullivan
 */
@FunctionalInterface
public interface Transactional<R>
{
	public enum Cache { CLEAN, DIRTY };

	/**
	 * Work to be transacted. Lambdas implement this method.
	 *
	 * @param em The entity manager.
	 */
	public R work(EntityManager em);

	default public R transact(EntityManager em)
	{
		return transact(em, Cache.CLEAN);
	}

	/**
	 * Execute the work method within a transaction.
	 *
	 * @param em The entity manager.
	 * @param cache Flag to flush and clear the entity cache.
	 */
	default public R transact(EntityManager em, Cache cache)
	{
		// Is this the topmost (inactive) transaction?
		boolean isTopTransaction = !em.getTransaction().isActive();

		try
		{
			// Conditionally begin the topmost transaction.
			if (isTopTransaction)
			{
				em.getTransaction().begin();
				if ( cache == Cache.CLEAN )
				{
					em.flush();
					em.clear();
				}
			}

			// Execute callback method for the business work.
			return work(em);
		}
		catch (RuntimeException ex)
		{
			em.getTransaction().setRollbackOnly();
			throw ex;
		}
		finally
		{
			// The topmost transaction is responsible for commit, rollback and cleanup.
			if (isTopTransaction)
			{
				if (em.getTransaction().getRollbackOnly())
					em.getTransaction().rollback();
				else
					em.getTransaction().commit();
			}
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
