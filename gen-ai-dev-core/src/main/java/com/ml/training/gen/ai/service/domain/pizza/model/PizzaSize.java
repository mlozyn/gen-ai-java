package com.ml.training.gen.ai.service.domain.pizza.model;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum PizzaSize {

  SMALL("Small"),
  MEDIUM("Medium"),
  LARGE("Large");

  private static final Logger LOG = LoggerFactory.getLogger(PizzaSize.class);

  private final String displayName;

  PizzaSize(final String displayName) {
    this.displayName = displayName;
  }

  public static PizzaSize forDisplayName(final String source) {
    LOG.info("Converting pizza size display value '{}'", source);

    for (final PizzaSize size : PizzaSize.values()) {
      if (size.getDisplayName().equalsIgnoreCase(source)) {
        return size;
      }
    }

    throw new IllegalArgumentException(
        String.format("Pizza size for name '%s' is not supported", source));
  }

  public static PizzaSize forObject(final Object obj) {
    if (obj instanceof PizzaSize size) {
      LOG.info("Converting pizza size type");
      return size;
    }

    return PizzaSize.forDisplayName(obj.toString());
  }

}
