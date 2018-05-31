package integration;

import dal.PizzaService;
import dal.dao.PizzaDAO;
import functional.helpers.DatabaseSetupRule;
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

        List<PizzaDAO> allPizzaDAOS = pizzaService.getAllPizzaDaos();
        assertThat(allPizzaDAOS.size(), is(4));

        PizzaDAO veggiePizzaDAO = getPizzaByName(allPizzaDAOS, "Veggie");
        PizzaDAO pepperoniPizzaDAO = getPizzaByName(allPizzaDAOS, "Pepperoni feast");

        assertThat(veggiePizzaDAO.getName(), is("Veggie"));
        assertThat(veggiePizzaDAO.getSlug(), is("veggie"));
        assertThat(veggiePizzaDAO.getIngredients(), is("Pizza sauce, vegan alternative to cheese, spinach, sweetcorn, mixed peppers, red onion, mushrooms"));
        assertThat(veggiePizzaDAO.getPrice(), is(12.99F));

        assertThat(pepperoniPizzaDAO.getName(), is("Pepperoni feast"));
        assertThat(pepperoniPizzaDAO.getSlug(), is("pepperoni-feast"));
        assertThat(pepperoniPizzaDAO.getIngredients(), is("Pizza sauce, mozzarella cheese, pepperoni"));
        assertThat(pepperoniPizzaDAO.getPrice(), is(13.99F));
    }

    @Test
    public void shouldRRetrievePizzaBySlug() {
        PizzaDAO veggie = pizzaService.getPizzaBySlug("veggie").get();
        assertThat((veggie.getName()), is("Veggie"));

    }

    private PizzaDAO getPizzaByName(List<PizzaDAO> allPizzaDAOS, String pizzaName) {
        return allPizzaDAOS.stream()
                .filter(pizza -> pizza.getName().equals(pizzaName))
                .findFirst().get();
    }


    @Test
    public void shouldReturnEmptyOptionalIfNoPizzaWithSlugCanBeFound(){
        Optional<PizzaDAO> optional = pizzaService.getPizzaBySlug("pizzadoesnotexist");
        assertFalse(optional.isPresent());
    }
}