package org.jvnet.hyperjaxb.orm;

import java.time.Year;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Use the {@link AttributeConverter} interface to define the conversion logic
 * between the Java Year type and the integer-based column type.
 *
 * <p>To apply globally, declare this converter in your {@code orm.xml}:</p>
 *
 * <pre>{@code
 * <entity-mappings>
 *     <converter class="org.jvnet.hyperjaxb.orm.YearAttributeConverter" auto-apply="true"/>
 * </entity-mappings>
 * }</pre>
 *
 * @author Vlad Mihalcea
 */
@Converter(autoApply = true)
public class YearAttributeConverter implements AttributeConverter<Year, Short>
{
	@Override
	public Short convertToDatabaseColumn(Year attribute)
	{
		return ( attribute == null ) ? null : (short) attribute.getValue();
	}

	@Override
	public Year convertToEntityAttribute(Short dbData)
	{
		return ( dbData == null ) ? null : Year.of(dbData);
	}
}
