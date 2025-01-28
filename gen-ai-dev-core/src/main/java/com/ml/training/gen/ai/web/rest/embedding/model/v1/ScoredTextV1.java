package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ml.training.gen.ai.web.rest.common.bind.DoubleJsonSerializer;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "text", "score"
})
public class ScoredTextV1 {

  @JsonProperty("text")
  private String text;

  @JsonProperty("score")
  @JsonSerialize(using = DoubleJsonSerializer.class)
  private Double score;

}
