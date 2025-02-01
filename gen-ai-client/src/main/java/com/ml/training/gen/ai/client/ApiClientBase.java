package com.ml.training.gen.ai.client;

import com.ml.training.gen.ai.utils.JsonUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

public class ApiClientBase {

  @Getter
  private final URI baseUrl;
  private final HttpClient httpClient;

  protected ApiClientBase(final URI baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = createHttpClient();
  }

  @SuppressWarnings("unchecked")
  protected <T> T execute(final HttpRequest request, final Class<T> responseType) {
    final HttpResponse<String> response;
    try {
      response = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
    } catch (final Exception exception) {
      throw new RuntimeException(exception);
    }

    if (!HttpStatusCode.valueOf(response.statusCode()).is2xxSuccessful()) {
      throw new RuntimeException(response.body());
    }

    if (responseType.isAssignableFrom(String.class)) {
      return (T) response.body();
    }

    return JsonUtils.fromJsonString(response.body(), responseType);
  }

  private HttpClient createHttpClient() {
    return HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(1))
        .build();
  }

}
