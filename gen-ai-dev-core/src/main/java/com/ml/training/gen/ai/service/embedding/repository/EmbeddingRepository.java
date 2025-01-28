package com.ml.training.gen.ai.service.embedding.repository;

import com.ml.training.gen.ai.service.embedding.repository.model.ScoredTextEntity;
import com.ml.training.gen.ai.service.embedding.repository.model.TextEntity;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface EmbeddingRepository {

  List<ScoredTextEntity> search(@NonNull final List<Float> query, @NonNull final Integer limit,
      @Nullable final Float scoreThreshold);

  TextEntity save(@NonNull final TextEntity source);

  List<TextEntity> save(@NonNull final List<TextEntity> source);

}
