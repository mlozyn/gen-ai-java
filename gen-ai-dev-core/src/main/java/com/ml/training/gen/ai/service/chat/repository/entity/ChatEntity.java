package com.ml.training.gen.ai.service.chat.repository.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class ChatEntity {

  private Long id;
  private String name;
  private Integer clientType;

  private String systemMessage;

  private Long createdAt;

}
