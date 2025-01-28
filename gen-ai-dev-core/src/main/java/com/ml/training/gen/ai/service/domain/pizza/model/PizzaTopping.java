package com.ml.training.gen.ai.service.domain.pizza.model;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum PizzaTopping {

  CHEESE("Cheese"),
  PEPPERONI("Pepperoni"),
  MUSHROOMS("Mushrooms");

  private static final Logger LOG = LoggerFactory.getLogger(PizzaTopping.class);

  private final String displayName;

  PizzaTopping(final String displayName) {
    this.displayName = displayName;
  }

  public static PizzaTopping forDisplayName(final String source) {
    LOG.info("Converting pizza topping display value '{}'", source);
    for (final PizzaTopping topping : PizzaTopping.values()) {
      if (topping.getDisplayName().equalsIgnoreCase(source)) {
        return topping;
      }
    }

    throw new IllegalArgumentException(
        String.format("Pizza topping for name '%s' is not supported", source));
  }

  public static PizzaTopping forObject(final Object obj) {
    if (obj instanceof PizzaTopping topping) {
      LOG.info("Converting pizza topping");
      return topping;
    }

    return PizzaTopping.forDisplayName(obj.toString());
  }

}
