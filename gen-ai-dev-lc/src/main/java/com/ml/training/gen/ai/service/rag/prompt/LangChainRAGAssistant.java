package com.ml.training.gen.ai.service.rag.prompt;

import org.springframework.lang.NonNull;

public interface LangChainRAGAssistant {

  String chat(@NonNull final String userMessage);

}
