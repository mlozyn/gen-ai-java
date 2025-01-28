package com.ml.training.gen.ai.web.rest.chat.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatCompletionV1 {

  @JsonProperty("output")
  private String output;

}
