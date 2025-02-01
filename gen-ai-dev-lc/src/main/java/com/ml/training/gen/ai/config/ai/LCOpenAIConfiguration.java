package com.ml.training.gen.ai.config.ai;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.embedding.EmbeddingServiceImpl;
import com.ml.training.gen.ai.service.embedding.LangChainEmbeddingFunction;
import com.ml.training.gen.ai.service.embedding.mapping.TextEntityMapper;
import com.ml.training.gen.ai.service.embedding.repository.EmbeddingRepository;
import com.ml.training.gen.ai.service.prompt.LangChainChatService;
import com.ml.training.gen.ai.service.prompt.LangChainPromptServiceH;
import com.ml.training.gen.ai.service.rag.content.LangChainRAGContentRetriever;
import com.ml.training.gen.ai.service.rag.indexer.DocumentIndexer;
import com.ml.training.gen.ai.service.rag.indexer.LangChainDocumentIndexer;
import com.ml.training.gen.ai.service.rag.prompt.LangChainRAGAssistant;
import com.ml.training.gen.ai.service.rag.prompt.LangChainRAGPromptService;
import com.ml.training.gen.ai.service.sentiment.LangChainSentimentService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

/*
  LangChain4j supports high level & low level APIs
  As part of this project low level API is used
*/

@Configuration
public class LCOpenAIConfiguration {

  private static final ClientType CLIENT_TYPE = ClientType.LC_OPEN_AI;

  private static final int HISTORY_MAX_MESSAGES = 10;

  @Autowired
  private EmbeddingRepository embeddingRepository;

  @Bean
  public LangChainRAGPromptService lcOpenAIRAGPromptService(
      @NonNull final LangChainRAGAssistant assistant) {
    return new LangChainRAGPromptService(CLIENT_TYPE, assistant);
  }

  @Bean
  public LangChainRAGAssistant lcOpenAIRAGAssistant(
      @NonNull final AzureOpenAiChatModel chatModel,
      @NonNull @Qualifier("lcOpenAIContentRetriever") final ContentRetriever contentRetrieval) {
    // query transformer
    final ExpandingQueryTransformer queryTransformer = ExpandingQueryTransformer.builder()
        .chatLanguageModel(chatModel)
        .n(3)
        .build();

    // content aggregator
    final ContentAggregator contentAggregator = new DefaultContentAggregator();

    // content injector - inject contents into final prompt
    final ContentInjector contentInjector = DefaultContentInjector.builder()
        .build();

    final RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
        .queryTransformer(queryTransformer)
        .contentRetriever(contentRetrieval)
        .contentAggregator(contentAggregator)
        .contentInjector(contentInjector)
        // to run all above simultaneously
        //.executor(executor)
        .build();

    return AiServices.builder(LangChainRAGAssistant.class)
        .chatLanguageModel(chatModel)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        // simple way, naive RAG
        //.contentRetriever(contentRetriever)
        .retrievalAugmentor(retrievalAugmentor)
        .build();
  }

  @Bean
  public ContentRetriever lcOpenAIContentRetriever(
      @NonNull @Qualifier("lcOpenAIEmbeddingService") final EmbeddingService embeddingService) {
    return new LangChainRAGContentRetriever(embeddingService);
  }

  @Bean
  public DocumentIndexer lcOpenAIDocumentIndexer(
      @NonNull @Qualifier("lcOpenAIEmbeddingService") final EmbeddingService embeddingService) {
    return new LangChainDocumentIndexer(embeddingService);
  }

  @Bean
  public EmbeddingService lcOpenAIEmbeddingService(
      @NonNull final TextEntityMapper mapper,
      @NonNull @Qualifier("lcOpenAIEmbeddingFunction") final LangChainEmbeddingFunction embeddingFunction) {
    return new EmbeddingServiceImpl(embeddingRepository, mapper, embeddingFunction);
  }

  @Bean
  public LangChainSentimentService lcOpenAISentimentService(
      @NonNull final AzureOpenAiChatModel chatModel) {
    return new LangChainSentimentService(
        CLIENT_TYPE,
        chatModel
    );
  }

//  Low level API
//  @Bean
//  public LangChainPromptServiceL lcOpenAIPromptService(@NonNull final AzureOpenAiChatModel chatModel,
//      @NonNull final ChatMemoryStore repository) {
//    return new LangChainPromptServiceL(
//        CLIENT_TYPE,
//        chatModel, repository
//    );
//  }

  //  High level API
  @Bean
  public LangChainPromptServiceH lcOpenAIPromptService(
      @NonNull final LangChainChatService chatService,
      @NonNull final ChatMemoryStore repository) {
    return new LangChainPromptServiceH(
        CLIENT_TYPE,
        chatService, repository
    );
  }

  @Bean
  public LangChainEmbeddingFunction lcOpenAIEmbeddingFunction(
      @NonNull final AzureOpenAiEmbeddingModel embeddingModel) {
    return new LangChainEmbeddingFunction(
        CLIENT_TYPE,
        embeddingModel
    );
  }

  @Bean
  public AzureOpenAiEmbeddingModel lcOpenAiEmbeddingModel(
      @NonNull final OpenAIClientConfiguration config) {
    final var embeddingConfig = config.getEmbedding();

    final var builder = AzureOpenAiEmbeddingModel.builder()
        .apiKey(config.getKey())
        .endpoint(config.getEndpoint())
        .deploymentName(embeddingConfig.getModelName())

        .logRequestsAndResponses(false);

    return builder.build();
  }

  @Bean
  public LangChainChatService lcOpenAiChatService(
      @NonNull final AzureOpenAiChatModel chatModel,
      @NonNull final ChatMemoryStore repository) {
    return AiServices.builder(LangChainChatService.class)
        .chatLanguageModel(chatModel)
        .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
            .id(memoryId)
            .chatMemoryStore(repository)
            .maxMessages(HISTORY_MAX_MESSAGES)
            .build()
        )
        .build();
  }

  @Bean
  public AzureOpenAiChatModel lcOpenAiChatModel(
      @NonNull final OpenAIClientConfiguration config) {
    final var chatConfig = config.getChat();

    final var builder = AzureOpenAiChatModel.builder()
        .apiKey(config.getKey())
        .endpoint(config.getEndpoint())
        .deploymentName(chatConfig.getModelName())

        .logRequestsAndResponses(true);

    Optional.ofNullable(chatConfig.getTemperature()).ifPresent(builder::temperature);
    Optional.ofNullable(chatConfig.getTopP()).ifPresent(builder::topP);
    Optional.ofNullable(chatConfig.getPresencePenalty()).ifPresent(builder::presencePenalty);
    Optional.ofNullable(chatConfig.getFrequencyPenalty()).ifPresent(builder::frequencyPenalty);
    Optional.ofNullable(chatConfig.getMaxTokens()).ifPresent(builder::maxTokens);

    return builder.build();
  }

}
