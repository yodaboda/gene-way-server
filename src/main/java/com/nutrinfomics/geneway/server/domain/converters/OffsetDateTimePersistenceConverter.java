package com.nutrinfomics.geneway.server.domain.converters;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Objects;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OffsetDateTimePersistenceConverter
    implements AttributeConverter<OffsetDateTime, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(OffsetDateTime offsetDateTime) {
    Objects.requireNonNull(offsetDateTime, "offsetDateTime");
    return Timestamp.valueOf(offsetDateTime.toLocalDateTime());
  }

  @Override
  public OffsetDateTime convertToEntityAttribute(Timestamp timestamp) {
    Objects.requireNonNull(timestamp, "timestamp");
    return OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
  }
}
