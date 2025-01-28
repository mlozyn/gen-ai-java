package com.ml.training.gen.ai.web.rest.common.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClientTypeV1 {

  @JsonProperty("sk-open-ai")
  SK_OPEN_AI,

  @JsonProperty("lc-open-ai")
  LC_OPEN_AI,
  @JsonProperty("lc-hf")
  LC_HF

}
