package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests APIRecipeController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class APIRecipeTest {

	/**
	 * Local RecipeService.
	 */
	@Autowired
	private RecipeService service;

	/**
	 * Local MockMvc for POST, PUT, GET, DELETE requests.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * Ensures that POST requesting a new Recipe to the API does not throw an exception.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void ensureRecipeValidCreation() throws Exception {
		service.deleteAll();

		final Recipe r = new Recipe();
		Ingredient chocolate = new Ingredient("Chocolate", 2);
		Ingredient coffee = new Ingredient("Coffee", 4);
		r.addIngredient(coffee);
		r.addIngredient(chocolate);
		r.setPrice(10);
		r.setName("Mocha");
		Assertions.assertNotNull(r.getName());

		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(r))).andExpect(status().isOk());
	}

	/**
	 * Tests that adding a Recipe to the service properly increases the number of Recipes.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void testRecipeAPI() throws Exception {
		service.deleteAll();

		final Recipe recipe = new Recipe();
		recipe.setName("Thick mocha");
		Ingredient chocolate = new Ingredient("Chocolate", 8);
		Ingredient coffee = new Ingredient("Coffee", 4);
		recipe.addIngredient(coffee);
		recipe.addIngredient(chocolate);
		recipe.setPrice(5);

		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(recipe)));

		Assertions.assertEquals(1, (int) service.count());
	}

	/**
	 * Tests that adding a Recipe with a duplicate name to the service is properly handled.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void testAddRecipe2() throws Exception {
		service.deleteAll();
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");

		final Recipe r1 = new Recipe();
		Ingredient caramel = new Ingredient("Caramel", 3);
		Ingredient milk = new Ingredient("Milk", 4);
		Ingredient coffee = new Ingredient("Coffee", 5);
		r1.addIngredient(coffee);
		r1.addIngredient(milk);
		r1.addIngredient(caramel);
		r1.setName("Caramel latte");
		service.save(r1);

		final Recipe r2 = new Recipe();
		r2.setName("Caramel latte");
		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(r2))).andExpect(status().is4xxClientError());

		Assertions.assertEquals(1, service.findAll().size(),
				"There should only one Recipe in the CoffeeMaker");
	}

	/**
	 * Tests that the cap of 3 Recipes is enforced.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void testAddRecipe15() throws Exception {
		service.deleteAll();
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");

		final Recipe r1 = new Recipe();
		r1.setName("Black coffee");
		service.save(r1);

		final Recipe r2 = new Recipe();
		r2.setName("Caramel coffee");
		service.save(r2);

		final Recipe r3 = new Recipe();
		r3.setName("Mocha");
		service.save(r3);

		Assertions.assertEquals(3, service.count(),
				"Creating three Recipes should result in three Recipes in the database");

		final Recipe r4 = new Recipe();
		r4.setName("Hot chocolate");

		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(r4))).andExpect(status().isInsufficientStorage());

		Assertions.assertEquals(3, service.count(),
				"Creating a fourth Recipe should not get saved");
	}

	/**
	 * Tests getting a valid Recipe in the database.
	 * 
	 * @throws Exception if GET fails
	 */
	@Test
	@Transactional
	public void testRecipesGetAPI() throws Exception {
		service.deleteAll();

		final Recipe r1 = new Recipe();
		r1.setName("Black coffee");
		service.save(r1);

		try {
			String newRecipe = mvc.perform(get("/api/v1/recipes/Black coffee"))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			Assertions.assertTrue(newRecipe.contains("black coffee"));
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}

	/**
	 * Tests getting an invalid Recipe in the database.
	 * 
	 * @throws Exception if GET fails
	 */
	@Test
	@Transactional
	public void testRecipesGetInvalidAPI() throws Exception {
		service.deleteAll();

		final Recipe r1 = new Recipe();
		r1.setName("Green coffee");
		service.save(r1);

		String newRecipe = mvc.perform(get("/api/v1/recipes/Black coffee"))
				.andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

		Assertions.assertNotNull(newRecipe);
	}

	/**
	 * Tests getting all Recipes in the database.
	 * 
	 * @throws Exception if GET fails
	 */
	@Test
	@Transactional
	public void testRecipesGetAllAPI() throws Exception {
		service.deleteAll();

		final Recipe r1 = new Recipe();
		r1.setName("Black coffee");
		service.save(r1);

		final Recipe r2 = new Recipe();
		r2.setName("Caramel coffee");
		service.save(r2);

		final Recipe r3 = new Recipe();
		r3.setName("Mocha");
		service.save(r3);

		mvc.perform(get("/api/v1/recipes")).andExpect(status().isOk()).andReturn();

		Assertions.assertEquals(3, (int) service.count());
	}

	/**
	 * Tests deleting a Recipe using DELETE.
	 */
	@Test
	@Transactional
	public void testDeleteRecipe() {
		service.deleteAll(); // Clean slate
		final Recipe r1 = new Recipe();
		r1.setName("Coffee");
		service.save(r1);

		try {
			mvc.perform(delete("/api/v1/recipes/Coffee")).andExpect(status().isOk());
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Deleting a created Recipe returned bad status");
		}

		try {
			mvc.perform(delete("/api/v1/recipes/Coffee")).andExpect(status().isNotFound());
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail("Deleting a non-existent Recipe returned unexpected status");
		}
	}
}
