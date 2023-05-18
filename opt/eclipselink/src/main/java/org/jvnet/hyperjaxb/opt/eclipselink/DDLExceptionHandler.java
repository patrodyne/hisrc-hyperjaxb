package org.jvnet.hyperjaxb.opt.eclipselink;

import java.util.regex.Pattern;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database Definition Language (DDL) exception handler catches errors
 * that occur during database definition. The exception handler has the
 * option of re-throwing the exception, throwing a different exception or
 * ignoring the database operation.
 */
public class DDLExceptionHandler implements ExceptionHandler
{
	private static Logger logger = LoggerFactory.getLogger(DDLExceptionHandler.class);
	public static Logger getLogger() { return logger; }
	
	private static Pattern DROP_PATTERN = Pattern.compile(".*\\bDROP +[A-Z]+\\b.*", Pattern.CASE_INSENSITIVE);
	
	/**
     * Re-throw the exception, throw a different exception or
     * ignore the database operation.
	 * 
	 * @param ex The exception to be handled.
	 * 
	 * @return An exception or null to handle the exception here.
	 */
	@Override
	public Object handleException(RuntimeException ex)
	{
		if ( ex instanceof DatabaseException )
		{
			DatabaseException dbex = (DatabaseException) ex;
			String call = dbex.getCall().getLogString(dbex.getAccessor());
		    if ( DROP_PATTERN.matcher(call).matches() )
		    {
		    	getLogger().debug("{} HANDLED: \n[{}\n]", ex.getClass().getSimpleName(), ex.getMessage());
		    	return 0;
		    }
		    else
		    	throw ex;
		}
		else
			throw ex;
	}
}
