package com.ml.training.gen.ai.service.domain.pizza;

import com.ml.training.gen.ai.service.domain.pizza.model.Cart;
import com.ml.training.gen.ai.service.domain.pizza.model.Checkout;
import com.ml.training.gen.ai.service.domain.pizza.model.Menu;
import com.ml.training.gen.ai.service.domain.pizza.model.Pizza;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaSize;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaTopping;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface PizzaService {

  Menu getMenu();

  Cart getCart(@NonNull final UUID cartId);

  Pizza getPizzaFromCart(@NonNull final UUID cartId, @NonNull final Long pizzaId);

  Pizza addPizzaToCart(@NonNull final UUID cartId, @NonNull final PizzaSize size,
      @NonNull final List<PizzaTopping> toppings, int quantity, final String specialInstructions);

  Pizza updatePizzaToppings(@NonNull final UUID cartId, @NonNull final Long pizzaId,
      @NonNull final List<PizzaTopping> toppings);

  Pizza removePizzaFromCart(@NonNull final UUID cartId, @NonNull final Long pizzaId);

  Checkout checkout(@NonNull final UUID cartId);

}
