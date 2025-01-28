package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmbeddingV1Request {

  @NotEmpty
  @JsonProperty("text")
  private String text;

  @NotNull
  @JsonProperty("clientType")
  private ClientTypeV1 clientType;

}
