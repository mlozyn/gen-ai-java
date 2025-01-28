package com.ml.training.gen.ai.service.chat;

import com.ml.training.gen.ai.service.chat.model.Chat;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface ChatService {

  Chat getById(@NonNull final Long id);

  Optional<Chat> findById(@NonNull final Long id);

  Chat create(@NonNull final Chat chat);

  void delete(@NonNull final Long id);

}
