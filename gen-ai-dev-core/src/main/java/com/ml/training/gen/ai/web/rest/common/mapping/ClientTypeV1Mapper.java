package com.ml.training.gen.ai.web.rest.common.mapping;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import org.springframework.stereotype.Component;

@Component
public class ClientTypeV1Mapper {

  public ClientType fromRequest(final ClientTypeV1 clientType) {
    return switch (clientType) {
      case SK_OPEN_AI -> ClientType.SK_OPEN_AI;
      case LC_OPEN_AI -> ClientType.LC_OPEN_AI;
      case LC_HF -> ClientType.LC_HF;
    };
  }

  public ClientTypeV1 toResponse(final ClientType clientType) {
    return switch (clientType) {
      case SK_OPEN_AI -> ClientTypeV1.SK_OPEN_AI;
      case LC_OPEN_AI -> ClientTypeV1.LC_OPEN_AI;
      case LC_HF -> ClientTypeV1.LC_HF;
    };

  }

}
