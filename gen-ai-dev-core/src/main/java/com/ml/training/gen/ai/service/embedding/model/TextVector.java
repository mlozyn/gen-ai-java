package com.ml.training.gen.ai.service.embedding.model;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(setterPrefix = "with")
@ToString
public class TextVector {

  private UUID id;

  private String text;
  private List<Float> vector;

}
