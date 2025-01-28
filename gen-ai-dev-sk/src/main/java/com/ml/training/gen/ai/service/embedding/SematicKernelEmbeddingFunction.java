package com.ml.training.gen.ai.service.embedding;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.textembedding.TextEmbeddingGenerationService;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class SematicKernelEmbeddingFunction implements EmbeddingFunction {

  private final ClientType clientType;
  private final Kernel kernel;

  public SematicKernelEmbeddingFunction(@NonNull final ClientType clientType,
      @NonNull final Kernel kernel) {
    this.clientType = clientType;
    this.kernel = kernel;
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public ClientType getClientType() {
    return clientType;
  }

  @Override
  public TextVector embed(@NonNull final String text) {
    final var service = getEmbeddingGenerationService();

    final var response = service.generateEmbeddingAsync(text).block();
    Assert.notNull(response, "Embedding must not be null");

    return TextVector.builder()
        .withText(text)
        .withVector(response.getVector())
        .build();
  }

  private TextEmbeddingGenerationService getEmbeddingGenerationService() {
    try {
      return kernel.getService(TextEmbeddingGenerationService.class);
    } catch (final ServiceNotFoundException exception) {
      throw new RuntimeException("EmbeddingGenerationService not found", exception);
    }
  }

}
