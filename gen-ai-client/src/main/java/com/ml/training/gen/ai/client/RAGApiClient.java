package com.ml.training.gen.ai.client;

import com.ml.training.gen.ai.utils.JsonUtils;
import com.ml.training.gen.ai.web.rest.rag.model.v1.RAGCompletionV1;
import com.ml.training.gen.ai.web.rest.rag.model.v1.RAGPromptV1;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class RAGApiClient extends ApiClientBase {

  public static void main(String[] args) {
    final RAGApiClient client = new RAGApiClient(URI.create("http://localhost:8080"));

    final StopWatch stopWatch = StopWatch.createStarted();

//  1. upload documents (main/resources/rag/data)

//  2. ask question
//    final RAGPromptV1 prompt = new RAGPromptV1();
//    prompt.setQuestion(
//        "Please provide me with the role description for the Chief Technology Officer");
//
//    final RAGCompletionV1 completion = client.ask(prompt);
//
//    System.out.println("==== Question");
//    System.out.println(prompt.getQuestion());
//
//    System.out.println("");
//
//    System.out.println("==== Answer");
//    System.out.println(completion.getAnswer());
//
//    stopWatch.stop();
//    System.out.println(String.format("Execution completed. Took: %d ms",
//        stopWatch.getTime(TimeUnit.MILLISECONDS)));
  }


  public RAGApiClient(final URI baseUrl) {
    super(baseUrl);
  }

  public RAGCompletionV1 ask(final RAGPromptV1 prompt) {
    final URI destUri = getBaseUrl().resolve("/rag/v1/ask");
    final var request = HttpRequest.newBuilder(destUri)
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(prompt), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(60))

        .build();

    return execute(request, RAGCompletionV1.class);
  }

}
