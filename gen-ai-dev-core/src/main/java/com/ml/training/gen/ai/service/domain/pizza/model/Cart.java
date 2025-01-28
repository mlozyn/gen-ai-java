package com.ml.training.gen.ai.service.domain.pizza.model;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NonNull;

@Data
public class Cart {

  private UUID id;
  private List<Pizza> items;

  public Cart() {
    this.items = new LinkedList<>();
  }

  public Cart(@NonNull final UUID id) {
    this();
    this.id = id;
  }

}
