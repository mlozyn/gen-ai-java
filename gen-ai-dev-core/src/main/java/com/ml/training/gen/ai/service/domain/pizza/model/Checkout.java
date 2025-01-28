package com.ml.training.gen.ai.service.domain.pizza.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class Checkout {

  private String id;
  private Cart cart;

}
