package org.jvnet.hyperjaxb.orm;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.AttributeConverter;

public class MonthDayAttributeConverter
	implements AttributeConverter<MonthDay, Date>
{
	@Override
	public Date convertToDatabaseColumn(MonthDay md)
	{
		Date sqlDate = null;
		if ( md != null )
		{
			// Year 1 generates: sun.util.calendar.JulianCalendar.Date
			// Year 1970 generates: sun.util.calendar.Gregorian.Date
			LocalDate ld = md.atYear(1970);
			sqlDate = Date.valueOf(ld);
		}
		return sqlDate;
	}

	@Override
	public MonthDay convertToEntityAttribute(Date sqlDate)
	{
		MonthDay md = null;
    	if ( sqlDate != null )
    	{
    		// In many database systems the TIMESTAMP (WITH TIME ZONE)
    		// data type(s) are internally stored as Coordinated Universal
    		// Time (UTC) values. The original time zone information is used
    		// during input to calculate the UTC equivalent but is generally
    		// not retained as a separate piece of data with the stored
    		// timestamp value itself.
        	Instant sdi = Instant.ofEpochMilli(sqlDate.getTime());
        	OffsetDateTime sdo = sdi.atOffset(ZoneOffset.UTC);
        	LocalDate ld = sdo.toLocalDate();
        	md = MonthDay.from(ld);
    	}
		return md;
	}
}
