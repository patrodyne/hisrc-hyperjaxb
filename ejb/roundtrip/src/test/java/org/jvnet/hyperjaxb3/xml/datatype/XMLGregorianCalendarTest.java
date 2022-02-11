package org.jvnet.hyperjaxb3.xml.datatype;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLGregorianCalendarTest extends TestCase {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private DatatypeFactory datatypeFactory;
	{
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public DatatypeFactory getDatatypeFactory() {
		return datatypeFactory;
	}

	public void testIt() {

		final XMLGregorianCalendar calendar = getDatatypeFactory()
				.newXMLGregorianCalendar();

		calendar.setDay(15);
		calendar.setTimezone(120);
		final String gDayString = calendar.toXMLFormat();
		logger.debug(gDayString);
	}

}
