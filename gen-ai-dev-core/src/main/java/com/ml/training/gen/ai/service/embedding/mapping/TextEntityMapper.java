package com.ml.training.gen.ai.service.embedding.mapping;

import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import com.ml.training.gen.ai.service.embedding.repository.model.ScoredTextEntity;
import com.ml.training.gen.ai.service.embedding.repository.model.TextEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TextEntityMapper {

  public TextEntity toEntity(@NonNull final TextVector source) {
    return TextEntity.builder()
        .withText(source.getText())
        .withVector(source.getVector())
        .build();
  }

  public TextVector fromEntity(@NonNull final TextEntity source) {
    return TextVector.builder()
        .withId(source.getId())
        .withText(source.getText())
        .withVector(source.getVector())
        .build();
  }

  public ScoredText fromEntity(@NonNull final ScoredTextEntity source) {
    return ScoredText.builder()
        .withId(source.getId())
        .withText(source.getText())
        .withScore(source.getScore().doubleValue())
        .build();
  }


}
