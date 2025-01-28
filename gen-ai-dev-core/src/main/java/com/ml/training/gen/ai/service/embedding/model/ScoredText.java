package com.ml.training.gen.ai.service.embedding.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(setterPrefix = "with")
@ToString
public class ScoredText {

  private UUID id;
  private String text;
  private Double score;

}
