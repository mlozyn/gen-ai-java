package com.ml.training.gen.ai.service.prompt.impl.sk.plugin;

import com.ml.training.gen.ai.service.domain.pizza.PizzaService;
import com.ml.training.gen.ai.service.domain.pizza.model.Cart;
import com.ml.training.gen.ai.service.domain.pizza.model.Checkout;
import com.ml.training.gen.ai.service.domain.pizza.model.Menu;
import com.ml.training.gen.ai.service.domain.pizza.model.Pizza;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaSize;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaTopping;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class SemanticKernelOrderPizzaPlugin {

  private final PizzaService pizzaService;

  private final UUID cartId = UUID.randomUUID();

  public SemanticKernelOrderPizzaPlugin(@NonNull final PizzaService pizzaService) {
    this.pizzaService = pizzaService;
  }

  @DefineKernelFunction(
      name = "get_pizza_menu",
      description = "Get the pizza menu.",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Menu"
  )
  public Menu getPizzaMenu() {
    return pizzaService.getMenu();
  }

  @DefineKernelFunction(
      name = "add_pizza_to_cart",
      description = "Add a pizza to the user's cart. If the user selects multiple toppings, add the first topping when adding the pizza to the cart, and then add the remaining toppings one by one using the pizzaId.",
      returnDescription = "Returns the new item and updated cart",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Pizza")
  public Pizza addPizzaToCart(
      @KernelFunctionParameter(name = "size", description = "The size of the pizza", type = PizzaSize.class)
      @NonNull final PizzaSize size,
      @KernelFunctionParameter(name = "topping", description = "The topping to add to the the pizza", type = PizzaTopping.class)
      @NonNull final PizzaTopping topping,
      @KernelFunctionParameter(name = "quantity", description = "How many of this pizza to order", type = int.class, defaultValue = "1")
      int quantity,
      // be careful with default values for string ... could change behaviour dramatically
      // try to set here empty string ...
      @KernelFunctionParameter(name = "specialInstructions", description = "Special instructions for the order", required = false)
      @Nullable final String specialInstructions
  ) {
    return pizzaService.addPizzaToCart(
        cartId,
        size,
        new LinkedList<>(List.of(topping)),
        quantity,
        KernelFunctionParameter.NO_DEFAULT_VALUE.equalsIgnoreCase(specialInstructions) ? null : specialInstructions
    );
  }

  // Kernel function arguments can't be complex type like array or collection.
  // This method serves as a workaround to enable support for multiple toppings

  @DefineKernelFunction(
      name = "add_pizza_topping",
      description = "Add topping to user's pizza",
      returnDescription = "Returns the new item and updated cart",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Pizza")
  public Pizza addPizzaTopping(
      @KernelFunctionParameter(name = "pizzaId", description = "Id of the pizza to add the topping", type = Long.class)
      @NonNull final Long pizzaId,
      @KernelFunctionParameter(name = "topping", description = "The topping to add to the the pizza", type = PizzaTopping.class)
      @NonNull final PizzaTopping topping
  ) {
    final var pizza = pizzaService.getPizzaFromCart(cartId, pizzaId);
    final var toppings = Stream.concat(pizza.getToppings().stream(), Stream.of(topping))
        .toList();

    return pizzaService.updatePizzaToppings(
        cartId,
        pizzaId,
        toppings
    );
  }

  @DefineKernelFunction(
      name = "remove_pizza_from_cart",
      description = "Remove user's pizza from cart",
      returnDescription = "Returns the new item and updated cart",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Pizza")
  public Pizza removePizzaFromCart(
      @KernelFunctionParameter(name = "pizzaId", description = "Id of the pizza to add the topping", type = Long.class)
      @NonNull final Long pizzaId) {
    return pizzaService.removePizzaFromCart(cartId, pizzaId);
  }

  @DefineKernelFunction(
      name = "get_pizza_from_cart",
      description = "Returns the specific details of a pizza in the user's cart; use this instead of relying on previous messages since the cart may have changed since then.",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Pizza")
  public Pizza getPizzaFromCart(
      @KernelFunctionParameter(name = "pizzaId", description = "Id of the pizza to get from the cart", type = Integer.class, required = true) final Long pizzaId) {
    return pizzaService.getPizzaFromCart(cartId, pizzaId);
  }

  @DefineKernelFunction(
      name = "get_cart",
      description = "Returns the user's current cart, including the total price and items in the cart.",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Cart")
  public Cart getCart() {
    return pizzaService.getCart(cartId);
  }

  @DefineKernelFunction(
      name = "checkout",
      description = "Checkouts the user's cart; this function will retrieve the payment from the user and complete the order.",
      returnType = "com.ml.training.gen.ai.service.domain.pizza.model.Checkout")
  public Checkout checkout() {
    return pizzaService.checkout(cartId);
  }

}
