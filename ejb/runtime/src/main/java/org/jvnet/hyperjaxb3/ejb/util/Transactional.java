package org.jvnet.hyperjaxb3.ejb.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

/**
 * Execute JPQL work within a transaction. 
 * 
 * The caller provides a lambda expression to define the work method.
 *
 * @param <R> The type returned by the work method.
 *
 * @author Rick O'Sullivan
 */
@FunctionalInterface
public interface Transactional<R>
{
	/** Enumerate the cache option: clean or reuse. */
	public enum CacheOption { CLEAN, REUSE };

	/**
	 * Work to be transacted. Hint: Use a lambda to implement this method.
	 *
	 * Example:
	 *
	 * <pre>Transactional&lt;R&gt; tx = (em) -&gt; { ... };</pre>
	 *
	 * A lambda expression is a short block of code which takes in parameters
	 * and returns a value. Lambda expressions are similar to methods, but
	 * they do not need a name and they can be implemented right in the body of
	 * a method.
	 *
	 * @param em The entity manager.
	 */
	public R work(EntityManager em);

	/**
	 * Execute the work method within a transaction. Always clean the first
	 * level cache at the beginning of the topmost transaction (preferred).
	 *
	 * @param em The entity manager.
	 *
	 * @return The result of the work method.
	 */
	default public R transact(EntityManager em)
	{
		return transact(em, CacheOption.CLEAN);
	}

	/**
	 * Execute the work method within a transaction. Optionally, clean the
	 * first level cache at the beginning of the topmost transaction.
	 *
	 * Warning: Using <code>Cache.REUSE</code> may overwrite external data
	 * changes.
	 *
	 * @param em The entity manager.
	 * @param cache Flag to flush and clear the entity cache.
	 */
	default public R transact(EntityManager em, CacheOption cache)
	{
		// Is this the topmost (inactive) transaction?
		boolean isTopTransaction = !em.getTransaction().isActive();

		try
		{
			// Conditionally begin the topmost transaction.
			if (isTopTransaction)
			{
				em.getTransaction().begin();
				if ( cache == CacheOption.CLEAN )
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
			if ( em.getTransaction().isActive() )
			{
				em.getTransaction().setRollbackOnly();
				throw new RollbackException("Transaction marked for rollback only.", ex);
			}
			else
				throw new PersistenceException("Transaction is not active.", ex);
		}
		finally
		{
			// The topmost transaction is responsible for commit, rollback and cleanup.
			if ( isTopTransaction && em.getTransaction().isActive() )
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
