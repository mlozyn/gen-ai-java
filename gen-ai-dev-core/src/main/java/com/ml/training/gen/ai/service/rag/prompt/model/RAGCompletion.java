package com.ml.training.gen.ai.service.rag.prompt.model;

import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(setterPrefix = "with")
@ToString
public class RAGCompletion {

  private String answer;

  private List<String> queries;
  private List<ScoredText> sources;

}
