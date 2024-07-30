package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests making coffee via POST requests
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

	/**
	 * Local MockMvc for POST, PUT, GET, DELETE requests.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * Local RecipeService.
	 */
	@Autowired
	private RecipeService service;

	/**
	 * Local InventoryService.
	 */
	@Autowired
	private InventoryService iService;

	/**
	 * Sets up the tests.
	 */
	@BeforeEach
	public void setup() {

		final Inventory ivt = iService.getInventory();

		ivt.addIngredient(new Ingredient("Chocolate", 15));
		ivt.addIngredient(new Ingredient("Coffee", 15));
		ivt.addIngredient(new Ingredient("Milk", 15));
		ivt.addIngredient(new Ingredient("Sugar", 15));

		iService.save(ivt);

		final Recipe recipe = new Recipe();
		recipe.setName("Coffee");
		recipe.setPrice(50);
		recipe.addIngredient(new Ingredient("Coffee", 3));
		recipe.addIngredient(new Ingredient("Milk", 1));
		recipe.addIngredient(new Ingredient("Sugar", 1));
		recipe.addIngredient(new Ingredient("Chocolate", 1));
		service.save(recipe);
	}

	/**
	 * Valid purchase.
	 * 
	 * @throws Exception from POST
	 */
	@Test
	@Transactional
	public void testPurchaseBeverage1() throws Exception {

		final String name = "Coffee";

		mvc.perform(post(String.format("/api/v1/makecoffee/%s", name))
				.contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(60)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.message").value(10));

		Assertions.assertNotNull(name);
	}

	/**
	 * Insufficient amount paid.
	 * 
	 * @throws Exception from POST
	 */
	@Test
	@Transactional
	public void testPurchaseBeverage2() throws Exception {
		final String name = "Coffee";

		mvc.perform(post(String.format("/api/v1/makecoffee/%s", name))
				.contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(40)))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message").value("Not enough money paid"));

		Assertions.assertNotNull(name);
	}

	/**
	 * Insufficient Inventory.
	 * 
	 * @throws Exception from POST
	 */
	@Test
	@Transactional
	public void testPurchaseBeverage3() throws Exception {
		final Inventory ivt = iService.getInventory();
		ivt.setIngredient("Coffee", 1);
		iService.save(ivt);

		final String name = "Coffee";

		mvc.perform(post(String.format("/api/v1/makecoffee/%s", name))
				.contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(50)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.message")
						.value("Not enough Inventory or Ingredient not found in Inventory"));

		Assertions.assertNotNull(name);
	}

	/**
	 * Non-existent Recipe.
	 * 
	 * @throws Exception from POST
	 */
	@Test
	@Transactional
	public void testPurchaseBeverage4() throws Exception {
		final Inventory ivt = iService.getInventory();
		ivt.setIngredient("Coffee", 1);
		iService.save(ivt);

		final String name = "Covfefe";

		mvc.perform(post(String.format("/api/v1/makecoffee/%s", name))
				.contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(50)))
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.message").value("Recipe does not exist"));

		Assertions.assertNotNull(name);
	}

	/**
	 * Non-existent Ingredient Recipe.
	 * 
	 * @throws Exception from POST
	 */
	@Test
	@Transactional
	public void testPurchaseBeverage5() throws Exception {
		final Inventory ivt = iService.getInventory();
		ivt.removeIngredient("Coffee");
		iService.save(ivt);

		final String name = "Coffee";

		mvc.perform(post(String.format("/api/v1/makecoffee/%s", name))
				.contentType(MediaType.APPLICATION_JSON).content(TestUtils.asJsonString(50)))
				.andExpect(status().is4xxClientError()).andExpect(jsonPath("$.message")
						.value("Not enough Inventory or Ingredient not found in Inventory"));

		Assertions.assertNotNull(name);
	}
}
