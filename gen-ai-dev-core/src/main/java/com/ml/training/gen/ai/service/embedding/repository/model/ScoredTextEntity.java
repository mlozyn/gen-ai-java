package com.ml.training.gen.ai.service.embedding.repository.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class ScoredTextEntity {

  private UUID id;
  private String text;

  private Float score;

}
