package com.ml.training.gen.ai.service.embedding;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface EmbeddingService {

  boolean supports(@NonNull final ClientType clientType);

  TextVector embed(@NonNull final String text);

  List<ScoredText> score(@NonNull final String query, @NonNull final List<String> documents);

  List<ScoredText> search(@NonNull final String query, @NonNull final Integer limit,
      @Nullable final Float scoreThreshold);

  TextVector save(final String text);

  List<TextVector> save(final List<String> source);

}
