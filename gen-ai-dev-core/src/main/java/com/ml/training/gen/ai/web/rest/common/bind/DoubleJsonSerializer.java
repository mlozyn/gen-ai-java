package com.ml.training.gen.ai.web.rest.common.bind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Objects;

public class DoubleJsonSerializer extends JsonSerializer<Double> {

  @Override
  public void serialize(final Double value, final JsonGenerator jsonGenerator,
      final SerializerProvider serializerProvider) throws IOException {
    if (Objects.isNull(value)) {
      return;
    }

    final String result = String.format("%.2f", value);
    jsonGenerator.writeString(result);
  }
}
