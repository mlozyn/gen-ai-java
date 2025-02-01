package com.ml.training.gen.ai.service.prompt;

import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/*
  Low level API usage - cumbersome ...
 */

public class LangChainPromptServiceL implements PromptService {

  private static final Logger LOG = LoggerFactory.getLogger(LangChainPromptServiceL.class);

  private static final int HISTORY_MAX_MESSAGES = 10;

  private final ClientType clientType;
  private final ChatLanguageModel languageModel;

  private final ChatMemoryStore repository;

  public LangChainPromptServiceL(@NonNull final ClientType clientType,
      @NonNull final ChatLanguageModel languageModel, @NonNull final ChatMemoryStore repository) {
    this.clientType = clientType;

    this.languageModel = languageModel;
    this.repository = repository;
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public String ask(@NonNull final Chat chat, @NonNull final String message,
      @Nullable final PromptSettings settings) {
    Assert.isTrue(supports(chat.getClientType()), "Unsupported client type by prompt service");

    // LangChain4j does not allow to change prompt exec settings on fly
    // you could specify them only during the model initialization

    LOG.info("[{}] Prompt execution STARTED Message: '{}'", chat.getName(), message);
    final var stopWatch = StopWatch.createStarted();

    final var chatMemory = getOrCreateMemory(chat);

    // add user message
    final var userMessage = UserMessage.from(message);
    chatMemory.add(userMessage);

    final Response<AiMessage> response = languageModel.generate(chatMemory.messages());

    stopWatch.stop();
    LOG.info("[{}] Prompt execution COMPLETED. Usage: {}. Finish reason: {}. Took: {} ms",
        chat.getName(), response.tokenUsage(), response.finishReason(),
        stopWatch.getTime(TimeUnit.MILLISECONDS));

    final var aiMessage = response.content();
    chatMemory.add(aiMessage);

    return aiMessage.text();
  }

  @Override
  public void clearMemory(@NonNull final Chat chat) {
    Assert.isTrue(supports(chat.getClientType()), "Unsupported client type by prompt service");

    repository.deleteMessages(chat.getId());
  }

  private ChatMemory getOrCreateMemory(final Chat chat) {
    final var result = MessageWindowChatMemory.builder()
        .id(chat.getId())
        .chatMemoryStore(repository)
        .maxMessages(HISTORY_MAX_MESSAGES)
        .build();

    if (StringUtils.isNotBlank(chat.getSystemMessage())
        && CollectionUtils.isEmpty(result.messages())) {
      LOG.info("[{}] Adding system message ...", chat.getName());

      final var systemMessage = SystemMessage.from(chat.getSystemMessage());
      result.add(systemMessage);
    }

    return result;
  }

}
