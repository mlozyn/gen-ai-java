package com.ml.training.gen.ai.web.rest.common.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;

public class ZonedDateTimeJsonSerializer extends JsonSerializer<Instant> {

  private static final ZoneId UTC_TIME_ZONE = ZoneId.of("UTC");
  private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
      .appendLiteral('Z')
      .toFormatter()
      .withZone(UTC_TIME_ZONE);

  @Override
  public void serialize(final Instant instant, final JsonGenerator jsonGenerator,
      final SerializerProvider serializerProvider) throws IOException {
    if (Objects.isNull(instant)) {
      return;
    }

    final ZonedDateTime zonedDateTime = instant.atZone(UTC_TIME_ZONE);
    final String result = zonedDateTime.format(DATE_TIME_FORMATTER);

    jsonGenerator.writeString(result);
  }

}
