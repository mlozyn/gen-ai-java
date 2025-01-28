package com.ml.training.gen.ai.web.rest.chat.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChatPromptV1 {

  @NotEmpty
  @JsonProperty("input")
  private String input;

  @JsonProperty("settings")
  private PromptSettingsV1 settings;

}
