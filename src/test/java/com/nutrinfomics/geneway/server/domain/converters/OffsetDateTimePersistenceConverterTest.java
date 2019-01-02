package com.nutrinfomics.geneway.server.domain.converters;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class OffsetDateTimePersistenceConverterTest {

  private final Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.of("America/New_York"));

  private OffsetDateTimePersistenceConverter offsetDateTimePersistenceConverter =
      new OffsetDateTimePersistenceConverter();

  @Test
  public void convertToDatabaseColumn_AsExpected() {
    OffsetDateTime offsetDateTime = OffsetDateTime.now(clock);

    java.sql.Timestamp timestamp =
        offsetDateTimePersistenceConverter.convertToDatabaseColumn(offsetDateTime);

    String offsetDateTimeString = offsetDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    String timestampString = dateFormat.format(timestamp);
    assertEquals(offsetDateTimeString, timestampString);
  }

  @Test
  public void convertToEntityAttribute_AsExpected() {
    Timestamp timestamp = Timestamp.from(clock.instant());

    OffsetDateTime offsetDateTime =
        offsetDateTimePersistenceConverter.convertToEntityAttribute(timestamp);

    String offsetDateTimeString = offsetDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    String timestampString = dateFormat.format(timestamp);

    assertEquals(timestampString, offsetDateTimeString);
  }

  @Test
  public void convertRoundTrip() {
    OffsetDateTime offsetDateTime = OffsetDateTime.now();
    java.sql.Timestamp timestamp =
        offsetDateTimePersistenceConverter.convertToDatabaseColumn(offsetDateTime);
    OffsetDateTime convertedOffsetDateTime =
        offsetDateTimePersistenceConverter.convertToEntityAttribute(timestamp);

    assertTrue(offsetDateTime.isEqual(convertedOffsetDateTime));
  }
}
