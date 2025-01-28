package com.ml.training.gen.ai.service.domain.pizza.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

  private Set<PizzaTopping> toppings;

}
