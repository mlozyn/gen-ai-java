package com.ml.training.gen.ai.web.rest.chat.model.v1;

import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatV1Request {

  @NotBlank
  @JsonProperty("name")
  private String name;

  @NotNull
  @JsonProperty("clientType")
  private ClientTypeV1 clientType;

  @JsonProperty("systemMessage")
  private String systemMessage;

}
