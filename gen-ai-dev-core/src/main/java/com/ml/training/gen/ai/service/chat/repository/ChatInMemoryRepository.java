package com.ml.training.gen.ai.service.chat.repository;

import com.ml.training.gen.ai.service.chat.repository.entity.ChatEntity;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ChatInMemoryRepository implements ChatRepository {

  private final AtomicLong generator = new AtomicLong();
  private final Map<Long, ChatEntity> data = new ConcurrentHashMap<>();

  @Override
  // we don't care here about name duplication
  public ChatEntity create(@NonNull final ChatEntity entity) {
    final var result = ChatEntity.builder()
        .withId(generator.incrementAndGet())
        .withName(entity.getName())
        .withClientType(entity.getClientType())
        .withSystemMessage(entity.getSystemMessage())
        .withCreatedAt(System.currentTimeMillis())

        .build();

    data.put(result.getId(), result);

    return result;
  }

  @Override
  public Optional<ChatEntity> findById(@NonNull final Long id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public void delete(@NonNull final Long id) {
    data.remove(id);
  }

}
