package com.ml.training.gen.ai.service.prompt.impl.sk.repository;

import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface ChatHistoryRepository {

  Optional<ChatHistory> getHistory(@NonNull final Object id);

  void updateHistory(@NonNull final Object id, @NonNull final ChatHistory history);

  void deleteHistory(@NonNull Object id);

}
