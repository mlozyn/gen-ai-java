package com.ml.training.gen.ai.web.rest.sentiment;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.sentiment.SentimentService;
import com.ml.training.gen.ai.web.rest.common.error.exception.UnsupportedClientType;
import com.ml.training.gen.ai.web.rest.common.mapping.ClientTypeV1Mapper;
import com.ml.training.gen.ai.web.rest.sentiment.mapping.SentimentCompletionV1Mapper;
import com.ml.training.gen.ai.web.rest.sentiment.model.v1.SentimentCompletionV1;
import com.ml.training.gen.ai.web.rest.sentiment.model.v1.SentimentPromptV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/sentiment/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sentiment", description = "Sentiment API")
public class SentimentRestControllerV1 {

  private final List<SentimentService> services;

  private final ClientTypeV1Mapper clientTypeMapper;
  private final SentimentCompletionV1Mapper completionMapper;

  public SentimentRestControllerV1(@NonNull final List<SentimentService> services,
      @NonNull final ClientTypeV1Mapper clientTypeMapper,
      @NonNull final SentimentCompletionV1Mapper completionMapper) {
    this.services = services;

    this.clientTypeMapper = clientTypeMapper;
    this.completionMapper = completionMapper;
  }

  @PostMapping
  @Operation(tags = "Sentiment", summary = "Analyze message sentiment", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = SentimentCompletionV1.class))}),
  })
  @ResponseStatus(HttpStatus.OK)
  public SentimentCompletionV1 getSentiment(
      @RequestBody @NotNull @Valid final SentimentPromptV1 prompt) {
    final var clientType = clientTypeMapper.fromRequest(prompt.getClientType());

    return completionMapper.toResponse(
        getService(clientType).getSentiment(prompt.getInput())
    );
  }

  private SentimentService getService(final ClientType clientType) {
    return services.stream()
        .filter(service -> service.supports(clientType))
        .findAny()
        .orElseThrow(() -> new UnsupportedClientType(
            String.format("Client type '%s' is not supported by Sentiment service", clientType))
        );
  }

}
