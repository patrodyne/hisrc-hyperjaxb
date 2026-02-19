package org.jvnet.hyperjaxb.orm;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convert a database date to a modern {@link java.time.LocalDate}.
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date>
{
    @Override
    public Date convertToDatabaseColumn(LocalDate locDate)
    {
        return (locDate == null ? null : Date.valueOf(locDate));
    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate)
    {
    	LocalDate sdl = null;
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
        	sdl = sdo.toLocalDate();
    	}
        return sdl;
    }
}
