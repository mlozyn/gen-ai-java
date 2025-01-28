package com.ml.training.gen.ai.config.ai;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.aiservices.openai.textembedding.OpenAITextEmbeddingGenerationService;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypeConverter;
import com.microsoft.semantickernel.contextvariables.ContextVariableTypes;
import com.microsoft.semantickernel.implementation.EmbeddedResourceLoader;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.HandlebarsPromptTemplateFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionYaml;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.textembedding.TextEmbeddingGenerationService;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.domain.pizza.model.Cart;
import com.ml.training.gen.ai.service.domain.pizza.model.Checkout;
import com.ml.training.gen.ai.service.domain.pizza.model.Pizza;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaSize;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaTopping;
import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.embedding.EmbeddingServiceImpl;
import com.ml.training.gen.ai.service.embedding.SematicKernelEmbeddingFunction;
import com.ml.training.gen.ai.service.embedding.mapping.TextEntityMapper;
import com.ml.training.gen.ai.service.embedding.repository.EmbeddingRepository;
import com.ml.training.gen.ai.service.prompt.SemanticKernelPromptService;
import com.ml.training.gen.ai.service.prompt.impl.sk.mapping.PromptSettingsMapper;
import com.ml.training.gen.ai.service.prompt.impl.sk.plugin.SemanticKernelOrderPizzaPlugin;
import com.ml.training.gen.ai.service.prompt.impl.sk.plugin.SemanticKernelWeatherPlugin;
import com.ml.training.gen.ai.service.prompt.impl.sk.repository.ChatHistoryRepository;
import com.ml.training.gen.ai.service.rag.content.RAGContentService;
import com.ml.training.gen.ai.service.rag.content.RAGFusionContentService;
import com.ml.training.gen.ai.service.rag.indexer.DocumentIndexer;
import com.ml.training.gen.ai.service.rag.indexer.SemanticKernelDocumentIndexer;
import com.ml.training.gen.ai.service.rag.prompt.SemanticKernelRAGPromptService;
import com.ml.training.gen.ai.utils.JsonUtils;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

@Configuration
public class SKOpenAIConfiguration {

  private static final ClientType CLIENT_TYPE = ClientType.SK_OPEN_AI;

  @Autowired
  private EmbeddingRepository embeddingRepository;

  @Bean
  public SemanticKernelRAGPromptService skOpenAIRAGPromptService(
      @NonNull @Qualifier("skOpenAIRAGContentService") final RAGContentService contentService,
      @NonNull final Kernel kernel) {
    return new SemanticKernelRAGPromptService(
        CLIENT_TYPE,
        contentService, kernel
    );
  }

  @Bean
  public RAGContentService skOpenAIRAGContentService(
      @NonNull @Qualifier("skOpenAIEmbeddingService") final EmbeddingService embeddingService) {
    return new RAGFusionContentService(embeddingService);
  }

  @Bean
  public DocumentIndexer skOpenAIDocumentIndexer(
      @NonNull @Qualifier("skOpenAIEmbeddingService") final EmbeddingService embeddingService) {
    return new SemanticKernelDocumentIndexer(embeddingService);
  }

  @Bean
  public EmbeddingService skOpenAIEmbeddingService(
      @NonNull final TextEntityMapper mapper,
      @NonNull final SematicKernelEmbeddingFunction embeddingFunction) {
    return new EmbeddingServiceImpl(embeddingRepository, mapper, embeddingFunction);
  }

  @Bean
  public SemanticKernelPromptService skOpenAIPromptService(@NonNull final Kernel kernel,
      @NonNull final PromptExecutionSettings settings,
      @NonNull final ChatHistoryRepository repository,
      @NonNull final PromptSettingsMapper settingsMapper) {
    return new SemanticKernelPromptService(
        CLIENT_TYPE,
        kernel, settings, repository,
        settingsMapper
    );
  }

  @Bean
  public SematicKernelEmbeddingFunction skOpenAIEmbeddingFunction(@NonNull final Kernel kernel) {
    return new SematicKernelEmbeddingFunction(
        CLIENT_TYPE,
        kernel
    );
  }

  @Bean
  public Kernel kernel(@NonNull final ChatCompletionService chatCompletion,
      @NonNull final TextEmbeddingGenerationService embeddingGeneration,
      @NonNull final List<KernelPlugin> plugins) throws Exception {
    final var builder = Kernel.builder()
        .withAIService(ChatCompletionService.class, chatCompletion)
        .withAIService(TextEmbeddingGenerationService.class, embeddingGeneration);

    plugins.forEach(builder::withPlugin);

    // register templates as plugins
    builder.withPlugin(
        KernelPluginFactory.createFromFunctions(
            "RAG",
            "Various RAG related templates",
            List.of(
                KernelFunctionYaml.fromPromptYaml(
                    EmbeddedResourceLoader.readFile(
                        "rag/templates/answerQuestion.prompt.yaml",
                        RAGFusionContentService.class,
                        EmbeddedResourceLoader.ResourceLocation.CLASSPATH_ROOT
                    ),
                    new HandlebarsPromptTemplateFactory()
                ),
                KernelFunctionYaml.fromPromptYaml(
                    EmbeddedResourceLoader.readFile(
                        "rag/templates/multiQueryQuestion.prompt.yaml",
                        RAGFusionContentService.class,
                        EmbeddedResourceLoader.ResourceLocation.CLASSPATH_ROOT
                    ),
                    new HandlebarsPromptTemplateFactory()
                )
            )
        )
    );

    return builder.build();
  }

  @Bean
  public KernelPlugin skWeatherForecastPlugin(
      @NonNull final SemanticKernelWeatherPlugin weatherPlugin) {
    return KernelPluginFactory.createFromObject(
        weatherPlugin,
        "weatherForecast"
    );
  }

  @Bean
  public KernelPlugin skOrderPizzaKernelPlugin(
      @NonNull final SemanticKernelOrderPizzaPlugin orderPizzaPlugin) {
    ContextVariableTypes.addGlobalConverter(
        ContextVariableTypeConverter.builder(PizzaSize.class)
            .fromObject(PizzaSize::forObject)
            .toPromptString(PizzaSize::getDisplayName)
            .fromPromptString(PizzaSize::forDisplayName)
            .build()
    );
    ContextVariableTypes.addGlobalConverter(
        ContextVariableTypeConverter.builder(PizzaTopping.class)
            .fromObject(PizzaTopping::forObject)
            .toPromptString(PizzaTopping::getDisplayName)
            .fromPromptString(PizzaTopping::forDisplayName)
            .build()
    );

    ContextVariableTypes.addGlobalConverter(
        ContextVariableTypeConverter.builder(Cart.class)
            .toPromptString(JsonUtils::toJsonString)
            .build()
    );
    ContextVariableTypes.addGlobalConverter(
        ContextVariableTypeConverter.builder(Pizza.class)
            .toPromptString(JsonUtils::toJsonString)
            .build()
    );
    ContextVariableTypes.addGlobalConverter(
        ContextVariableTypeConverter.builder(Checkout.class)
            .toPromptString(JsonUtils::toJsonString)
            .build()
    );

    return KernelPluginFactory.createFromObject(
        orderPizzaPlugin,
        "orderPizza"
    );
  }

  @Bean
  public PromptExecutionSettings skOpenAIPromptExecutionSettings(
      @NonNull final OpenAIClientConfiguration config) {
    final var chatConfig = config.getChat();
    Assert.notNull(chatConfig, "Chat config must not be null");

    final var builder = PromptExecutionSettings.builder();

    Optional.ofNullable(chatConfig.getTemperature()).ifPresent(builder::withTemperature);
    Optional.ofNullable(chatConfig.getTopP()).ifPresent(builder::withTopP);
    Optional.ofNullable(chatConfig.getPresencePenalty()).ifPresent(builder::withPresencePenalty);
    Optional.ofNullable(chatConfig.getFrequencyPenalty()).ifPresent(builder::withFrequencyPenalty);
    Optional.ofNullable(chatConfig.getMaxTokens()).ifPresent(builder::withMaxTokens);

    return builder.build();
  }

  @Bean
  public TextEmbeddingGenerationService skOpenAIEmbeddingGeneration(
      @NonNull final OpenAIClientConfiguration config,
      @NonNull final OpenAIAsyncClient asyncClient) {
    final var embeddingConfig = config.getEmbedding();
    Assert.notNull(embeddingConfig, "Chat config must not be null");

    return OpenAITextEmbeddingGenerationService.builder()
        .withModelId(embeddingConfig.getModelName())
        .withOpenAIAsyncClient(asyncClient)
        .build();
  }

  @Bean
  public ChatCompletionService skOpenAIChatCompletion(
      @NonNull final OpenAIClientConfiguration config,
      @NonNull final OpenAIAsyncClient asyncClient) {
    final var chatConfig = config.getChat();
    Assert.notNull(chatConfig, "Chat config must not be null");

    return OpenAIChatCompletion.builder()
        .withModelId(chatConfig.getModelName())
        .withOpenAIAsyncClient(asyncClient)
        .build();
  }

  @Bean
  public OpenAIAsyncClient skOpenAIAsyncClient(@NonNull final OpenAIClientConfiguration config) {
    return new OpenAIClientBuilder()
        .credential(new AzureKeyCredential(config.getKey()))
        .endpoint(config.getEndpoint())
        .buildAsyncClient();
  }

}
