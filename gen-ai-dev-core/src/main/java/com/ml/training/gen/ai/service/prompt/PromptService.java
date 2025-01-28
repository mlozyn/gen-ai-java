package com.ml.training.gen.ai.service.prompt;

import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface PromptService {

  boolean supports(final ClientType clientType);

  String ask(@NonNull final Chat chat, @NonNull final String message,
      @Nullable final PromptSettings settings);

  void clearMemory(@NonNull final Chat chat);

}
