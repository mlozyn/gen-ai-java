package com.ml.training.gen.ai.service.embedding.repository;

import static io.qdrant.client.WithPayloadSelectorFactory.enable;

import com.ml.training.gen.ai.service.embedding.repository.error.VectorDbException;
import com.ml.training.gen.ai.service.embedding.repository.mapping.PointStructMapper;
import com.ml.training.gen.ai.service.embedding.repository.model.ScoredTextEntity;
import com.ml.training.gen.ai.service.embedding.repository.model.TextEntity;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QueryFactory;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.QueryPoints;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class EmbeddingQdrantRepository implements EmbeddingRepository {

  private static final Logger LOG = LoggerFactory.getLogger(EmbeddingQdrantRepository.class);

  private static final int BATCH_SIZE = 10;

  private final String collectionName;
  private final Integer dimensions;

  private final QdrantClient client;
  private final PointStructMapper mapper;

  public EmbeddingQdrantRepository(@NonNull final String collectionName,
      @NonNull final Integer dimensions, @NonNull final QdrantClient client,
      @NonNull final PointStructMapper mapper) {
    this.collectionName = collectionName;
    this.dimensions = dimensions;

    this.client = client;
    this.mapper = mapper;

    initCollection();
  }

  @Override
  public List<ScoredTextEntity> search(@NonNull final List<Float> query,
      @NonNull final Integer limit, @Nullable final Float scoreThreshold) {
    assertVectorSize(query);

    try {
      final var queryBuilder = QueryPoints.newBuilder()
          .setCollectionName(collectionName)
          .setQuery(QueryFactory.nearest(query))
          .setWithPayload(enable(true))
          .setLimit(Long.valueOf(limit));

      Optional.ofNullable(scoreThreshold).ifPresent(queryBuilder::setScoreThreshold);

      final var result = client.queryAsync(queryBuilder.build()).get();

      return result.stream()
          .map(mapper::map)
          .toList();
    } catch (final Exception ex) {
      LOG.info("[{}] Unable to search collection values. Error: {}", collectionName,
          ex.getMessage());
      throw new VectorDbException(
          String.format("Unable to search collection '%s' values", collectionName),
          ex
      );
    }
  }

  @Override
  public TextEntity save(@NonNull final TextEntity source) {
    assertVectorSize(source.getVector());

    // existence check
    final var data = search(source.getVector(), 1, 0.99f);
    if (!data.isEmpty()) {
      LOG.info("[{}] Entity already exists in the collection", collectionName);
      return TextEntity.builder()
          .withId(data.getFirst().getId())
          .withText(source.getText())
          .withVector(source.getVector())
          .build();
    }

    // no such records
    final var result = createEntity(source);

    upsert(List.of(result));

    return result;
  }

  public List<TextEntity> save(@NonNull final List<TextEntity> source) {
    if (CollectionUtils.isEmpty(source)) {
      return List.of();
    }

    final List<TextEntity> result = new LinkedList<>();

    final int batchCount = (source.size() + BATCH_SIZE - 1) / BATCH_SIZE;
    for (int index = 0; index < batchCount; index++) {
      final var batch = source.subList(
          index * BATCH_SIZE,
          Math.min((index + 1) * BATCH_SIZE, source.size())
      );

      final var entities = batch.stream()
          .map(this::createEntity)
          .toList();

      upsert(entities);

      result.addAll(entities);
    }

    return result;
  }

  private void upsert(final List<TextEntity> entities) {
    final var points = entities.stream()
        .map(mapper::map)
        .toList();

    try {
      final var response = client.upsertAsync(collectionName, points).get();

      LOG.info("[{}] Entities stored in the collection. Batch size: {}", collectionName,
          entities.size());
    } catch (final Exception ex) {
      LOG.info("[{}] Unable to save entities in the collection. Error: {}", collectionName,
          ex.getMessage());
      throw new VectorDbException(
          String.format("Unable to save entities in the collection '%s'", collectionName),
          ex
      );
    }
  }

  private TextEntity createEntity(final TextEntity source) {
    return TextEntity.builder()
        .withId(UUID.randomUUID())
        .withText(source.getText())
        .withVector(source.getVector())
        .build();
  }

  private void initCollection() {
    LOG.info("[{}] Collection initialization STARTED", collectionName);

    try {
      final boolean collectionExists = client.collectionExistsAsync(collectionName).get();
      if (collectionExists) {
        LOG.info("[{}] Collection initialization COMPLETED. Collection already exists",
            collectionName);
        return;
      }

      // create if it does not exist
      client.createCollectionAsync(collectionName,
              VectorParams.newBuilder()
                  .setDistance(Distance.Cosine)
                  .setSize(dimensions)
                  .build())
          .get();

      LOG.info("[{}] Collection initialization COMPLETED. Collection created", collectionName);
    } catch (final Exception ex) {
      LOG.info("[{}] Collection initialization FAILED. Error: {}", collectionName, ex.getMessage());
      throw new VectorDbException(
          String.format("Unable to init collection '%s'", collectionName),
          ex
      );
    }
  }

  private void assertVectorSize(final List<Float> vector) {
    Assert.isTrue(vector.size() == dimensions, "vector size is invalid");
  }

}
