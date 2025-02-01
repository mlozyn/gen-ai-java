package com.ml.training.gen.ai.client;

import com.ml.training.gen.ai.utils.JsonUtils;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatCompletionV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatPromptV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatV1;
import com.ml.training.gen.ai.web.rest.chat.model.v1.ChatV1Request;
import com.ml.training.gen.ai.web.rest.chat.model.v1.PromptSettingsV1;
import com.ml.training.gen.ai.web.rest.common.model.v1.ClientTypeV1;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ChatApiClient extends ApiClientBase {

  public static void main(String[] args) {
    final ChatApiClient client = new ChatApiClient(URI.create("http://localhost:8080"));

    final StopWatch stopWatch = StopWatch.createStarted();

    //weatherForecastChat(client);

    pizzaChat(client);
    //assistantChat(client);

    System.out.println(String.format("Execution completed. Took: %d ms",
        stopWatch.getTime(TimeUnit.MILLISECONDS)));
  }

  private static void weatherForecastChat(final ChatApiClient client) {
    final var chat = Dialog.builder()
        .withTitle("weather forecast")
        .withUserMessages(List.of(
            "What is the weather forecast for tomorrow in London, UK ?"
        ))
        .build();

    executeDialog(chat, ClientTypeV1.SK_OPEN_AI, client, null);
  }

  private static void pizzaChat(final ChatApiClient client) {
    final var chat = Dialog.builder()
        .withTitle("pizza chat")
        .withSystemMessage(
            "You are a helpful AI assistant for an Italian pizzeria.")
        .withUserMessages(List.of(
            "Hello, my name is Klaus and I am hungry! I need a pizza!!!",
            "Prefer two medium sized pizzas.",
            "Let's add cheese and mushrooms toppings.",
            "I realized that medium sized pizzas won't be enough. Please replace them with large sized pizzas.",
            "Please show what is in my cart now ?",
            "Please add one medium sized pizza with all available toppings but double cheese to my cart.",
            "Let's proceed with checkout"
        ))
        .build();

    executeDialog(chat, ClientTypeV1.SK_OPEN_AI, client, null);
  }

  private static void assistantChat(final ChatApiClient client) {
    final var chat = Dialog.builder()
        .withTitle("assistant chat")
        .withSystemMessage(
            "You are a helpful assistant trained to answer a variety of questions. Please provide concise, informative, and accurate responses to the best of your ability. Use up to 5 concise sentences to answer the question.")
        .withUserMessages(List.of(
            "Can you tell me about the lost city of Atlantis? Where might it be located?",
            "What is the best way to train a pet dragon?"
        ))
        .build();

    final List<PromptSettingsV1> settingsList = List.of(
        PromptSettingsV1.builder()
            .withTemperature(0.3)
            .withTopP(0.3)
            .build(),
        PromptSettingsV1.builder()
            .withTemperature(0.5)
            .withTopP(0.5)
            .build(),
        PromptSettingsV1.builder()
            .withTemperature(0.8)
            .withTopP(1.0)
            .build()
    );

    for (final var settings : settingsList) {
      System.out.println("-- Execution settings: " + settings);
      executeDialog(chat, ClientTypeV1.SK_OPEN_AI, client, settings);
//    executeDialog(chat, ClientTypeV1.LC_OPEN_AI_PROMPT, client, null);
    }
  }

  private static void executeDialog(final Dialog dialog, final ClientTypeV1 clientType,
      final ChatApiClient client, final PromptSettingsV1 settings) {
    System.out.println(String.format(" ===== %s =====", clientType));
    client.executeDialog(dialog, clientType, settings);
    System.out.println(" =====");
  }

  public ChatApiClient(final URI baseUrl) {
    super(baseUrl);
  }

  private void executeDialog(final Dialog dialog, final ClientTypeV1 clientType,
      final PromptSettingsV1 settings) {
    final Long chatId = createChat(dialog.getTitle(), clientType, dialog.getSystemMessage());
    System.out.printf("--- chat '%s' created \n", dialog.getTitle());

    for (final String message : dialog.getUserMessages()) {
      System.out.println("user: " + message);
      System.out.println("ai  : " + ask(chatId, message, settings) + "\n");
    }

    deleteChat(chatId);
    System.out.printf("--- chat '%s' deleted \n", dialog.getTitle());
  }

  public Long createChat(final String name, final ClientTypeV1 clientType,
      final String systemMessage) {
    final var body = new ChatV1Request();
    body.setName(name);
    body.setClientType(clientType);
    body.setSystemMessage(systemMessage);

    final var request = HttpRequest.newBuilder(getBaseUrl().resolve("/chat/v1"))
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(body), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(1))

        .build();

    final var result = execute(request, ChatV1.class);
    return result.getId();
  }

  public void deleteChat(final Long chatId) {
    final var request = HttpRequest.newBuilder(getBaseUrl().resolve("/chat/v1/" + chatId))
        .DELETE()
        .timeout(Duration.ofSeconds(1))

        .build();

    execute(request, String.class);
  }

  public String ask(final Long chatId, final String message, final PromptSettingsV1 settings) {
    final var prompt = new ChatPromptV1();
    prompt.setInput(message);
    prompt.setSettings(settings);

    final var request = HttpRequest.newBuilder(getBaseUrl().resolve("/chat/v1/" + chatId + "/ask"))
        .POST(BodyPublishers.ofString(JsonUtils.toJsonString(prompt), StandardCharsets.UTF_8))

        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)

        .timeout(Duration.ofSeconds(60))

        .build();

    final var body = execute(request, ChatCompletionV1.class);
    return body.getOutput();
  }

  @Getter
  @Builder(setterPrefix = "with")
  private static class Dialog {

    private String title;
    private String systemMessage;

    private List<String> userMessages;
  }

}
