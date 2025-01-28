package com.ml.training.gen.ai.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("client.hugging-face")
@Data
public class HuggingFaceClientConfiguration {

  private String key;
  private HuggingFaceChatModelConfiguration chat;
  private HuggingFaceEmbeddingModelConfiguration embedding;

  @Data
  public static class HuggingFaceChatModelConfiguration {

    private String modelName;

    // model configs
    private Double temperature;
    private Integer maxTokens;
    private Boolean returnFullText;

  }

  @Data
  public static class HuggingFaceEmbeddingModelConfiguration {

    private String modelName;
    private Integer dimensions;

  }

}
