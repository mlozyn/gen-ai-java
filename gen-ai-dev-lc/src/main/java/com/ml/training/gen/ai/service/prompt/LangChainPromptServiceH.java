package com.ml.training.gen.ai.service.prompt;

import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/*
  https://docs.langchain4j.dev/tutorials/ai-services/
  High level API usage - simple and straightforward
*/

public class LangChainPromptServiceH implements PromptService {

  private static final Logger LOG = LoggerFactory.getLogger(LangChainPromptServiceH.class);

  private final ClientType clientType;

  private final LangChainChatService chatService;
  private final ChatMemoryStore repository;

  public LangChainPromptServiceH(@NonNull final ClientType clientType,
      @NonNull final LangChainChatService chatService,
      @NonNull final ChatMemoryStore repository) {
    this.clientType = clientType;

    this.chatService = chatService;
    this.repository = repository;
  }

  @Override
  public boolean supports(final @NonNull ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public String ask(@NonNull final Chat chat,
      @NonNull final String message, @Nullable final PromptSettings settings) {
    Assert.isTrue(supports(chat.getClientType()), "Unsupported client type by prompt service");

    LOG.info("[{}] Prompt execution STARTED Message: '{}'", chat.getName(), message);
    final var stopWatch = StopWatch.createStarted();

    final var result = StringUtils.isBlank(chat.getSystemMessage())
        ? chatService.ask(chat.getId(), message)
        : chatService.ask(chat.getId(), chat.getSystemMessage(), message);

    stopWatch.stop();
    LOG.info("[{}] Prompt execution COMPLETED. Usage: {}. Finish reason: {}. Took: {} ms",
        chat.getName(), result.tokenUsage(), result.finishReason(),
        stopWatch.getTime(TimeUnit.MILLISECONDS));

    return result.content();
  }

  @Override
  public void clearMemory(@NonNull final Chat chat) {
    Assert.isTrue(supports(chat.getClientType()), "Unsupported client type by prompt service");
    repository.deleteMessages(chat.getId());
  }

}
