package com.ml.training.gen.ai.service.domain.pizza.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {

  private Long id;

  @Builder.Default
  private int quantity = 1;

  private PizzaSize size;
  private List<PizzaTopping> toppings;

  private String specialInstructions;

}
