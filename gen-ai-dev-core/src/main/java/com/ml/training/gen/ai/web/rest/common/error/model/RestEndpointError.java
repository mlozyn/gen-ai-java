package com.ml.training.gen.ai.web.rest.common.error.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ml.training.gen.ai.web.rest.common.bind.ZonedDateTimeJsonSerializer;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
@JsonPropertyOrder({"title", "detail", "error", "timestamp"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestEndpointError {

  @JsonProperty("title")
  private String title;
  @JsonProperty("detail")
  private String detail;
  @JsonProperty("error")
  private String error;

  @JsonProperty(value = "timestamp")
  @JsonSerialize(using = ZonedDateTimeJsonSerializer.class)
  private Instant timestamp;

}
