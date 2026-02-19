package org.jvnet.hyperjaxb.orm;

import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;

import jakarta.persistence.AttributeConverter;

public class YearMonthAttributeConverter
	implements AttributeConverter<YearMonth, Date>
{
	@Override
	public Date convertToDatabaseColumn(YearMonth ym)
	{
		return ( ym != null ) ? Date.valueOf(ym.atDay(1)) : null;
	}

	@Override
	public YearMonth convertToEntityAttribute(Date sqlDate)
	{
		YearMonth ym = null;
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
        	ym = YearMonth.from(sdo.toLocalDate());
    	}
		return ym;
	}
}
