package com.ml.training.gen.ai.service.embedding.repository.model;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(setterPrefix = "with")
@EqualsAndHashCode
public class TextEntity {

  private UUID id;
  private String text;

  private List<Float> vector;

}
