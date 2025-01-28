package com.ml.training.gen.ai.web.rest.common.bind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import org.apache.commons.lang3.StringUtils;

public class ZonedDateTimeJsonDeserializer extends JsonDeserializer<Instant> {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd'T'HH:mm:ss")

      .optionalStart()
      .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
      .optionalEnd()

      .optionalStart()
      .appendPattern("Z")
      .optionalEnd()

      .optionalStart()
      .appendPattern("XXX")
      .optionalEnd()

      .toFormatter();

  @Override
  public Instant deserialize(final JsonParser parser, final DeserializationContext context)
      throws IOException {
    final String date = parser.getText();
    if (StringUtils.isEmpty(date)) {
      return null;
    }

    return Instant.from(DATE_TIME_FORMATTER.parse(date));
  }

}
