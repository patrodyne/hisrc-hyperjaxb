package org.example.jpa21.other;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BooleanConverter implements AttributeConverter<Boolean, String>
{
	@Override
	public String convertToDatabaseColumn(Boolean attribute)
	{
		return (attribute != null && attribute) ? "Y" : "N";
	}

	@Override
	public Boolean convertToEntityAttribute(String dbData)
	{
		return "Y".equals(dbData);
	}
}
