package com.ml.training.gen.ai.service.prompt.tool;

import com.ml.training.gen.ai.service.domain.pizza.PizzaService;
import com.ml.training.gen.ai.service.domain.pizza.model.Cart;
import com.ml.training.gen.ai.service.domain.pizza.model.Checkout;
import com.ml.training.gen.ai.service.domain.pizza.model.Menu;
import com.ml.training.gen.ai.service.domain.pizza.model.Pizza;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaSize;
import com.ml.training.gen.ai.service.domain.pizza.model.PizzaTopping;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.NonNull;

/*

  Below example demonstrate the power of LangChain4j comparing to Semantic Kernel
  A lot of simplifications, automated types resolution, complex types support ...

 */

public class LangChainOrderPizzaTools {

  private final UUID cartId = UUID.randomUUID();

  private final PizzaService pizzaService;

  public LangChainOrderPizzaTools(final PizzaService pizzaService) {
    this.pizzaService = pizzaService;
  }

  @Tool("Get the pizza menu.")
  public Menu getPizzaMenu() {
    return pizzaService.getMenu();
  }

  @Tool("Add a pizza to the user's cart.")
  public Pizza addPizzaToCart(
      @P("The size of the pizza.")
      @NonNull final PizzaSize size,
      @P("The toppings to add to the the pizza")
      @NonNull final List<PizzaTopping> toppings,
      @P("How many of this pizza to order")
      int quantity,
      @P(value = "Special instructions for the order", required = false) final String specialInstructions
  ) {
    return pizzaService.addPizzaToCart(
        cartId,
        size,
        toppings,
        quantity,
        specialInstructions
    );
  }

  @Tool("Remove user's pizza from cart")
  public Pizza removePizzaFromCart(
      @P("Id of the pizza to add the topping")
      @NonNull final Long pizzaId) {
    return pizzaService.removePizzaFromCart(cartId, pizzaId);
  }

  @Tool("Returns the specific details of a pizza in the user's cart; use this instead of relying on previous messages since the cart may have changed since then.")
  public Pizza getPizzaFromCart(
      @P("Id of the pizza to get from the cart")
      @NonNull final Long pizzaId) {
    return pizzaService.getPizzaFromCart(cartId, pizzaId);
  }

  @Tool("Returns the user's current cart, including the total price and items in the cart.")
  public Cart getCart() {
    return pizzaService.getCart(cartId);
  }

  @Tool("Checkouts the user's cart; this function will retrieve the payment from the user and complete the order.")
  public Checkout checkout() {
    return pizzaService.checkout(cartId);
  }

}
