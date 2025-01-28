package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@JsonPropertyOrder({
    "id", "text", "dimensions", "vector"
})
@JsonInclude(Include.NON_NULL)
public class EmbeddingV1Response {

  @JsonProperty("id")
  private UUID id;
  @JsonProperty("text")
  private String text;
  @JsonProperty("dimensions")
  private Integer dimensions;

  @JsonProperty("vector")
  private List<Float> vector;

}
