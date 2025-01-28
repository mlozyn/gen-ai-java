package com.ml.training.gen.ai.web.rest.embedding.mapping;

import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.EmbeddingSearchV1Response;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SimilaritySearchV1Mapper {

  private final ScoredTextV1Mapper scoredTextMapper;

  public SimilaritySearchV1Mapper(final ScoredTextV1Mapper scoredTextMapper) {
    this.scoredTextMapper = scoredTextMapper;
  }

  public EmbeddingSearchV1Response toResponse(@NonNull final List<ScoredText> source) {
    return EmbeddingSearchV1Response.builder()
        .withData(
            source.stream()
                .map(scoredTextMapper::toResponse)
                .toList()
        )
        .build();
  }

}
