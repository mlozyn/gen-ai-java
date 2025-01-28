package com.ml.training.gen.ai.web.rest.rag.mapping.v1;

import com.ml.training.gen.ai.service.rag.prompt.model.RAGCompletion;
import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.web.rest.rag.model.v1.RAGCompletionV1;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class RAGCompletionV1Mapper {

  public RAGCompletionV1 map(@NonNull final RAGCompletion source) {
    return RAGCompletionV1.builder()
        .withAnswer(source.getAnswer())
        .withQuestions(source.getQueries())
        .withSources(source.getSources().stream()
            .map(ScoredText::getText)
            .toList()
        )
        .build();
  }

}
