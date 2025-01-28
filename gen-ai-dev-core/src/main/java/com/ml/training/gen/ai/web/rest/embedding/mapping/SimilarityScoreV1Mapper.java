package com.ml.training.gen.ai.web.rest.embedding.mapping;

import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.DocumentsScoreV1Response;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.ScoredTextV1;
import java.util.Comparator;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SimilarityScoreV1Mapper {

  private final ScoredTextV1Mapper scoredTextMapper;

  public SimilarityScoreV1Mapper(final ScoredTextV1Mapper scoredTextMapper) {
    this.scoredTextMapper = scoredTextMapper;
  }

  public DocumentsScoreV1Response toResponse(@NonNull final String query,
      @NonNull final List<ScoredText> documents) {
    return DocumentsScoreV1Response.builder()
        .withQuery(query)
        .withDocuments(documents.stream()
            .map(scoredTextMapper::toResponse)
            .sorted(Comparator.comparing(ScoredTextV1::getScore).reversed())

            .toList()
        )
        .build();
  }

}
