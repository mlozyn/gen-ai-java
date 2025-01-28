package com.ml.training.gen.ai.web.rest.sentiment.model.v1;

import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SentimentPromptV1 {

  @NotEmpty
  @JsonProperty("input")
  private String input;

  @NotNull
  @JsonProperty("clientType")
  private ClientTypeV1 clientType;

}
