package org.jvnet.hyperjaxb.ejb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.RollbackException;

/**
 * Execute JPQL work within a transaction. 
 * Always perform EntityManager actions within a transaction!
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
			String msg = "Transaction is not active.";
			if ( em.getTransaction().isActive() )
			{
				msg = "Transaction marked for rollback only.";
				em.getTransaction().setRollbackOnly();
			}
			if ( ex instanceof RollbackException )
				throw ex;
			else
				throw new RollbackException(msg, ex);
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
