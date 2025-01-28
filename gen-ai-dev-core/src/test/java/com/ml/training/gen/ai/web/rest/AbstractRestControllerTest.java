package com.ml.training.gen.ai.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.ml.training.gen.ai.utils.JsonUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


abstract class AbstractRestControllerTest {

  private static final ParameterizedTypeReference<JsonNode> JSON_NODE_TYPE_REFERENCE =
      new ParameterizedTypeReference<>() {
      };

  protected abstract TestRestTemplate getRestTemplate();

  protected void endpointTest(final String testName, final JsonNode given, final JsonNode expected,
      final Set<String> ignoreProperties) {
    final ResponseEntity<JsonNode> actual = executeRequest(given);

    assertEquals(expected.get("status").asInt(), actual.getStatusCode().value(),
        String.format("[%s] response status code. Body: '%s'", testName,
            JsonUtils.toJsonString(actual.getBody())));

    final JsonNode expectedBody = expected.path("body");
    if (JsonUtils.isNull(expectedBody)) {
      assertNull(actual.getBody(), String.format("[%s] response body", testName));
    } else {
      Assertions.assertTrue(
          JsonUtils.equals(expectedBody, actual.getBody(), ignoreProperties),
          String.format("[%s] response body. \nExpected: '%s' \nActual: '%s'", testName,
              JsonUtils.toJsonString(expectedBody),
              JsonUtils.toJsonString(actual.getBody())));
    }
  }

  @SuppressWarnings("unchecked")
  protected ResponseEntity<JsonNode> executeRequest(final JsonNode given) {
    final JsonNode request = given.get("request");
    return executeRequest(
        request.get("endpoint").asText(),
        request.get("method").asText(),
        (Map<String, String>) JsonUtils.OBJECT_MAPPER
            .convertValue(request.get("headers"), Map.class),
        request.path("body")
    );
  }

  private ResponseEntity<JsonNode> executeRequest(
      final String endpointUri, final String method,
      final Map<String, String> headers, final JsonNode body) {

    return getRestTemplate().exchange(endpointUri, HttpMethod.valueOf(method),
        createHttpEntity(headers, body), JSON_NODE_TYPE_REFERENCE);
  }

  private HttpEntity<JsonNode> createHttpEntity(final Map<String, String> customHeaders,
      final JsonNode body) {
    final HttpHeaders headers = new HttpHeaders();
    // default header
    headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
    // custom headers
    if (Objects.nonNull(customHeaders)) {
      customHeaders.forEach(headers::add);
    }

    if (JsonUtils.isNull(body)) {
      return new HttpEntity<>(null, headers);
    }

    return new HttpEntity<>(body, headers);
  }

  static Stream<Arguments> loadClassPassTestCases(final String resourceFilePath) {
    final List<JsonNode> testCases = JsonUtils.loadClassPathJson(resourceFilePath,
        new TypeReference<>() {
        });

    return testCases.stream()
        .map(testCase -> Arguments.of(
            testCase.path("name").asText(),
            testCase.path("given"),
            testCase.path("expected"))
        );
  }

}