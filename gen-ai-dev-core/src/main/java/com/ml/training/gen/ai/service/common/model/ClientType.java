package com.ml.training.gen.ai.service.common.model;

import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public enum ClientType {

  SK_OPEN_AI(10, "Semantic Kernel Azure Open AI"),

  LC_OPEN_AI(20, "Lang Chain Azure Open AI"),
  LC_HF(21, "Lang Chain Hugging Face");

  private final int value;
  private final String title;

  ClientType(@NonNull final int value, @NonNull final String title) {
    this.value = value;
    this.title = title;
  }

  public static ClientType forValue(final Integer value) {
    for (final var clientType : values()) {
      if (clientType.getValue() == value) {
        return clientType;
      }
    }

    throw new IllegalArgumentException(String.format("Client type '%s' is not supported", value));
  }

}
