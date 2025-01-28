package com.ml.training.gen.ai.web.rest.chat.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class PromptSettingsV1 {

  @JsonProperty("temperature")
  private Double temperature;
  @JsonProperty("topP")
  private Double topP;
  @JsonProperty("frequencyPenalty")
  private Double frequencyPenalty;
  @JsonProperty("presencePenalty")
  private Double presencePenalty;
  @JsonProperty("maxTokens")
  private Integer maxTokens;

}
