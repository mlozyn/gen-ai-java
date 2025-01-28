package com.ml.training.gen.ai.web.rest.embedding.mapping;

import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.ScoredTextV1;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ScoredTextV1Mapper {

  public ScoredTextV1 toResponse(@NonNull final ScoredText source) {
    final var result = new ScoredTextV1();
    result.setText(source.getText());
    result.setScore(source.getScore());

    return result;
  }

}
