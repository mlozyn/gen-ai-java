package com.ml.training.gen.ai.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("client.azure-openai")
@Data
public class OpenAIClientConfiguration {

  private String key;
  private String endpoint;

  private OpenAIChatModelConfiguration chat;
  private OpenAIEmbeddingModelConfiguration embedding;

  @Data
  public static class OpenAIChatModelConfiguration {

    private String modelName;

    // model configs
    private Double temperature;
    private Double topP;
    private Double frequencyPenalty;
    private Double presencePenalty;
    private Integer maxTokens;

  }

  @Data
  public static class OpenAIEmbeddingModelConfiguration {

    private String modelName;
    private Integer dimensions;

  }

}
