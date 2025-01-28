package com.ml.training.gen.ai.service.prompt;

import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.prompt.impl.sk.mapping.PromptSettingsMapper;
import com.ml.training.gen.ai.service.prompt.impl.sk.repository.ChatHistoryRepository;
import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationContext.Builder;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


public class SemanticKernelPromptService implements PromptService {

  private static final Logger LOG = LoggerFactory.getLogger(SemanticKernelPromptService.class);

  private final ClientType clientType;

  private final Kernel kernel;
  private final PromptExecutionSettings settings;
  private final ChatHistoryRepository repository;

  private final PromptSettingsMapper settingsMapper;

  public SemanticKernelPromptService(@NonNull final ClientType clientType,
      @NonNull final Kernel kernel, @NonNull final PromptExecutionSettings settings,
      @NonNull final ChatHistoryRepository repository,
      @NonNull final PromptSettingsMapper settingsMapper) {
    this.clientType = clientType;

    this.kernel = kernel;
    this.settings = settings;
    this.repository = repository;

    this.settingsMapper = settingsMapper;
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public String ask(@NonNull final Chat chat, @NonNull final String message,
      @Nullable final PromptSettings promptSettings) {
    Assert.isTrue(supports(chat.getClientType()), "Unsupported client type by prompt service");

    LOG.info("[{}] Prompt execution STARTED Message: '{}'", chat.getName(), message);
    final var stopWatch = StopWatch.createStarted();

    // chat completion service
    final var chatCompletionService = getChatCompletionService();

    // chat history
    final ChatHistory history = getOrCreateHistory(chat);
    // add user message
    history.addUserMessage(message);

    // invocation context
    final var invocationContext = createInvocationContext(getExecutionSettings(promptSettings));

    // Prompt AI for response to users input
    final List<ChatMessageContent<?>> completions = chatCompletionService
        .getChatMessageContentsAsync(history, kernel, invocationContext)
        .block();

    if (Objects.nonNull(completions)) {
      // update history
      completions.forEach(history::addMessage);
      repository.updateHistory(chat.getId(), history);
    }

    stopWatch.stop();
    LOG.info("[{}] Prompt execution COMPLETED. Took: {} ms",
        chat.getName(), stopWatch.getTime(TimeUnit.MILLISECONDS));

    return buildAnswer(completions);
  }

  public void clearMemory(@NonNull final Chat chat) {
    Assert.isTrue(supports(chat.getClientType()), "Unsupported client type by prompt service");

    repository.deleteHistory(chat.getId());
  }

  private String buildAnswer(final List<ChatMessageContent<?>> completions) {
    if (CollectionUtils.isEmpty(completions)) {
      return "";
    }

    return completions.stream()
        .filter(completion ->
            completion.getAuthorRole() == AuthorRole.ASSISTANT
                && StringUtils.isNotBlank(completion.getContent())
        )
        .map(ChatMessageContent::getContent)
        .collect(Collectors.joining());
  }

  private ChatHistory getOrCreateHistory(final Chat chat) {
    final var optional = repository.getHistory(chat.getId());
    if (optional.isPresent()) {
      return optional.get();
    }

    final var result = new ChatHistory();

    // we could add system message or other modals during chat history creation
    if (StringUtils.isNotBlank(chat.getSystemMessage())) {
      LOG.info("[{}] Adding system message ...", chat.getName());
      result.addSystemMessage(chat.getSystemMessage());
    }

    return result;
  }

  private InvocationContext createInvocationContext(final PromptExecutionSettings settings) {
    return new Builder()
        .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
        .withPromptExecutionSettings(settings)
        // plugins
        .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
        .build();
  }

  private PromptExecutionSettings getExecutionSettings(final PromptSettings promptSettings) {
    return Optional.ofNullable(promptSettings)
        .map(settingsMapper::map)
        .orElse(this.settings);
  }

  private ChatCompletionService getChatCompletionService() {
    try {
      return kernel.getService(ChatCompletionService.class);
    } catch (final ServiceNotFoundException exception) {
      throw new RuntimeException("ChatCompletionService not found", exception);
    }
  }

}
