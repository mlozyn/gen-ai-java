package com.ml.training.gen.ai.service.rag.content;

import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

public class RAGFusionContentService implements RAGContentService {

  private static final Logger LOG = LoggerFactory.getLogger(RAGFusionContentService.class);

  private static final int MAX_RESULT_COUNT = 3;
  private static final float SCORE_THRESHOLD = 0.75f;

  private static final int K = 60;

  private final EmbeddingService embeddingService;

  public RAGFusionContentService(@NonNull final EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  @Override
  public List<ScoredText> getSources(@NonNull final List<String> queries) {
    if (CollectionUtils.isEmpty(queries)) {
      return Collections.emptyList();
    }

    LOG.info("[rag] Vector repository search by query STARTED. Queries count: {}", queries.size());

    final List<List<ScoredText>> contents = queries.stream()
        .map(this::getSources)
        .toList();

    final List<ScoredText> result = reciprocalRankFusion(contents);
    LOG.info("[rag] Vector repository search by query COMPLETED. Results size: {}", result.size());

    return result;
  }

  private List<ScoredText> reciprocalRankFusion(final List<List<ScoredText>> source) {
    final Map<UUID, ScoredText> values = new HashMap<>();
    final Map<UUID, Double> scores = new TreeMap<>(Collections.reverseOrder());

    for (final List<ScoredText> items : source) {
      for (int rank = 0; rank < items.size(); rank++) {
        final ScoredText text = items.get(rank);

        values.putIfAbsent(text.getId(), text);

        // If the document is not yet in the scores dictionary, add it with an initial score of 0
        scores.putIfAbsent(text.getId(), 0d);

        // Retrieve the current score of the document, if any
        final var score = scores.get(text.getId());
        scores.put(
            text.getId(),
            score + (1.0 / (rank + K))
        );
      }
    }

    return scores.keySet().stream()
        .map(values::get)
        .limit(MAX_RESULT_COUNT)
        .toList();
  }

  private List<ScoredText> getSources(final String query) {
    LOG.info("[rag] Getting sources for query: '{}'", query);

    return embeddingService.search(query, MAX_RESULT_COUNT, SCORE_THRESHOLD);
  }

}
