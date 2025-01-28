package com.ml.training.gen.ai.web.rest.sentiment.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SentimentCompletionV1 {

  @JsonProperty("output")
  private String output;

}
