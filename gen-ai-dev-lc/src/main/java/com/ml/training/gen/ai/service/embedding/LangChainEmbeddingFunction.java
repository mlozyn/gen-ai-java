package com.ml.training.gen.ai.service.embedding;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class LangChainEmbeddingFunction implements EmbeddingFunction {

  private final ClientType clientType;
  private final EmbeddingModel embeddingModel;

  public LangChainEmbeddingFunction(@NonNull final ClientType clientType,
      @NonNull final EmbeddingModel embeddingModel) {
    this.clientType = clientType;
    this.embeddingModel = embeddingModel;
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
    final var response = embeddingModel.embed(text);
    Assert.notNull(response.content(), "Embedding must not be null");

    return TextVector.builder()
        .withText(text)
        .withVector(response.content().vectorAsList())
        .build();
  }

}
