package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class DocumentsScoreV1Request {

  @NotEmpty
  @JsonProperty("query")
  private String query;

  @NotEmpty
  @JsonProperty("documents")
  private List<String> documents;

  @NotNull
  @JsonProperty("clientType")
  private ClientTypeV1 clientType;

}
