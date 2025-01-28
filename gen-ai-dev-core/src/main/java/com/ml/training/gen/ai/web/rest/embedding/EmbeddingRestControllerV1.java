package com.ml.training.gen.ai.web.rest.embedding;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.web.rest.common.error.exception.UnsupportedClientType;
import com.ml.training.gen.ai.web.rest.common.mapping.ClientTypeV1Mapper;
import com.ml.training.gen.ai.web.rest.embedding.mapping.EmbeddingV1Mapper;
import com.ml.training.gen.ai.web.rest.embedding.mapping.SimilarityScoreV1Mapper;
import com.ml.training.gen.ai.web.rest.embedding.mapping.SimilaritySearchV1Mapper;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.DocumentsScoreV1Request;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.DocumentsScoreV1Response;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.EmbeddingSearchV1Request;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.EmbeddingSearchV1Response;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.EmbeddingV1Request;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.EmbeddingV1Response;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/embedding/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Embedding", description = "Embedding API")
public class EmbeddingRestControllerV1 {

  private final List<EmbeddingService> services;

  private final ClientTypeV1Mapper clientTypeMapper;
  private final EmbeddingV1Mapper embeddingMapper;
  private final SimilaritySearchV1Mapper searchMapper;
  private final SimilarityScoreV1Mapper scoreMapper;

  public EmbeddingRestControllerV1(@NonNull final List<EmbeddingService> services,
      @NonNull final ClientTypeV1Mapper clientTypeMapper,
      @NonNull final EmbeddingV1Mapper embeddingMapper,
      @NonNull final SimilaritySearchV1Mapper searchMapper,
      @NonNull final SimilarityScoreV1Mapper scoreMapper) {
    this.services = services;

    this.clientTypeMapper = clientTypeMapper;
    this.embeddingMapper = embeddingMapper;
    this.searchMapper = searchMapper;
    this.scoreMapper = scoreMapper;
  }

  @PostMapping("/embed")
  @Operation(tags = "Embedding", summary = "Get text embedding", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = EmbeddingV1Response.class))}),
  })
  @ResponseStatus(HttpStatus.OK)
  public EmbeddingV1Response embed(
      @RequestParam(value = "persist", required = false, defaultValue = "false") final Boolean persist,
      @RequestBody @NotNull @Valid final EmbeddingV1Request request) {
    final var clientType = clientTypeMapper.fromRequest(request.getClientType());
    final var service = getService(clientType);

    return embeddingMapper.toResponse(
        persist ? service.save(request.getText()) : service.embed(request.getText())
    );
  }

  @PostMapping("/search")
  @Operation(tags = "Embedding", summary = "Search for similar sentences", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = EmbeddingSearchV1Response.class))}),
  })
  @ResponseStatus(HttpStatus.OK)
  public EmbeddingSearchV1Response search(
      @RequestBody @NotNull @Valid final EmbeddingSearchV1Request request) {
    final var clientType = clientTypeMapper.fromRequest(request.getClientType());

    return searchMapper.toResponse(
        getService(clientType).search(request.getQuery(), request.getLimit(),
            request.getScoreThreshold())
    );
  }

  @PostMapping("/score")
  @Operation(tags = "Embedding", summary = "Score text similarity", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = DocumentsScoreV1Response.class))}),
  })
  @ResponseStatus(HttpStatus.OK)
  public DocumentsScoreV1Response score(
      @RequestBody @NotNull @Valid final DocumentsScoreV1Request request) {
    final var clientType = clientTypeMapper.fromRequest(request.getClientType());

    return scoreMapper.toResponse(
        request.getQuery(),
        getService(clientType).score(request.getQuery(), request.getDocuments())
    );
  }

  private EmbeddingService getService(final ClientType clientType) {
    return services.stream()
        .filter(service -> service.supports(clientType))
        .findAny()
        .orElseThrow(() -> new UnsupportedClientType(
            String.format("Client type '%s' is not supported by Embedding service", clientType))
        );
  }

}
