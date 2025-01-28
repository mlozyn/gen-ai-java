package com.ml.training.gen.ai.web.rest.embedding.model.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@JsonPropertyOrder({
    "query", "documents"
})
@JsonInclude(Include.NON_NULL)
public class DocumentsScoreV1Response {

  @JsonProperty("query")
  private String query;
  @JsonProperty("documents")
  private List<ScoredTextV1> documents;

}
