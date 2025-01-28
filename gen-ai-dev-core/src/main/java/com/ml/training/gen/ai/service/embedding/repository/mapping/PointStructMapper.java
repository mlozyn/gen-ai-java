package com.ml.training.gen.ai.service.embedding.repository.mapping;

import static io.qdrant.client.VectorsFactory.vectors;

import com.ml.training.gen.ai.service.embedding.repository.model.ScoredTextEntity;
import com.ml.training.gen.ai.service.embedding.repository.model.TextEntity;
import io.qdrant.client.PointIdFactory;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.grpc.JsonWithInt.Value;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.ScoredPoint;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PointStructMapper {

  private static final String TEXT_FIELD_NAME = "text";

  public PointStruct map(@NonNull final TextEntity source) {
    return PointStruct.newBuilder()
        .setId(PointIdFactory.id(source.getId()))
        .setVectors(vectors(source.getVector()))
        .putAllPayload(Map.of(
            TEXT_FIELD_NAME, ValueFactory.value(source.getText())
        ))
        .build();
  }

  public ScoredTextEntity map(@NonNull final ScoredPoint source) {
    var builder = ScoredTextEntity.builder()
        .withId(UUID.fromString(source.getId().getUuid()))
        .withScore(source.getScore());

    final var payload = source.getPayloadMap();
    builder.withText(
        Optional.ofNullable(payload.get(TEXT_FIELD_NAME))
            .map(Value::getStringValue)
            .orElse(null)
    );

    return builder.build();
  }

}
