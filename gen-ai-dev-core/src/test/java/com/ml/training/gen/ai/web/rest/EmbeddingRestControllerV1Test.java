package com.ml.training.gen.ai.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.ml.training.gen.ai.config.ApplicationTestConfiguration;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.embedding.EmbeddingFunction;
import com.ml.training.gen.ai.service.embedding.EmbeddingService;
import com.ml.training.gen.ai.service.embedding.EmbeddingServiceImpl;
import com.ml.training.gen.ai.service.embedding.mapping.TextEntityMapper;
import com.ml.training.gen.ai.service.embedding.model.TextVector;
import com.ml.training.gen.ai.service.embedding.repository.EmbeddingRepository;
import com.ml.training.gen.ai.service.embedding.repository.model.ScoredTextEntity;
import com.ml.training.gen.ai.service.embedding.repository.model.TextEntity;
import com.ml.training.gen.ai.web.rest.EmbeddingRestControllerV1Test.EmbeddingServiceTestConfiguration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {ApplicationTestConfiguration.class, EmbeddingServiceTestConfiguration.class}
)
public class EmbeddingRestControllerV1Test extends AbstractRestControllerTest {

  private static final String TEST_CASES_PATH = "data/embedding-controller-v1-tests.json";

  private static final Map<String, TextVector> TEXT_VECTORS = Stream.of(
      TextVector.builder()
          .withText("text1")
          .withVector(List.of(1.0f, 2.0f, 3.0f))
          .build(),
      TextVector.builder()
          .withText("text2")
          .withVector(List.of(3.0f, 2.0f, 1.0f))
          .build(),
      TextVector.builder()
          .withText("document1")
          .withVector(List.of(10.0f, 20.0f, 30.0f))
          .build(),
      TextVector.builder()
          .withText("document2")
          .withVector(List.of(5.0f, 17.0f, 20.0f))
          .build()
  ).collect(Collectors.toMap(
      TextVector::getText,
      Function.identity()
  ));

  private static final Map<String, TextEntity> TEXT_ENTITIES = TEXT_VECTORS.values().stream()
      .map(vector -> {
        final UUID uuid = switch (vector.getText()) {
          case "text1" -> UUID.fromString("36ac81d1-d88f-4449-8e81-d0e942f4217d");
          case "text2" -> UUID.fromString("956c8efa-d808-4756-9488-9e2d1cd7221d");
          default -> UUID.randomUUID();
        };

        return TextEntity.builder()
            .withId(uuid)
            .withText(vector.getText())
            .withVector(vector.getVector())
            .build();
      }).collect(Collectors.toMap(
          TextEntity::getText,
          Function.identity()
      ));

  @Autowired
  private EmbeddingFunction embeddingFunctionMock;
  @Autowired
  private EmbeddingRepository embeddingRepositoryMock;

  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    when(embeddingFunctionMock.supports(any())).thenReturn(false);
    when(embeddingFunctionMock.supports(eq(ClientType.LC_OPEN_AI))).thenReturn(true);

    when(embeddingFunctionMock.getClientType()).thenReturn(ClientType.LC_OPEN_AI);

    when(embeddingFunctionMock.embed(anyString())).thenAnswer(invocation -> {
      final String text = invocation.getArgument(0);

      return Optional.ofNullable(TEXT_VECTORS.get(text))
          .orElseThrow(() -> new IllegalArgumentException(String.format(
              "Embed for '%s' is not supported", text))
          );
    });

    when(embeddingRepositoryMock.save(any(TextEntity.class))).thenAnswer(invocation -> {
      final TextEntity entity = invocation.getArgument(0);

      return Optional.ofNullable(TEXT_ENTITIES.get(entity.getText()))
          .orElseThrow(() -> new IllegalArgumentException(String.format(
              "Text Entity for '%s' does not exist", entity.getText()))
          );
    });

    when(embeddingRepositoryMock.search(
        eq(TEXT_VECTORS.get("text1").getVector()),
        eq(5),
        eq(0.8f)
    )).thenReturn(List.of(
        ScoredTextEntity.builder()
            .withId(UUID.fromString("78ac81d1-d99f-4449-8e81-d0e942f4217d"))
            .withScore(0.81f)
            .withText("search-text1")
            .build(),
        ScoredTextEntity.builder()
            .withId(UUID.fromString("12ac44d1-d66f-1119-8e81-d0e942f4217d"))
            .withScore(0.92f)
            .withText("search-text2")
            .build()
    ));
  }

  @ParameterizedTest
  @MethodSource("endpointTestCases")
  public void endpointTest(final String testName, final JsonNode given, final JsonNode expected) {
    endpointTest(testName, given, expected, Set.of("createdAt", "timestamp"));
  }

  @Override
  protected TestRestTemplate getRestTemplate() {
    return restTemplate;
  }

  private static Stream<Arguments> endpointTestCases() {
    return loadClassPassTestCases(TEST_CASES_PATH);
  }

  @TestConfiguration
  public static class EmbeddingServiceTestConfiguration {

    @MockBean
    private EmbeddingFunction embeddingFunctionMock;
    @MockBean
    private EmbeddingRepository embeddingRepositoryMock;

    @Bean
    public EmbeddingService embeddingService(@NonNull final EmbeddingRepository repository,
        @NonNull final TextEntityMapper mapper,
        @NonNull final EmbeddingFunction embeddingFunction) {
      return new EmbeddingServiceImpl(repository, mapper, embeddingFunction);
    }

  }

}
