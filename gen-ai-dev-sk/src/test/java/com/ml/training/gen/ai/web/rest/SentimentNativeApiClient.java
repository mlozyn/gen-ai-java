package com.ml.training.gen.ai.web.rest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;

// neither the Semantic Kernel nor the LangChain4j frameworks supports sentiment analysis out of the box
// you have to implement client on your own
public class SentimentNativeApiClient {

  public static void main(String[] args) throws Exception {
    final var messages = List.of(
        "The pizza arrived hot and fresh, and the toppings were generously spread. Great service overall!",
        "The internet speed is consistently fast, and I’ve experienced zero downtime. Fantastic service!",
        "The installation process was smooth, and the technician was knowledgeable and helpful. Wonderful experience!",

        "Internet outages have been frequent, and customer support is unresponsive. I’m extremely frustrated.",
        "Delivery was delayed, and the customer service agent was rude when I called for assistance.",
        "The mechanic quickly fixed my car issue, but it doesn't run now"
    );

    for (final String message : messages) {
      final var result = getSentimentHF(message);
      System.out.println(message + ": " + result);
    }

    System.out.println("=== Done");
  }

  private static String getSentimentHF(final String text) throws Exception {
    //final var modelName = "finiteautomata/bertweet-base-sentiment-analysis";
    final var modelName = "cardiffnlp/twitter-roberta-base-sentiment-latest";

    final var apiKey = System.getenv("HF_API_KEY");

    final var payload = String.format("{\"inputs\": \"%s\"}", text);

    try (final HttpClient client = HttpClient.newBuilder().build()) {
      final var request = HttpRequest.newBuilder()
          .uri(URI.create("https://api-inference.huggingface.co/models/" + modelName))
          .header("Authorization", "Bearer " + apiKey)
          .header("Content-Type", "application/json")
          .POST(BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
          .build();

      final var response = client.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
      return response.body();
    }

  }

  /*
    Output:

    The pizza arrived hot and fresh, and the toppings were generously spread. Great service overall!: [[{"label":"positive","score":0.9840046167373657},{"label":"neutral","score":0.01260200422257185},{"label":"negative","score":0.0033933331724256277}]]
    The internet speed is consistently fast, and I’ve experienced zero downtime. Fantastic service!: [[{"label":"positive","score":0.9856697916984558},{"label":"neutral","score":0.010603110305964947},{"label":"negative","score":0.0037270321045070887}]]
    The installation process was smooth, and the technician was knowledgeable and helpful. Wonderful experience!: [[{"label":"positive","score":0.9830896854400635},{"label":"neutral","score":0.012438462115824223},{"label":"negative","score":0.004471813794225454}]]
    Internet outages have been frequent, and customer support is unresponsive. I’m extremely frustrated.: [[{"label":"negative","score":0.9462187886238098},{"label":"neutral","score":0.04762733727693558},{"label":"positive","score":0.00615393090993166}]]
    Delivery was delayed, and the customer service agent was rude when I called for assistance.: [[{"label":"negative","score":0.9247249364852905},{"label":"neutral","score":0.06968334317207336},{"label":"positive","score":0.005591731518507004}]]
    The mechanic quickly fixed my car issue, but it doesn't run now: [[{"label":"neutral","score":0.5235767960548401},{"label":"negative","score":0.42088595032691956},{"label":"positive","score":0.05553724244236946}]]

    ML: The output is almost correct, except for the last sentence, which I find to be more negative than neutral ...

   */

}
