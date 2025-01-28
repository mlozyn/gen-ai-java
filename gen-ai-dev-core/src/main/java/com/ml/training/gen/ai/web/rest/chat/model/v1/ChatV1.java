package com.ml.training.gen.ai.web.rest.chat.model.v1;

import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "id", "name", "clientType", "systemMessage",
    "createdAt"
})
public class ChatV1 {

  @JsonProperty("id")
  private Long id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("clientType")
  private ClientTypeV1 clientType;

  @JsonProperty("systemMessage")
  private String systemMessage;

  @JsonProperty("createdAt")
  private Long createdAt;

}
