package com.ml.training.gen.ai.service.prompt.impl.sk.repository;

import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChatHistoryInMemoryRepository implements ChatHistoryRepository {

  private static final Logger LOG = LoggerFactory.getLogger(ChatHistoryInMemoryRepository.class);

  private final ConcurrentMap<Object, ChatHistory> data = new ConcurrentHashMap<>();

  @Override
  public Optional<ChatHistory> getHistory(@NonNull final Object id) {
    LOG.info("[{}] Loading chat messages", id);

    return Optional.ofNullable(data.get(id));
  }

  @Override
  public void updateHistory(@NonNull final Object id, @NonNull final ChatHistory history) {
    LOG.info("[{}] Updating chat messages", id);

    data.put(id, history);
  }

  @Override
  public void deleteHistory(@NonNull final Object id) {
    LOG.info("[{}] Deleting chat messages", id);

    data.remove(id);
  }

  private ChatHistory createHistory(final Object id) {
    final var result = new ChatHistory();

//    we could add system message or other modals during chat history creation
//    result.addSystemMessage("Respond in up to four concise sentences. "
//        + "Be accurate and if you don't know the answer, please respond with 'I don't know'");

    return result;
  }

}
