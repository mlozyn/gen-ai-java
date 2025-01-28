package com.ml.training.gen.ai.service.chat.repository;

import com.ml.training.gen.ai.service.chat.repository.entity.ChatEntity;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface ChatRepository {

  ChatEntity create(@NonNull final ChatEntity chat);

  Optional<ChatEntity> findById(@NonNull final Long id);

  void delete(@NonNull final Long id);

}
