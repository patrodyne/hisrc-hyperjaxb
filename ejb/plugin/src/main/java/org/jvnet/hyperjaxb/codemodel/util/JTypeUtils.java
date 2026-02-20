package org.jvnet.hyperjaxb.codemodel.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.ArrayUtils;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

/**
 * Utility methods to get basic and/or temporal {@link JType}.
 */
public class JTypeUtils
{
	// Seal this utility class
	private JTypeUtils() { }

	/**
	 * Determine when the given {@link JType} is a basic type.
	 *
	 * @param type The {@link JType} to evaluate.
	 *
	 * @return True when the given type is a basic type; otherwise, false.
	 */
	public static boolean isBasicType(final JType type)
	{
		final JType[] basicTypes = getBasicTypes(type.owner());
		return ArrayUtils.contains(basicTypes, type);
	}

	/**
	 * Determine when the given {@link JType} is a temporal type.
	 *
	 * @param type The {@link JType} to evaluate.
	 *
	 * @return True when the given type is a temporal type; otherwise, false.
	 */
	public static boolean isTemporalType(final JType type)
	{
		final JType[] temporalTypes = getTemporalTypes(type.owner());
		return ArrayUtils.contains(temporalTypes, type);
	}

	/**
	 * The static list of basic {@link JType}s.
	 *
	 * @param codeModel The {@link JCodeModel} to reference.
	 *
	 * @return A static list of basic {@link JType}s.
	 */
	public static JType[] getBasicTypes(final JCodeModel codeModel)
	{
		final JType[] basicTypes = new JType[]
		{
			codeModel.BOOLEAN,
			codeModel.BOOLEAN.boxify(), codeModel.BYTE,
			codeModel.BYTE.boxify(), codeModel.CHAR,
			codeModel.CHAR.boxify(), codeModel.DOUBLE,
			codeModel.DOUBLE.boxify(), codeModel.FLOAT,
			codeModel.FLOAT.boxify(), codeModel.INT,
			codeModel.INT.boxify(), codeModel.LONG,
			codeModel.LONG.boxify(), codeModel.SHORT,
			codeModel.SHORT.boxify(), codeModel.ref(String.class),
			codeModel.ref(BigInteger.class),
			codeModel.ref(BigDecimal.class),
			codeModel.ref(java.util.Date.class),
			codeModel.ref(java.util.Calendar.class),
			codeModel.ref(java.sql.Date.class),
			codeModel.ref(java.sql.Time.class),
			codeModel.ref(java.sql.Timestamp.class),
			codeModel.ref(java.time.LocalDate.class),
			codeModel.ref(java.time.LocalTime.class),
			codeModel.ref(java.time.LocalDateTime.class),
			codeModel.ref(java.time.OffsetTime.class),
			codeModel.ref(java.time.OffsetDateTime.class),
			codeModel.ref(java.time.Duration.class),
			codeModel.ref(java.time.Instant.class),
			codeModel.ref(java.time.DayOfWeek.class),
			codeModel.ref(java.time.Month.class),
			codeModel.ref(java.time.MonthDay.class),
			codeModel.ref(java.time.Year.class),
			codeModel.ref(java.time.YearMonth.class),
			codeModel.ref(java.time.ZonedDateTime.class),
			codeModel.ref(java.time.ZoneOffset.class),
			codeModel.BYTE.array(),	codeModel.BYTE.boxify().array(),
			codeModel.CHAR.array(),	codeModel.CHAR.boxify().array()
		};
		return basicTypes;
	}

	/**
	 * The static list of temporal {@link JType}s.
	 *
	 * The {@code jakarta.persistence.Temporal} annotation must
	 * be specified for persistent fields or properties ONLY of
	 * type {@code java.util.Date} and {@code java.util.Calendar}.
	 * It may only be specified for fields or properties of
	 * those types.
	 *
	 * Note: The newer {@code java.time.* } types do not use the
	 * {@code jakarta.persistence.Temporal} annotation.
	 *
	 * @param codeModel The {@link JCodeModel} to reference.
	 *
	 * @return A static list of temporal {@link JType}s.
	 */
	public static JType[] getTemporalTypes(final JCodeModel codeModel)
	{
		final JType[] temporalTypes = new JType[]
		{
			codeModel.ref(java.util.Date.class),
			codeModel.ref(java.util.Calendar.class),
			codeModel.ref(java.sql.Date.class),
			codeModel.ref(java.sql.Time.class),
			codeModel.ref(java.sql.Timestamp.class)
		};
		return temporalTypes;
	}
}
