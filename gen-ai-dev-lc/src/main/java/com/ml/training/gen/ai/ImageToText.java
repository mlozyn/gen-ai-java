package com.ml.training.gen.ai;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.azure.AzureOpenAiImageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.output.Response;
import java.time.Duration;
import java.util.Objects;

public class ImageToText {

  public static void main(String[] args) {
    final var apiKey = System.getenv("AZURE_OPEN_AI_KEY");
    final var endpoint = System.getenv("AZURE_OPEN_AI_ENDPOINT");

    final ImageModel imagenModel = AzureOpenAiImageModel.builder()
        .apiKey(apiKey)
        .endpoint(endpoint)
        .deploymentName("imagegeneration@005")

        .timeout(Duration.ofSeconds(120))
        .maxRetries(2)

        .logRequestsAndResponses(true)

        .build();

    Response<Image> response = imagenModel.generate(
        "watercolor of a colorful parrot drinking a cup of coffee");

    final var image = response.content();
    if (Objects.nonNull(image)) {
      System.out.println(response.content().base64Data());
    }
  }

}
