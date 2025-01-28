package com.ml.training.gen.ai.service.embedding;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import org.springframework.lang.NonNull;

public interface EmbeddingFunction {

  boolean supports(@NonNull final ClientType clientType);

  ClientType getClientType();

  TextVector embed(@NonNull final String text);

}
