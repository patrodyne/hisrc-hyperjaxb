package org.jvnet.hyperjaxb3.ejb.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

/**
 * Execute SQL work within a transaction. 
 * Always perform JDBC actions within a transaction!
 * 
 * The caller provides a lambda expression to define the work method.
 *
 * @param <R> The type returned by the work method.
 *
 * @author Rick O'Sullivan
 */
@FunctionalInterface
public interface TransactionalSql<R> // extends ReturningWork<R>
{
	/**
	 * Work to be transacted. Hint: Use a lambda to implement this method.
	 *
	 * Example:
	 *
	 * <pre>TransactionalSql&lt;R&gt; tx = (conn) -&gt; { ... };</pre>
	 *
	 * A lambda expression is a short block of code which takes in parameters
	 * and returns a value. Lambda expressions are similar to methods, but
	 * they do not need a name and they can be implemented right in the body of
	 * a method.
	 *
	 * @param conn The JDBC connection.
	 * 
	 * @throws SQLException  For information on a database access error or other errors. 
	 */
	public R work(Connection conn) throws SQLException;

	/**
	 * Execute the work method within a transaction.
	 *
	 * @param conn A connection (session) with a specific database.
 	 */
	default public R transact(Connection conn)
	{
		// Indicates when the transaction should finally be rolled back.
		boolean isRollbackOnly = false;

		// Is this the topmost transaction? The top transaction
		// begins in auto-commit mode which is then switched to
		// non-auto-commit so work can be committed or rolled back.
		boolean isTopTransaction = false;
		
		try
		{
			// Is this the topmost transaction? The top transaction
			// begins in auto-commit mode which is then switched to
			// non-auto-commit so work can be committed or rolled back.
			isTopTransaction = conn.getAutoCommit();

			// Conditionally begin the topmost transaction.
			if (isTopTransaction)
				conn.setAutoCommit(false);

			// Execute callback method for the business work.
			return work(conn);
		}
		catch (Exception ex)
		{
			try
			{
				String msg = "Transaction is not active.";
				if ( !conn.getAutoCommit() )
				{
					msg = "Transaction will be rolled back.";
					isRollbackOnly = true;
				}
				if ( ex instanceof RollbackException )
					throw ex;
				else
					throw new RollbackException(msg, ex);
			}
			catch ( SQLException sex)
			{
				throw new RollbackException("Transaction will not be rolled back!", sex);
			}
		}
		finally
		{
			try
			{
				// The topmost transaction is responsible for commit, rollback and cleanup.
				if (isTopTransaction && !conn.getAutoCommit())
				{
					if (isRollbackOnly)
						conn.rollback();
					else
						conn.commit();
				}
			}
			catch ( SQLException sex)
			{
				throw new PersistenceException("Transaction not completed!", sex);
			}
		}
	}
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
