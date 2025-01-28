package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmbeddingSearchV1Request {

  @JsonProperty("limit")
  private Integer limit = 10;
  @JsonProperty("scoreThreshold")
  private Float scoreThreshold;

  @NotEmpty
  @JsonProperty("query")
  private String query;

  @NotNull
  @JsonProperty("clientType")
  private ClientTypeV1 clientType;

}
