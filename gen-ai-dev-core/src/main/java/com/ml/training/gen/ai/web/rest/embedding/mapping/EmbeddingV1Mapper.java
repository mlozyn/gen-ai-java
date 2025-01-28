package com.ml.training.gen.ai.web.rest.embedding.mapping;

import com.ml.training.gen.ai.service.embedding.model.TextVector;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.EmbeddingV1Response;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingV1Mapper {

  public EmbeddingV1Response toResponse(@NonNull final TextVector vector) {
    return EmbeddingV1Response.builder()
        .withId(vector.getId())
        .withText(vector.getText())
        .withDimensions(Optional.ofNullable(vector.getVector()).map(List::size).orElse(null))
        .withVector(vector.getVector())
        .build();
  }

}
