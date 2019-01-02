package com.nutrinfomics.geneway.server.domain.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Test;

public class LocalDatePersistenceConverterTest {

	private LocalDatePersistenceConverter localDatePersistenceConverter = new LocalDatePersistenceConverter();
	private final Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.of("Asia/Shanghai"));

	@Test
	public void convertToDatabaseColumn_AsExpected() {
		LocalDate localDate = LocalDate.now(clock);

		java.sql.Date expectedDate = new java.sql.Date(java.util.Date.from(clock.instant()).getTime());
		java.sql.Date date = localDatePersistenceConverter.convertToDatabaseColumn(localDate);

		int millisInDay = 86400000;
		assertTrue(Math.abs(expectedDate.getTime() - date.getTime()) < millisInDay);
	}

	@Test
	public void convertToEntityAttribute_AsExpected() {
		LocalDate expectedLocalDate = LocalDate.now(clock);
		java.sql.Date date = new java.sql.Date(java.util.Date.from(clock.instant()).getTime());
		LocalDate localDate = localDatePersistenceConverter.convertToEntityAttribute(date);

		assertEquals(expectedLocalDate, localDate);
	}

}
