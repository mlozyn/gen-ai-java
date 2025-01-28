package com.ml.training.gen.ai.service.rag.indexer;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import java.io.InputStream;
import org.springframework.lang.NonNull;

public class SemanticKernelDocumentIndexer implements DocumentIndexer {

  private final EmbeddingService embeddingService;

  public SemanticKernelDocumentIndexer(@NonNull final EmbeddingService embeddingService) {
    this.embeddingService = embeddingService;
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return embeddingService.supports(clientType);
  }

  @Override
  public void execute(@NonNull final InputStream source) {

  }

}
