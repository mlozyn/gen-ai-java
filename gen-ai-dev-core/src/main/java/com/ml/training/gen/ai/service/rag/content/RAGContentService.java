package com.ml.training.gen.ai.service.rag.content;

import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import java.util.List;
import org.springframework.lang.NonNull;

public interface RAGContentService {

  List<ScoredText> getSources(@NonNull final List<String> queries);

}
