package com.ml.training.gen.ai.service.rag.prompt;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.rag.prompt.model.RAGCompletion;
import org.springframework.lang.NonNull;

public class LangChainRAGPromptService implements RAGPromptService {

  private final ClientType clientType;
  private final LangChainRAGAssistant assistant;

  public LangChainRAGPromptService(@NonNull final ClientType clientType,
      @NonNull final LangChainRAGAssistant assistant) {
    this.clientType = clientType;
    this.assistant = assistant;

  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public RAGCompletion ask(@NonNull final String question) {
    final String result = assistant.chat(question);

    return RAGCompletion.builder()
        .withAnswer(result)
        .build();
  }

}
