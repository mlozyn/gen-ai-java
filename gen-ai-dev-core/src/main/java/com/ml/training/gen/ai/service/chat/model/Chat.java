package com.ml.training.gen.ai.service.chat.model;

import com.ml.training.gen.ai.service.common.model.ClientType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class Chat {

  private Long id;
  private String name;
  private ClientType clientType;

  private String systemMessage;

  private Long createdAt;

}
