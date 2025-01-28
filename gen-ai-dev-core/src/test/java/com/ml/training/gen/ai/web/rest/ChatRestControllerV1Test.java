package com.ml.training.gen.ai.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.ml.training.gen.ai.config.ApplicationTestConfiguration;
import com.ml.training.gen.ai.service.chat.model.Chat;
import com.ml.training.gen.ai.service.common.model.ClientType;
import com.ml.training.gen.ai.service.prompt.PromptService;
import com.ml.training.gen.ai.service.prompt.model.PromptSettings;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {ApplicationTestConfiguration.class}
)
class ChatRestControllerV1Test extends AbstractRestControllerTest {

  private static final String TEST_CASES_PATH = "data/chat-controller-v1-tests.json";

  @MockBean
  private PromptService promptServiceMock;

  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    when(promptServiceMock.supports(any())).thenReturn(false);
    when(promptServiceMock.supports(eq(ClientType.LC_OPEN_AI))).thenReturn(true);

    when(promptServiceMock.ask(any(Chat.class), eq("simple question"), any(PromptSettings.class)))
        .thenReturn("simple answer");
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

}