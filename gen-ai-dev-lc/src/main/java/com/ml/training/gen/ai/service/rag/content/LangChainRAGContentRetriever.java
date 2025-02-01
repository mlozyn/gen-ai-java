package com.ml.training.gen.ai.service.rag.content;

import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import java.util.List;
import org.springframework.lang.NonNull;

public class LangChainRAGContentRetriever implements ContentRetriever {

  private final EmbeddingService embeddingService;

  public LangChainRAGContentRetriever(@NonNull final EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  @Override
  public List<Content> retrieve(final Query query) {
    final List<ScoredText> result = embeddingService.search(query.text(), 5, 0.7f);

    return result.stream()
        .map(ScoredText::getText)
        .map(Content::new)
        .toList();
  }

}
