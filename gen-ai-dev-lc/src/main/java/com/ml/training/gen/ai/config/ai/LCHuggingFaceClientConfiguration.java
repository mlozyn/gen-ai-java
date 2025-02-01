package com.ml.training.gen.ai.config.ai;

import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.embedding.EmbeddingServiceImpl;
import com.ml.training.gen.ai.service.embedding.LangChainEmbeddingFunction;
import com.ml.training.gen.ai.service.embedding.mapping.TextEntityMapper;
import com.ml.training.gen.ai.service.embedding.repository.EmbeddingRepository;
import com.ml.training.gen.ai.service.prompt.LangChainPromptServiceL;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class LCHuggingFaceClientConfiguration {

  private static final ClientType CLIENT_TYPE = ClientType.LC_HF;

  @Autowired
  private EmbeddingRepository embeddingRepository;

  @Bean
  public EmbeddingService lcHFEmbeddingService(
      @NonNull final TextEntityMapper mapper,
      @NonNull @Qualifier("lcHFEmbeddingFunction") final LangChainEmbeddingFunction embeddingFunction) {
    return new EmbeddingServiceImpl(embeddingRepository, mapper, embeddingFunction);
  }

  @Bean
  public LangChainPromptServiceL lcHFPromptService(@NonNull final HuggingFaceChatModel chatModel,
      @NonNull final ChatMemoryStore repository) {
    return new LangChainPromptServiceL(
        CLIENT_TYPE,
        chatModel, repository
    );
  }

  @Bean
  public LangChainEmbeddingFunction lcHFEmbeddingFunction(
      @NonNull final HuggingFaceEmbeddingModel embeddingModel) {
    return new LangChainEmbeddingFunction(
        CLIENT_TYPE,
        embeddingModel
    );
  }

  @Bean
  public HuggingFaceEmbeddingModel lsHFEmbeddingModel(
      @NonNull final HuggingFaceClientConfiguration config) {
    final var embeddingConfig = config.getEmbedding();

    final var builder = HuggingFaceEmbeddingModel.builder()
        .accessToken(config.getKey())
        .modelId(embeddingConfig.getModelName());

    return builder.build();
  }

  @Bean
  public HuggingFaceChatModel lsHFChatModel(
      @NonNull final HuggingFaceClientConfiguration config) {
    final var chatConfig = config.getChat();

    final var builder = HuggingFaceChatModel.builder()
        .accessToken(config.getKey())
        .modelId(chatConfig.getModelName())

        // some models are pretty slow ...
        .timeout(Duration.ofSeconds(60))
        .waitForModel(true);

    Optional.ofNullable(chatConfig.getTemperature()).ifPresent(builder::temperature);
    Optional.ofNullable(chatConfig.getMaxTokens()).ifPresent(builder::maxNewTokens);
    Optional.ofNullable(chatConfig.getReturnFullText()).ifPresent(builder::returnFullText);

    return builder.build();
  }

}
