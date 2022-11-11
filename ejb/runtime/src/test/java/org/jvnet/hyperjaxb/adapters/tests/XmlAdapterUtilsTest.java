package org.jvnet.hyperjaxb.adapters.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;

import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.DurationAsString;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.QNameAsString;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.TimeStringAsCalendar;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsDate;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsDateTime;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XMLGregorianCalendarAsTime;
import org.jvnet.hyperjaxb.xml.bind.annotation.adapters.XmlAdapterUtils;
import org.jvnet.hyperjaxb.xml.datatype.util.XMLGregorianCalendarUtils;

public class XmlAdapterUtilsTest {

	@Test
	public void testQNameXmlAdapter() throws Exception {
		
		final String alpha = "{urn:test}test";
		final QName omega = new QName("urn:test", "test");

		assertEquals(alpha, XmlAdapterUtils .unmarshall(QNameAsString.class, omega), "Conversion failed.");
		assertEquals(omega, XmlAdapterUtils .marshall(QNameAsString.class, alpha), "Conversion failed.");
	}

	@Test
	public void testDuration() throws Exception {

		final String alpha = "P1Y2M3DT10H30M12.3S";
		final Duration omega = DatatypeFactory.newInstance().newDuration(alpha);

		assertEquals(alpha, XmlAdapterUtils .unmarshall(DurationAsString.class, omega), "Conversion failed.");
		assertEquals(omega, XmlAdapterUtils .marshall(DurationAsString.class, alpha), "Conversion failed.");
	}

	@Test
	public void testXMLGregorianCalendarXmlAdapter() throws Exception {

		final XMLGregorianCalendar alpha = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar("2005-01-01T11:00:00.012+04:00");

		final XMLGregorianCalendar omega = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar("2005-01-01T09:00:00.012+02:00");

		final XMLGregorianCalendar beta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDateTime.class,
				XmlAdapterUtils.unmarshall(
						XMLGregorianCalendarAsDateTime.class, alpha));
		// assertEquals("Conversion failed.", alpha.normalize(),
		// omega.normalize());
		// assertEquals("Conversion failed.", alpha.normalize(),
		// beta.normalize());
		// assertEquals("Conversion failed.", beta.normalize(),
		// omega.normalize());
		assertEquals(XMLGregorianCalendarUtils
				.getTimeInMillis(alpha), XMLGregorianCalendarUtils
				.getTimeInMillis(beta), "Conversion failed.");
		assertEquals(XMLGregorianCalendarUtils
				.getTimeInMillis(alpha), XMLGregorianCalendarUtils
				.getTimeInMillis(omega), "Conversion failed.");
		assertEquals(XMLGregorianCalendarUtils
				.getTimeInMillis(beta), XMLGregorianCalendarUtils
				.getTimeInMillis(omega), "Conversion failed.");
	}

	@Test
	public void testXMLGregorianCalendarAsDate() throws Exception {

		final java.sql.Date alpha = java.sql.Date.valueOf("2005-01-01");

		System.out.println("1)" + alpha.getTime());

		final XMLGregorianCalendar beta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDate.class, alpha);

		System.out.println("2)" + beta.toGregorianCalendar().getTimeInMillis());
		System.out.println("2>" + beta);

		final java.util.Date gamma = XmlAdapterUtils.unmarshall(
				XMLGregorianCalendarAsDate.class, beta);
		System.out.println("3)" + gamma.getTime());
		final XMLGregorianCalendar delta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDate.class, gamma);
		System.out.println("4)"
				+ delta.toGregorianCalendar().getTime().getTime());
		System.out.println("4>" + delta);
		assertEquals(beta, delta, "Conversion failed.");
	}

	@Test
	public void testXMLGregorianCalendarAsDateInNegativeTimezone()
			throws Exception {

		TimeZone _default = TimeZone.getDefault();

		TimeZone.setDefault(TimeZone.getTimeZone("GMT-2"));

		final java.sql.Date alpha = java.sql.Date.valueOf("2005-01-01");

		System.out.println("1)" + alpha.getTime());

		final XMLGregorianCalendar beta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDate.class, alpha);

		System.out.println("2)" + beta.toGregorianCalendar().getTimeInMillis());

		final java.util.Date gamma = XmlAdapterUtils.unmarshall(
				XMLGregorianCalendarAsDate.class, beta);
		System.out.println("3)" + gamma.getTime());
		final XMLGregorianCalendar delta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDate.class, gamma);
		System.out.println("4)"
				+ delta.toGregorianCalendar().getTime().getTime());
		assertEquals(beta, delta, "Conversion failed.");

		TimeZone.setDefault(_default);
	}

	@Test
	public void testXMLGregorianCalendarAsTime() throws Exception {

		final java.sql.Time alpha = java.sql.Time.valueOf("10:12:14");

		final XMLGregorianCalendar beta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsTime.class, alpha);

		final java.util.Date gamma = XmlAdapterUtils.unmarshall(
				XMLGregorianCalendarAsTime.class, beta);
		final XMLGregorianCalendar delta = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsTime.class, gamma);
		assertEquals(beta, delta, "Conversion failed.");
	}

	@Test
	public void testTimeStringAsCalendarXmlAdapter() throws Exception {

		checkTimeStringAsCalendarXmlAdapter("10:20:30");
		checkTimeStringAsCalendarXmlAdapter("10:20:30Z");
		checkTimeStringAsCalendarXmlAdapter("12:13:14+01:00");
		checkTimeStringAsCalendarXmlAdapter("12:13:14+02:00");
		checkTimeStringAsCalendarXmlAdapter("12:13:14-03:00");
	}

	private void checkTimeStringAsCalendarXmlAdapter(final String alpha) {
		final Calendar beta = XmlAdapterUtils.unmarshall(
				TimeStringAsCalendar.class, alpha);
		final String gamma = XmlAdapterUtils.marshall(
				TimeStringAsCalendar.class, beta);
		final Calendar delta = XmlAdapterUtils.unmarshall(
				TimeStringAsCalendar.class, gamma);
		final String epsilon = XmlAdapterUtils.marshall(
				TimeStringAsCalendar.class, delta);
		// assertEquals("Conversion failed.", alpha, gamma);
		assertEquals(beta, delta, "Conversion failed.");
		assertEquals(gamma, epsilon, "Conversion failed.");
	}

	@Test
	public void testXMLGregorianCalendarAsDateTimeXmlAdapter() throws Exception {

		checkXMLGregorianCalendarAsDateTimeXmlAdapter("2005-01-01T00:00:00.000+00:00");
		checkXMLGregorianCalendarAsDateTimeXmlAdapter("2005-01-01T09:00:00.012+02:00");
		checkXMLGregorianCalendarAsDateTimeXmlAdapter("2008-01-02T10:18:30+01:00");
		checkXMLGregorianCalendarAsDateTimeXmlAdapter("2008-01-02T10:19:30Z");
		checkXMLGregorianCalendarAsDateTimeXmlAdapter("2008-01-02T10:20:30");
	}

	private void checkXMLGregorianCalendarAsDateTimeXmlAdapter(final String text)
			throws DatatypeConfigurationException {

		final XMLGregorianCalendar alpha = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(text);
		System.out.println("T]" + alpha.getTimezone());
		long a = alpha.toGregorianCalendar().getTimeInMillis();
		System.out.println("1]" + a);
		final Date beta = XmlAdapterUtils.unmarshall(
				XMLGregorianCalendarAsDateTime.class, alpha);
		long b = beta.getTime();
		System.out.println("2]" + b);
		final XMLGregorianCalendar gamma = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDateTime.class, beta);
		long c = gamma.toGregorianCalendar().getTimeInMillis();
		System.out.println("3]" + c);
		final Date delta = XmlAdapterUtils.unmarshall(
				XMLGregorianCalendarAsDateTime.class, gamma);
		long d = delta.getTime();
		System.out.println("4]" + d);
		final XMLGregorianCalendar epsilon = XmlAdapterUtils.marshall(
				XMLGregorianCalendarAsDateTime.class, delta);
		long e = epsilon.toGregorianCalendar().getTimeInMillis();
		System.out.println("5]" + e);
		// assertEquals("Conversion failed.", alpha, gamma);
		assertEquals(beta, delta, "Conversion failed.");
		assertEquals(gamma, epsilon, "Conversion failed.");
		assertEquals(a, b, "Conversion failed.");
		assertEquals(b, c, "Conversion failed.");
		assertEquals(c, d, "Conversion failed.");
		assertEquals(d, e, "Conversion failed.");
	}

//	public void testXMLGregorianCalendarAsDateXmlAdapter() throws Exception {
//
//		checkXMLGregorianCalendarAsDateXmlAdapter("2008-01-02");
//		checkXMLGregorianCalendarAsDateXmlAdapter("2008-01-02Z");
//		checkXMLGregorianCalendarAsDateXmlAdapter("2005-01-01+00:00");
//		checkXMLGregorianCalendarAsDateXmlAdapter("2005-01-01+02:00");
//		checkXMLGregorianCalendarAsDateXmlAdapter("2008-01-02+01:00");
//	}
//
//	private void checkXMLGregorianCalendarAsDateXmlAdapter(final String text)
//			throws DatatypeConfigurationException {
//
//		final XMLGregorianCalendar alpha = DatatypeFactory.newInstance()
//				.newXMLGregorianCalendar(text);
//		System.out.println("T>" + alpha.getTimezone());
//
//		long a = alpha.toGregorianCalendar().getTimeInMillis();
//		System.out.println("1>" + a);
//		final Date beta = XmlAdapterUtils.unmarshall(
//				XMLGregorianCalendarAsDate.class, alpha);
//		long b = beta.getTime();
//		System.out.println("2>" + b);
//		final XMLGregorianCalendar gamma = XmlAdapterUtils.marshall(
//				XMLGregorianCalendarAsDate.class, beta);
//		long c = gamma.toGregorianCalendar().getTimeInMillis();
//		System.out.println("3>" + c);
//		final Date delta = XmlAdapterUtils.unmarshall(
//				XMLGregorianCalendarAsDate.class, gamma);
//		long d = delta.getTime();
//		System.out.println("4>" + d);
//		final XMLGregorianCalendar epsilon = XmlAdapterUtils.marshall(
//				XMLGregorianCalendarAsDate.class, delta);
//		long e = epsilon.toGregorianCalendar().getTimeInMillis();
//		System.out.println("5>" + e);
//		// assertEquals("Conversion failed.", alpha, gamma);
//		// assertEquals("Conversion failed.", beta, delta);
//		// assertEquals("Conversion failed.", gamma, epsilon);
//		assertEquals("Conversion failed.", a, b);
//		assertEquals("Conversion failed.", b, c);
//		assertEquals("Conversion failed.", c, d);
//		assertEquals("Conversion failed.", d, e);
//	}
}
