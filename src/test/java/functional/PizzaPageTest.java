package functional;

import com.despegar.sparkjava.test.SparkServer;
import config.DatabaseConfig;
import org.hamcrest.Matchers;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V10;

public class PizzaPageTest {

    private WebDriver driver;
    private static EmbeddedPostgres postgres;

    @BeforeClass
    public static void beforeAll() throws InterruptedException, IOException {
        postgres = new EmbeddedPostgres(V10);
        DatabaseConfig pizzaTestConfig = TestPizzaApplication.getPizzaTestConfig();
        String url = postgres.start(pizzaTestConfig.getHost(), Integer.parseInt(pizzaTestConfig.getPort()), pizzaTestConfig.getDatabaseName(), pizzaTestConfig.getUser(), pizzaTestConfig.getPassword());

        ProcessBuilder pb = new ProcessBuilder("./db-scripts/migrate.sh");
        Map<String, String> env = pb.environment();
        env.put("PGHOST", pizzaTestConfig.getHost());
        env.put("PGPORT", pizzaTestConfig.getPort());
        env.put("PGUSER", pizzaTestConfig.getUser());
        env.put("PGDATABASE", pizzaTestConfig.getDatabaseName());
        env.put("PGPW", pizzaTestConfig.getPassword());

        File directory = new File(".");
        pb.directory(directory);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process p = pb.start();
        p.waitFor();

    }

    @After
    public void tearDown() throws Exception {
        driver.close();
    }

    @AfterClass
    public static void afterAll() {
        postgres.stop();
    }

    @ClassRule
    public static SparkServer<TestPizzaApplication> testServer = new SparkServer<>(TestPizzaApplication.class, 4568);


    @Test
    @Ignore("We are still progressing story #14")
    public void shouldBeAbleToNavigateToIndividualPizzaPage() {
        System.setProperty("webdriver.gecko.driver", "./lib/geckodriver");
        driver = new HtmlUnitDriver();
        driver.get("http://localhost:4568");
        List<WebElement> pizzaElements = driver.findElements(By.className("pizza"));
        assertThat(pizzaElements.size(), Matchers.is(4));
        assertThat(driver.getCurrentUrl(), is("http://localhost:4568/"));

        WebElement firstPizza = pizzaElements.get(0);
        firstPizza.findElement(By.linkText("Veggie")).click();

        assertThat(driver.getCurrentUrl(), is("http://localhost:4568/pizza/veggie"));


    }


}
