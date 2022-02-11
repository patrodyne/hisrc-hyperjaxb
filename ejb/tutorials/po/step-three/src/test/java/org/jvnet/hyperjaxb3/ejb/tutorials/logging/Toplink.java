package org.jvnet.hyperjaxb3.ejb.tutorials.logging;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import oracle.toplink.essentials.logging.SessionLog;
import oracle.toplink.essentials.logging.SessionLogEntry;
import oracle.toplink.essentials.sessions.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Implement Toplink's SessionLog using SLF4J.
 * 
 * @author Rick O'Sullivan
 */
public class Toplink extends oracle.toplink.essentials.logging.AbstractSessionLog {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Session session;
	@Override public Session getSession() { return session; }
	@Override public void setSession(Session session) { this.session = session; }

	private Writer writer = new PrintWriter(System.out);;
	@Override public Writer getWriter() { return writer; }
	@Override public void setWriter(Writer log) { this.writer = log; }

	private int level = SessionLog.INFO;
	@Override public int getLevel() { return level; }
	@Override public void setLevel(int level) { this.level = level; }

	private Map<String, Integer> levelMap = new HashMap<>();
	@Override public int getLevel(String category)
	{
		if ( !levelMap.containsKey(category) )
			levelMap.put(category, getLevel());
		return levelMap.get(category);
	}
	@Override public void setLevel(int level, String category) { levelMap.put(category, level); }

	@Override public boolean shouldLog(int level) { return (getLevel() >= level); }
	@Override public boolean shouldLog(int level, String category) { return (getLevel(category) >= level); }
	
	private boolean shouldLogExceptionStackTrace;
	@Override public boolean shouldLogExceptionStackTrace() { return shouldLogExceptionStackTrace; }
	@Override public void setShouldLogExceptionStackTrace(boolean flag) { this.shouldLogExceptionStackTrace = flag; }

	private boolean shouldPrintDate;
	@Override public boolean shouldPrintDate() { return shouldPrintDate; }
	@Override public void setShouldPrintDate(boolean flag) { this.shouldPrintDate = flag; }

	private boolean shouldPrintThread;
	@Override public boolean shouldPrintThread() { return shouldPrintThread; }
	@Override public void setShouldPrintThread(boolean flag) { this.shouldPrintThread = flag; }

	private boolean shouldPrintConnection;
	@Override public boolean shouldPrintConnection() { return shouldPrintConnection; }
	@Override public void setShouldPrintConnection(boolean flag) { this.shouldPrintConnection = flag; }

	private boolean shouldPrintSession;
	@Override public boolean shouldPrintSession() { return shouldPrintSession; }
	@Override public void setShouldPrintSession(boolean flag) { this.shouldPrintSession = flag; }

	private Level level(int sessionLevel)
	{
		Level level = Level.INFO;
		switch (sessionLevel)
		{
			case SessionLog.SEVERE: level = Level.ERROR; break;
			case SessionLog.WARNING: level = Level.WARN; break;
			case SessionLog.INFO: level = Level.INFO; break;
			case SessionLog.FINE: level = Level.DEBUG; break;
			case SessionLog.FINER: level = Level.TRACE; break;
			case SessionLog.FINEST: level = Level.TRACE; break;
			case SessionLog.ALL: level = Level.TRACE; break;
			case SessionLog.OFF: level = Level.ERROR; break;
			case SessionLog.CONFIG: level = Level.INFO; break;
			default: level = Level.INFO; break;
		}
		return level;
	}
	
	@Override
	public void log(SessionLogEntry entry) {
		// Marker marker = new BasicMarker(entry.getNameSpace());
		log(entry.getLevel(), entry.getMessage(), entry.getParameters());
	}

	@Override
	public void log(int level, String message) {
		switch (level(level))
		{
			case ERROR: logger.error(message);
			case WARN: logger.warn(message);
			case INFO: logger.info(message);
			case DEBUG: logger.debug(message);
			case TRACE: logger.trace(message);
		}
	}

	@Override
	public void log(int level, String message, Object param) {
		log(level, message, new Object[] { param } );
	}

	@Override
	public void log(int level, String message, Object param1, Object param2) {
		log(level, message, new Object[] { param1, param2 } );
	}

	@Override
	public void log(int level, String message, Object param1, Object param2, Object param3) {
		log(level, message, new Object[] { param1, param2, param3 } );
	}

	@Override
	public void log(int level, String message, Object[] arguments) {
		switch (level(level))
		{
			case ERROR: logger.error(message, arguments);
			case WARN: logger.warn(message, arguments);
			case INFO: logger.info(message, arguments);
			case DEBUG: logger.debug(message, arguments);
			case TRACE: logger.trace(message, arguments);
		}
	}

	@Override
	public void log(int level, String message, Object[] arguments, boolean shouldTranslate) {
		log(level, message, arguments);
	}

	@Override
	public void throwing(Throwable throwable) {
		logThrowable(SessionLog.SEVERE, throwable);
	}

	@Override
	public void severe(String message) {
		log(SessionLog.SEVERE, message);
	}

	@Override
	public void warning(String message) {
		log(SessionLog.WARNING, message);
	}

	@Override
	public void info(String message) {
		log(SessionLog.INFO, message);
	}

	@Override
	public void config(String message) {
		log(SessionLog.INFO, message);
	}

	@Override
	public void fine(String message) {
		log(SessionLog.FINE, message);
	}

	@Override
	public void finer(String message) {
		log(SessionLog.FINER, message);
	}

	@Override
	public void finest(String message) {
		log(SessionLog.FINEST, message);
	}

	@Override
	public void logThrowable(int level, Throwable throwable) {
		log(level, throwable.getMessage(), throwable);
	}
}
