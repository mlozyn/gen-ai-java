package com.ml.training.gen.ai.service.rag.prompt;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.rag.prompt.model.RAGCompletion;
import org.springframework.lang.NonNull;

public interface RAGPromptService {

  boolean supports(final ClientType clientType);

  RAGCompletion ask(@NonNull final String question);

}
