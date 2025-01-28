package com.ml.training.gen.ai.service.chat.mapping;

import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.chat.repository.entity.ChatEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

  public Chat fromEntity(@NonNull final ChatEntity entity) {
    return Chat.builder()
        .withId(entity.getId())

        .withName(entity.getName())
        .withClientType(ClientType.forValue(entity.getClientType()))
        .withSystemMessage(entity.getSystemMessage())

        .withCreatedAt(entity.getCreatedAt())

        .build();
  }

  public ChatEntity toEntity(@NonNull final Chat chat) {
    return ChatEntity.builder()
        .withId(chat.getId())

        .withName(chat.getName())
        .withClientType(chat.getClientType().getValue())
        .withSystemMessage(chat.getSystemMessage())

        .withCreatedAt(chat.getCreatedAt())

        .build();
  }


}
