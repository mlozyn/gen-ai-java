package com.ml.training.gen.ai.service.sentiment.model;

import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public enum Sentiment {

  POSITIVE("positive"),
  NEUTRAL("neutral"),
  NEGATIVE("negative");

  private final String title;

  Sentiment(@NonNull final String title) {
    this.title = title;
  }

}
