package com.ml.training.gen.ai.web.rest;

import com.ml.training.gen.ai.utils.JsonUtils;
import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.ScoredTextV1;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.SimilarityEmbeddingV1Request;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.SimilarityEmbeddingV1Response;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.SimilarityScoreV1Request;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.SimilarityScoreV1Response;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.SimilaritySearchV1Request;
import com.ml.training.gen.ai.web.rest.embedding.model.v1.SimilaritySearchV1Response;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class SimilarityApiClient extends ApiClientBase {

  public static void main(String[] args) {
    final SimilarityApiClient client = new SimilarityApiClient(URI.create("http://localhost:8080"));

    final StopWatch stopWatch = StopWatch.createStarted();

//    scoreAnalysis(client);
//    embeddings(client, true);
    search(client);

    System.out.println(String.format("Execution completed. Took: %d ms",
        stopWatch.getTime(TimeUnit.MILLISECONDS)));
  }

  private static void search(final SimilarityApiClient client) {
    search(client, "animals with wings", null);
    search(client, "what do you know about pets?", 0.3f);
    search(client, "fox jumps over dog", null);
    search(client, "What is the role of AI in transforming industries?", 0.6f);
  }

  private static void search(final SimilarityApiClient client, final String query,
      final Float scoreThreshold) {
    System.out.println("===== Search: " + query);
    final var result = client.search(query, scoreThreshold, ClientTypeV1.SK_OPEN_AI);
    for (final ScoredTextV1 document : result.getData()) {
      System.out.println(String.format("- %.3f: %s", document.getScore(), document.getText()));
    }
  }

  private static void embeddings(final SimilarityApiClient client, final boolean persist) {
    embeddings(client, persist, ClientTypeV1.SK_OPEN_AI);
//    embeddings(client, persist, ClientTypeV1.LC_OPEN_AI);
  }

  private static void embeddings(final SimilarityApiClient client, final boolean persist,
      final ClientTypeV1 clientType) {

    final List<String> documents = Arrays.asList(
        "The human form consists of multiple systems working together to maintain balance and function.",
        "The body operates through a network of organs, muscles, and nerves to perform vital processes.",
        "The human physique is a marvel of biological engineering, designed for adaptability and survival.",

        "The flight from Kyiv to Berlin lasted approximately 2 hours.",
        "Birds are feathered creatures, uniquely adapted for flight with lightweight skeletons.",
        "These avian species exhibit remarkable diversity, ranging from songbirds to raptors, each suited to their habitats and lifestyles.",

        "The quick brown fox jumps over the lazy dog.",
        "A swift auburn fox leapt across a sleepy canine.",
        "The lazy dog was jumped over by a quick fox.",
        "An energetic rabbit hopped past a tired hound.",
        "Cats are independent creatures often loved for their elegance.",
        "Artificial intelligence is transforming the way we work and live.",
        "Machine learning is a subset of AI focused on data-driven models.",
        "The rise of quantum computing will redefine modern technology.",
        "Gardening requires patience, dedication, and a love for plants.",
        "Cooking delicious meals at home can be a therapeutic activity."
    );

    System.out.println("===== Embeddings. Client: " + clientType);
    for (final String document : documents) {
      final var result = client.embed(document, persist, clientType);
      System.out.println("result: " + JsonUtils.toJsonString(result));
    }
  }

  private static void scoreAnalysis(final SimilarityApiClient client) {
    scoreAnalysis(client, ClientTypeV1.SK_OPEN_AI);
    scoreAnalysis(client, ClientTypeV1.LC_OPEN_AI);
    scoreAnalysis(client, ClientTypeV1.LC_HF);
  }

  private static void scoreAnalysis(final SimilarityApiClient client,
      final ClientTypeV1 clientType) {
//    final List<String> documents = Arrays.asList(
//        "Amazon provides cloud computing services.",
//        "Machine learning models like Titan help in NLP tasks.",
//        "AWS Lambda is a serverless computing service."
//    );
//
//    final String query = "What services does Google offer?";

    final String query = "Which sentences are about animals with wings?";

    final List<String> documents = Arrays.asList(
        "The human form consists of multiple systems working together to maintain balance and function.",
        "The body operates through a network of organs, muscles, and nerves to perform vital processes.",
        "The human physique is a marvel of biological engineering, designed for adaptability and survival.",

        "The flight from Kyiv to Berlin lasted approximately 2 hours.",
        "Birds are feathered creatures, uniquely adapted for flight with lightweight skeletons.",
        "These avian species exhibit remarkable diversity, ranging from songbirds to raptors, each suited to their habitats and lifestyles."
    );

    System.out.println("===== Score Analysis. Client: " + clientType);
    final var result = client.score(query, documents, clientType);

    System.out.println("query: " + result.getQuery());
    System.out.println("documents: ");

    final var scoredDocuments = result.getDocuments();
    for (int index = 0; index < scoredDocuments.size(); index++) {
      final var scoredDocument = scoredDocuments.get(index);
      System.out.printf("Rank %d: %s (Score: %.4f)%n", index + 1,
          scoredDocument.getText(), scoredDocument.getScore());
    }
  }

  /*

  ===== LC_OPEN_AI
  query: Which sentences are about animals with wings?

  documents:
  Rank 1: Birds are feathered creatures, uniquely adapted for flight with lightweight skeletons. (Score: 0.6908)
  Rank 2: These avian species exhibit remarkable diversity, ranging from songbirds to raptors, each suited to their habitats and lifestyles. (Score: 0.6502)
  Rank 3: The human physique is a marvel of biological engineering, designed for adaptability and survival. (Score: 0.5364)
  Rank 4: The human form consists of multiple systems working together to maintain balance and function. (Score: 0.5316)
  Rank 5: The body operates through a network of organs, muscles, and nerves to perform vital processes. (Score: 0.5041)

  */

  public SimilarityApiClient(final URI baseUrl) {
    super(baseUrl);
  }

  public SimilarityEmbeddingV1Response embed(final String text, final boolean persist,
      final ClientTypeV1 clientType) {
    final var prompt = new SimilarityEmbeddingV1Request();
    prompt.setText(text);
    prompt.setClientType(clientType);

    final var request = HttpRequest.newBuilder(
            getBaseUrl().resolve("/similarity/v1/embed?persist=" + persist)
        )
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(prompt), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(60))

        .build();

    return execute(request, SimilarityEmbeddingV1Response.class);
  }

  public SimilarityScoreV1Response score(final String query, final List<String> documents,
      final ClientTypeV1 clientType) {
    final var prompt = new SimilarityScoreV1Request();
    prompt.setQuery(query);
    prompt.setDocuments(documents);
    prompt.setClientType(clientType);

    final var request = HttpRequest.newBuilder(getBaseUrl().resolve("/similarity/v1/score"))
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(prompt), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(60))

        .build();

    return execute(request, SimilarityScoreV1Response.class);
  }

  public SimilaritySearchV1Response search(final String query, final Float scoreThreshold,
      final ClientTypeV1 clientType) {
    final var prompt = new SimilaritySearchV1Request();
    prompt.setLimit(5);
    prompt.setScoreThreshold(scoreThreshold);
    prompt.setQuery(query);
    prompt.setClientType(clientType);

    final var request = HttpRequest.newBuilder(
            getBaseUrl().resolve("/similarity/v1/search")
        )
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(prompt), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(60))

        .build();

    return execute(request, SimilaritySearchV1Response.class);
  }

}
