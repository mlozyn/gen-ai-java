package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@JsonPropertyOrder({
    "data"
})
public class EmbeddingSearchV1Response {

  @JsonProperty("data")
  private List<ScoredTextV1> data;

}
