package com.ml.training.gen.ai.web.rest.chat;

import com.ml.training.gen.ai.service.chat.ChatService;
import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.prompt.PromptService;
import com.ml.training.gen.ai.web.rest.chat.mapping.v1.ChatCompletionV1Mapper;
import com.ml.training.gen.ai.web.rest.chat.mapping.v1.ChatV1Mapper;
import com.ml.training.gen.ai.web.rest.chat.mapping.v1.PromptSettingsV1Mapper;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatCompletionV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatPromptV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatV1Request;
import com.ml.training.gen.ai.web.rest.common.error.exception.UnsupportedClientType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/chat/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Chat", description = "Chat API")
public class ChatRestControllerV1 {

  private final ChatService chatService;
  private final List<PromptService> promptServices;

  private final ChatV1Mapper chatMapper;
  private final ChatCompletionV1Mapper completionMapper;
  private final PromptSettingsV1Mapper promptSettingsMapper;

  public ChatRestControllerV1(@NonNull final ChatService chatService,
      @NonNull final List<PromptService> promptServices, @NonNull final ChatV1Mapper chatMapper,
      @NonNull final ChatCompletionV1Mapper completionMapper,
      @NonNull final PromptSettingsV1Mapper promptSettingsMapper) {
    this.chatService = chatService;
    this.promptServices = promptServices;

    this.chatMapper = chatMapper;
    this.completionMapper = completionMapper;
    this.promptSettingsMapper = promptSettingsMapper;
  }

  @PostMapping
  @Operation(tags = "Chat", summary = "Create new chat", responses = {
      @ApiResponse(responseCode = "201",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ChatV1.class))})
  })
  @ResponseStatus(HttpStatus.CREATED)
  public ChatV1 create(@RequestBody @NotNull @Valid final ChatV1Request request) {
    return chatMapper.toResponse(
        chatService.create(chatMapper.fromRequest(request))
    );
  }

  @GetMapping("/{id}")
  @Operation(tags = "Chat", summary = "Get chat details", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ChatV1.class))})
  })
  @ResponseStatus(HttpStatus.OK)
  public ChatV1 getById(@PathVariable("id") @NotNull final Long chatId) {
    return chatMapper.toResponse(
        chatService.getById(chatId)
    );
  }

  @DeleteMapping("/{id}")
  @Operation(tags = "Chat", summary = "Delete chat", responses = {
      @ApiResponse(responseCode = "204")}
  )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") @NotNull final Long chatId) {
    final Optional<Chat> optional = chatService.findById(chatId);
    if (optional.isEmpty()) {
      return;
    }

    final var chat = optional.get();

    final var promptService = getPromptService(chat);
    promptService.clearMemory(chat);

    chatService.delete(chat.getId());
  }

  @PostMapping("/{id}/ask")
  @Operation(tags = "Chat", summary = "Submit prompt", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ChatCompletionV1.class))}),
  })
  @ResponseStatus(HttpStatus.OK)
  public ChatCompletionV1 ask(@PathVariable("id") @NotNull final Long chatId,
      @RequestBody @NotNull @Valid final ChatPromptV1 prompt) {
    final Chat chat = chatService.getById(chatId);
    final var promptService = getPromptService(chat);

    return completionMapper.toResponse(
        promptService.ask(chat, prompt.getInput(), promptSettingsMapper.map(prompt.getSettings()))
    );
  }

  private PromptService getPromptService(final Chat chat) {
    return promptServices.stream()
        .filter(service -> service.supports(chat.getClientType()))
        .findAny()
        .orElseThrow(() -> new UnsupportedClientType(
            String.format("Client type '%s' is not supported by Prompt service",
                chat.getClientType()))
        );
  }

}
