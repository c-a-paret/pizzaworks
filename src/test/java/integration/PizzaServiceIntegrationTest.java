package integration;

import dal.PizzaService;
import dal.dao.PizzaDAO;
import functional.helpers.DatabaseSetupRule;
import model.Pizza;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class PizzaServiceIntegrationTest {

    private PizzaService pizzaService;

    @ClassRule
    public static DatabaseSetupRule dbSetupRule = new DatabaseSetupRule();

    @Before
    public void setUp() {
        pizzaService = new PizzaService(dbSetupRule.getSql2o());
    }

    @Test
    public void shouldRetrievePizzas() {

        List<Pizza> allPizzas = pizzaService.getAllPizzas();
        assertThat(allPizzas.size(), is(4));

        Pizza veggiePizza = getPizzaByName(allPizzas, "Veggie");
        Pizza pepperoniPizza = getPizzaByName(allPizzas, "Pepperoni feast");

        assertThat(veggiePizza.getName(), is("Veggie"));
        assertThat(veggiePizza.getSlug(), is("veggie"));
        assertThat(veggiePizza.getIngredients(), is("Pizza sauce, vegan alternative to cheese, spinach, sweetcorn, mixed peppers, red onion, mushrooms"));
        assertThat(veggiePizza.getPrice(), is(12.99F));

        assertThat(pepperoniPizza.getName(), is("Pepperoni feast"));
        assertThat(pepperoniPizza.getSlug(), is("pepperoni-feast"));
        assertThat(pepperoniPizza.getIngredients(), is("Pizza sauce, mozzarella cheese, pepperoni"));
        assertThat(pepperoniPizza.getPrice(), is(13.99F));
    }

    @Test
    public void shouldRRetrievePizzaBySlug() {
        Pizza veggie = pizzaService.getPizzaBySlug("veggie").get();
        assertThat((veggie.getName()), is("Veggie"));

    }

    private Pizza getPizzaByName(List<Pizza> allPizzas, String pizzaName) {
        return allPizzas.stream()
                .filter(pizza -> pizza.getName().equals(pizzaName))
                .findFirst().get();
    }


    @Test
    public void shouldReturnEmptyOptionalIfNoPizzaWithSlugCanBeFound(){
        Optional<Pizza> optional = pizzaService.getPizzaBySlug("pizzadoesnotexist");
        assertFalse(optional.isPresent());
    }
}