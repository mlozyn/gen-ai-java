package com.ml.training.gen.ai.config;

import com.ml.training.gen.ai.service.embedding.repository.EmbeddingQdrantRepository;
import com.ml.training.gen.ai.service.embedding.repository.mapping.PointStructMapper;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
@ConditionalOnProperty(prefix = "database.qdrant", name = "enabled", havingValue = "true")
public class QdrantRepositoryConfiguration {

  @Autowired
  private PointStructMapper pointStructMapper;

  @Bean
  public EmbeddingQdrantRepository qdrantRepository(@NonNull final QdrantDbConfiguration config,
      @NonNull final QdrantClient client) {
    return new EmbeddingQdrantRepository(
        "embeddings",
        config.getDimensions(),
        client,
        pointStructMapper
    );
  }

  @Bean
  public QdrantClient qdrantClient(@NonNull final QdrantDbConfiguration config) {
    return new QdrantClient(
        QdrantGrpcClient.newBuilder(config.getHost(), config.getPort(), false)
            .build()
    );
  }

  @Bean
  @ConfigurationProperties("database.qdrant")
  public QdrantDbConfiguration qdrantDbConfiguration() {
    return new QdrantDbConfiguration();
  }

  @Data
  public static class QdrantDbConfiguration {

    private String host;
    private Integer port;

    private Integer dimensions;

  }

}
