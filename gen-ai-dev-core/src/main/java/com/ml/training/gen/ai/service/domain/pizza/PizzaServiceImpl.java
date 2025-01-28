package com.ml.training.gen.ai.service.domain.pizza;

import com.ml.training.gen.ai.service.domain.pizza.model.Cart;
import com.ml.training.gen.ai.service.domain.pizza.model.Checkout;
import com.ml.training.gen.ai.service.domain.pizza.model.Menu;
import com.ml.training.gen.ai.service.domain.pizza.model.Pizza;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaSize;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaTopping;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PizzaServiceImpl implements PizzaService {

  private static final Logger LOG = LoggerFactory.getLogger(PizzaServiceImpl.class);

  private static final Menu MENU = Menu.builder()
      .withToppings(EnumSet.allOf(PizzaTopping.class))
      .build();

  private final AtomicLong idGenerator = new AtomicLong();
  private final Map<UUID, Cart> carts = new ConcurrentHashMap<>();

  @Override
  public Menu getMenu() {
    LOG.info("Getting pizza menu");
    return PizzaServiceImpl.MENU;
  }

  @Override
  public Cart getCart(@NonNull final UUID cartId) {
    return Optional.ofNullable(carts.get(cartId))
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Cart with id '%s' does not exist", cartId))
        );
  }

  @Override
  public Pizza addPizzaToCart(@NonNull final UUID cartId, @NonNull final PizzaSize size,
      @NonNull final List<PizzaTopping> toppings, int quantity, final String specialInstructions) {
    LOG.info("[{}] Adding pizza to cart. Size: {}, toppings: {}, quantity: {}", cartId,
        size, toppings, quantity);

    final var pizza = Pizza.builder()
        .withId(idGenerator.incrementAndGet())

        .withQuantity(quantity)

        .withSize(size)
        .withToppings(toppings)
        .withSpecialInstructions(specialInstructions)

        .build();

    final var cart = getOrCreate(cartId);
    cart.getItems().add(pizza);

    return pizza;
  }

  @Override
  public Pizza updatePizzaToppings(@NonNull final UUID cartId, @NonNull final Long pizzaId,
      @NonNull final List<PizzaTopping> toppings) {
    LOG.info("[{}] Updating pizza's toppings. Pizza id: {}, toppings: {}", cartId,
        pizzaId, toppings);

    final var pizza = getPizzaFromCart(cartId, pizzaId);
    pizza.setToppings(toppings);

    return pizza;
  }

  @Override
  public Pizza removePizzaFromCart(@NonNull final UUID cartId,
      @NonNull final Long pizzaId) {
    LOG.info("[{}] Removing pizza from cart. Pizza id: {}", cartId, pizzaId);

    final Cart cart = getCart(cartId);
    final Pizza pizza = getPizzaFromCart(cart, pizzaId);

    cart.getItems().remove(pizza);
    return pizza;
  }

  @Override
  public Pizza getPizzaFromCart(@NonNull final UUID cartId, @NonNull final Long pizzaId) {
    LOG.info("[{}] Getting pizza from cart. Pizza id: {}", cartId, pizzaId);

    final Cart cart = getCart(cartId);
    return getPizzaFromCart(cart, pizzaId);
  }

  public Checkout checkout(@NonNull final UUID cartId) {
    LOG.info("[{}] Starting checkout process", cartId);

    final Cart cart = getCart(cartId);
    LOG.info("[{}] Cart items: {}", cartId, cart);

    return Checkout.builder()
        .withId(cartId.toString())
        .withCart(cart)
        .build();
  }

  private Pizza getPizzaFromCart(@NonNull final Cart cart, @NonNull final Long pizzaId) {
    return cart.getItems().stream()
        .filter(pizza -> pizza.getId().equals(pizzaId))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException(
            String.format("Pizza with id '%s' does not exist in cart '%s'", pizzaId, cart.getId()))
        );
  }

  private Cart getOrCreate(final UUID cartId) {
    return carts.computeIfAbsent(cartId, Cart::new);
  }

}
