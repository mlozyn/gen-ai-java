package com.ml.training.gen.ai.service.embedding;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.mapping.TextEntityMapper;
import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import com.ml.training.gen.ai.service.embedding.repository.EmbeddingRepository;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

public class EmbeddingServiceImpl implements EmbeddingService {

  private static final Logger LOG = LoggerFactory.getLogger(EmbeddingServiceImpl.class);

  private final EmbeddingRepository repository;

  private final TextEntityMapper mapper;
  private final EmbeddingFunction embeddingFunction;

  public EmbeddingServiceImpl(@NonNull final EmbeddingRepository repository,
      @NonNull final TextEntityMapper mapper, @NonNull final EmbeddingFunction embeddingFunction) {
    this.repository = repository;
    this.mapper = mapper;

    this.embeddingFunction = embeddingFunction;
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return embeddingFunction.supports(clientType);
  }

  @Override
  public TextVector embed(@NonNull final String text) {
    return embeddingFunction.embed(text);
  }

  @Override
  public List<ScoredText> score(@NonNull final String query,
      @NonNull final List<String> documents) {
    if (CollectionUtils.isEmpty(documents)) {
      return Collections.emptyList();
    }

    LOG.info("[{}] Documents scoring STARTED", embeddingFunction.getClientType().getTitle());
    final StopWatch stopWatch = StopWatch.createStarted();

    // get query embedding
    final TextVector queryVector = embed(query);

    // get documents embedding
    final List<TextVector> vectors = documents.stream()
        .map(this::embed)
        .toList();

    // score documents based on query embedding
    final var result = vectors.stream()
        .map(vector -> ScoredText.builder()
            .withText(vector.getText())
            .withScore(getCosineSimilarity(queryVector.getVector(), vector.getVector()))
            .build()
        )
        .toList();

    stopWatch.stop();
    LOG.info("[{}] Documents scoring COMPLETED. Took: {} ms",
        embeddingFunction.getClientType().getTitle(),
        stopWatch.getTime(TimeUnit.MILLISECONDS));

    return result;
  }

  @Override
  public List<ScoredText> search(@NonNull final String query, @NonNull final Integer limit,
      @Nullable final Float scoreThreshold) {
    if (StringUtils.isBlank(query)) {
      return List.of();
    }

    LOG.info("[{}] Get Documents by query STARTED. Query: '{}'",
        embeddingFunction.getClientType().getTitle(), query);
    final StopWatch stopWatch = StopWatch.createStarted();

    final var queryVector = embed(query);

    final var result = repository.search(queryVector.getVector(), limit, scoreThreshold).stream()
        .map(mapper::fromEntity)
        .toList();

    stopWatch.stop();
    LOG.info("[{}] Get Documents by query COMPLETED. Took: {} ms",
        embeddingFunction.getClientType().getTitle(),
        stopWatch.getTime(TimeUnit.MILLISECONDS));

    return result;
  }

  @Override
  public TextVector save(@NonNull final String text) {
    return mapper.fromEntity(
        repository.save(mapper.toEntity(embed(text)))
    );
  }

  private double getCosineSimilarity(final List<Float> vec1, final List<Float> vec2) {
    double dotProduct = 0.0;
    double norm1 = 0.0;
    double norm2 = 0.0;

    for (int index = 0; index < vec1.size(); index++) {
      dotProduct += vec1.get(index) * vec2.get(index);
      norm1 += Math.pow(vec1.get(index), 2);
      norm2 += Math.pow(vec2.get(index), 2);
    }

    return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
  }

}
