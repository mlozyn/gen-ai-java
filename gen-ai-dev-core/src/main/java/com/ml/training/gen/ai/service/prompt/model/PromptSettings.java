package com.ml.training.gen.ai.service.prompt.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class PromptSettings {

  private Double temperature;
  private Double topP;
  private Double frequencyPenalty;
  private Double presencePenalty;
  private Integer maxTokens;

}
