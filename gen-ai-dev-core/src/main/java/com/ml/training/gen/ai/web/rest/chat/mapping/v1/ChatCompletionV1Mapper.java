package com.ml.training.gen.ai.web.rest.chat.mapping.v1;

import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatCompletionV1;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChatCompletionV1Mapper {

  public ChatCompletionV1 toResponse(@NonNull final String message) {
    final var result = new ChatCompletionV1();
    result.setOutput(message);

    return result;
  }

}
