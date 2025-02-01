package com.ml.training.gen.ai.service.prompt.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChatMemoryInMemoryRepository implements ChatMemoryStore {

  private static final Logger LOG = LoggerFactory.getLogger(ChatMemoryInMemoryRepository.class);

  private Map<Object, List<ChatMessage>> data = new ConcurrentHashMap<>();

  @Override
  public List<ChatMessage> getMessages(@NonNull final Object id) {
    LOG.info("[{}] Loading chat messages", id);

    return data.getOrDefault(id, Collections.emptyList());
  }

  @Override
  public void updateMessages(@NonNull final Object id, @NonNull final List<ChatMessage> list) {
    LOG.info("[{}] Updating chat messages", id);

    data.put(id, list);
  }

  @Override
  public void deleteMessages(@NonNull Object id) {
    LOG.info("[{}] Deleting chat messages", id);

    data.remove(id);
  }

}
