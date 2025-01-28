package com.ml.training.gen.ai.web.rest.rag.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RAGPromptV1 {

  @NotEmpty
  @JsonProperty("question")
  private String question;

}
