package com.ml.training.gen.ai.service.chat;

import com.ml.training.gen.ai.service.chat.error.ChatNotFoundException;
import com.ml.training.gen.ai.service.chat.mapping.ChatMapper;
import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.chat.repository.ChatRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

  private static final Logger LOG = LoggerFactory.getLogger(ChatServiceImpl.class);

  private final ChatRepository repository;
  private final ChatMapper mapper;

  public ChatServiceImpl(@NonNull final ChatRepository repository,
      @NonNull final ChatMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Chat getById(@NonNull final Long id) {
    return findById(id)
        .orElseThrow(() -> new ChatNotFoundException(
            String.format("Chat with id '%s' does not exist", id))
        );
  }

  @Override
  public Optional<Chat> findById(@NonNull final Long id) {
    return repository.findById(id)
        .map(mapper::fromEntity);
  }

  @Override
  public Chat create(@NonNull final Chat chat) {
    LOG.info("Creating new chat with name '{}'", chat.getName());

    return mapper.fromEntity(
        repository.create(mapper.toEntity(chat))
    );
  }

  @Override
  public void delete(@NonNull final Long id) {
    LOG.info("[{}] Deleting chat", id);

    repository.delete(id);
  }

}
