package com.ml.training.gen.ai.web.rest;

import com.ml.training.gen.ai.utils.JsonUtils;
import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import com.ml.training.gen.ai.web.rest.sentiment.model.v1.SentimentCompletionV1;
import com.ml.training.gen.ai.web.rest.sentiment.model.v1.SentimentPromptV1;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class SentimentApiClient extends ApiClientBase {

  public static void main(String[] args) {
    final SentimentApiClient client = new SentimentApiClient(URI.create("http://localhost:8080"));

    final StopWatch stopWatch = StopWatch.createStarted();

    sentimentAnalysis(client, ClientTypeV1.LC_OPEN_AI);

    System.out.println(String.format("Execution completed. Took: %d ms",
        stopWatch.getTime(TimeUnit.MILLISECONDS)));
  }

  public SentimentApiClient(final URI baseUrl) {
    super(baseUrl);
  }

  private static void sentimentAnalysis(final SentimentApiClient client,
      final ClientTypeV1 clientType) {
    final var documents = List.of(
        "The pizza arrived hot and fresh, and the toppings were generously spread. Great service overall!",
        "The internet speed is consistently fast, and I’ve experienced zero downtime. Fantastic service!",
        "The installation process was smooth, and the technician was knowledgeable and helpful. Wonderful experience!",

        "Internet outages have been frequent, and customer support is unresponsive. I’m extremely frustrated.",
        "Delivery was delayed, and the customer service agent was rude when I called for assistance.",
        "The mechanic quickly fixed my car issue, but it doesn't run now"
    );

    System.out.println("===== " + clientType);

    for (final String document : documents) {
      final var result = client.getSentiment(document, clientType);
      System.out.println(document + " ---> " + result.getOutput());
    }
  }

  public SentimentCompletionV1 getSentiment(final String message, final ClientTypeV1 clientType) {
    final var prompt = new SentimentPromptV1();
    prompt.setInput(message);
    prompt.setClientType(clientType);

    final var request = HttpRequest.newBuilder(getBaseUrl().resolve("/sentiment/v1"))
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(prompt), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(60))

        .build();

    return execute(request, SentimentCompletionV1.class);
  }

}
