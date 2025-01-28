package com.ml.training.gen.ai.web.rest.chat.mapping.v1;

import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatV1Request;
import com.ml.training.gen.ai.web.rest.common.mapping.ClientTypeV1Mapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChatV1Mapper {

  private final ClientTypeV1Mapper clientTypeMapper;

  public ChatV1Mapper(@NonNull final ClientTypeV1Mapper clientTypeMapper) {
    this.clientTypeMapper = clientTypeMapper;
  }

  public ChatV1 toResponse(@NonNull final Chat source) {
    final var result = new ChatV1();
    result.setId(source.getId());

    result.setName(source.getName());
    result.setClientType(clientTypeMapper.toResponse(source.getClientType()));
    result.setSystemMessage(source.getSystemMessage());

    result.setCreatedAt(source.getCreatedAt());

    return result;
  }

  public Chat fromRequest(@NonNull final ChatV1Request source) {
    return Chat.builder()

        .withName(source.getName())
        .withClientType(clientTypeMapper.fromRequest(source.getClientType()))
        .withSystemMessage(source.getSystemMessage())

        .build();
  }

}
