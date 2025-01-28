package com.ml.training.gen.ai.web.rest.rag;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.rag.indexer.DocumentIndexer;
import com.ml.training.gen.ai.service.rag.prompt.RAGPromptService;
import com.ml.training.gen.ai.web.rest.common.error.exception.UnsupportedClientType;
import com.ml.training.gen.ai.web.rest.rag.mapping.v1.RAGCompletionV1Mapper;
import com.ml.training.gen.ai.web.rest.rag.model.v1.RAGCompletionV1;
import com.ml.training.gen.ai.web.rest.rag.model.v1.RAGPromptV1;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value = "/rag/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "RAG", description = "RAG API")
public class RAGRestControllerV1 {

  private static final String DOCUMENT_RESOURCE_FILE_PATH = "rag/data/role-library.pdf";

  private final List<DocumentIndexer> indexers;
  private final List<RAGPromptService> promptServices;

  private final RAGCompletionV1Mapper mapper;

  public RAGRestControllerV1(@NonNull final List<DocumentIndexer> indexers,
      @NonNull final List<RAGPromptService> promptServices,
      @NonNull final RAGCompletionV1Mapper mapper) {
    this.indexers = indexers;
    this.promptServices = promptServices;

    this.mapper = mapper;
  }

  @PostMapping("/init")
  @Operation(tags = "RAG", summary = "Source documents indexing", responses = {
      @ApiResponse(responseCode = "200")
  })
  @ResponseStatus(HttpStatus.OK)
  public void initialize() throws IOException {
    final DocumentIndexer indexer = getIndexer(ClientType.SK_OPEN_AI);
    final var resource = new ClassPathResource(DOCUMENT_RESOURCE_FILE_PATH);

    try (final InputStream source = resource.getInputStream()) {
      indexer.execute(source);
    }
  }

  @PostMapping("/ask")
  @Operation(tags = "RAG", summary = "Submit prompt", responses = {
      @ApiResponse(responseCode = "200",
          content = {@Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RAGCompletionV1.class))}),
  })
  @ResponseStatus(HttpStatus.OK)
  public RAGCompletionV1 ask(@RequestBody @Valid final RAGPromptV1 prompt) throws IOException {
    final RAGPromptService service = getPromptService(ClientType.SK_OPEN_AI);

    return mapper.map(
        service.ask(prompt.getQuestion())
    );
  }

  private RAGPromptService getPromptService(final ClientType clientType) {
    return promptServices.stream()
        .filter(service -> service.supports(clientType))
        .findAny()
        .orElseThrow(() -> new UnsupportedClientType(
            String.format("Client type '%s' is not supported by RAG Prompt service", clientType))
        );
  }

  private DocumentIndexer getIndexer(final ClientType clientType) {
    return indexers.stream()
        .filter(service -> service.supports(clientType))
        .findAny()
        .orElseThrow(() -> new UnsupportedClientType(
            String.format("Client type '%s' is not supported by Document indexer", clientType))
        );
  }

}
