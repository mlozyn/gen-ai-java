package com.ml.training.gen.ai.service.rag.prompt;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.rag.content.RAGContentService;
import com.ml.training.gen.ai.service.rag.prompt.model.RAGCompletion;
import com.ml.training.gen.ai.service.embedding.model.ScoredText;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class SemanticKernelRAGPromptService implements RAGPromptService {

  private static final Logger LOG = LoggerFactory.getLogger(SemanticKernelRAGPromptService.class);

  private static final int Q_VERSIONS_NUMBER = 3;

  private final ClientType clientType;
  private final RAGContentService contentService;

  private final Kernel kernel;

  public SemanticKernelRAGPromptService(@NonNull final ClientType clientType,
      @NonNull final RAGContentService contentService, @NonNull final Kernel kernel) {
    this.clientType = clientType;
    this.contentService = contentService;

    this.kernel = kernel;
  }

  @Override
  public boolean supports(@NonNull final ClientType clientType) {
    return this.clientType == clientType;
  }

  @Override
  public RAGCompletion ask(@NonNull final String question) {
    final StopWatch watch = StopWatch.createStarted();
    LOG.info("[rag] Question answering STARTED. Question: '{}'", question);

    // STEP 1.1: Query Transformation - RAG-Fusion Approach.
    // This involves prompt tuning by leveraging a large language model (LLM)
    // to generate multiple queries from various perspectives based on the user's input query.
    // Each query retrieves a set of relevant documents, and the unique union of all
    // these sets is taken to create a broader collection of potentially relevant documents.
    final List<String> queries = getQueries(question);

    // STEP 1.2: Documents retrieval based on rephrased queries.
    final List<ScoredText> sources = contentService.getSources(queries);

    // STEP 2: Get final Answer
    final String result = getAnswer(question, sources);

    watch.stop();
    LOG.info("[rag] Question answering COMPLETED. Took: {} ms. Answer: {}. ",
        watch.getTime(TimeUnit.MILLISECONDS), result);

    return RAGCompletion.builder()
        .withAnswer(result)

        .withQueries(queries)
        .withSources(sources)

        .build();
  }

  private String getAnswer(final String question, final List<ScoredText> sources) {
    LOG.info("[rag] Question answering with context STARTED");

    final var variables =
        KernelFunctionArguments.builder()
            .withVariable("sources", sources)
            .withVariable("input", question)
            .build();

    final FunctionResult<String> response = kernel
        .invokeAsync("RAG", "AnswerQuestion")
        .withArguments(variables)
        .withResultType(String.class)
        .block();

    final String result = response.getResult();
    LOG.info("[rag] Question answering with context COMPLETED. Answer: {}", result);

    return result;
  }

  private List<String> getQueries(final String question) {
    LOG.info("[rag] Query transformation STARTED. Versions number: {}", Q_VERSIONS_NUMBER);

    final var variables =
        KernelFunctionArguments.builder()
            .withVariable("versions_number", Q_VERSIONS_NUMBER)
            .withVariable("input", question)
            .build();

    final FunctionResult<String> response = kernel
        .invokeAsync("RAG", "MultiQueryQuestion")
        .withArguments(variables)
        .withResultType(String.class)
        .block();

    final String answer = response.getResult();

    final String[] result = answer.split("\\n");
    LOG.info("[rag] Query transformation COMPLETED");

    Assert.isTrue(result.length == Q_VERSIONS_NUMBER,
        "Question versions number does not match expected size");

    return Arrays.asList(result);
  }

}
