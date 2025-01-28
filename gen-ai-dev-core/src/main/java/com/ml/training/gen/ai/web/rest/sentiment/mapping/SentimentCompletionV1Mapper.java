package com.ml.training.gen.ai.web.rest.sentiment.mapping;

import com.ml.training.gen.ai.service.sentiment.model.Sentiment;
import com.ml.training.gen.ai.web.rest.sentiment.model.v1.SentimentCompletionV1;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SentimentCompletionV1Mapper {

  public SentimentCompletionV1 toResponse(@NonNull final Sentiment source) {
    final var result = new SentimentCompletionV1();
    result.setOutput(source.getTitle());

    return result;
  }

}
