package com.ml.training.gen.ai.web.rest.rag.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "answer", "questions", "sources"
})
public class RAGCompletionV1 {

  @JsonProperty("answer")
  private String answer;

  @JsonProperty("questions")
  private List<String> questions;
  @JsonProperty("sources")
  private List<String> sources;

}
