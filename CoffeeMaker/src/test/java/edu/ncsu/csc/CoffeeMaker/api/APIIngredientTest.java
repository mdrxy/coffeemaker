package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * Tests various Ingredient functionalities made possible in APIIngredientController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class APIIngredientTest {

	/**
	 * Local IngredientService.
	 */
	@Autowired
	private IngredientService ingService;

	/**
	 * Local MockMvc for POST, PUT, GET, DELETE requests.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * Format string to POSTING new Ingredients as decimal quantity.
	 */
	final String formatStringSD = "/api/v1/ingredients/%s?quantity=%d";

	/**
	 * Format string to POSTING new Ingredients as String quantity.
	 */
	final String formatStringSS = "/api/v1/ingredients/%s?quantity=%s";

	/**
	 * Ensures that POST requesting a new Ingredient to the API does not throw an exception.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void ensureIngredient() throws Exception {
		ingService.deleteAll();
		final Ingredient ingredient = new Ingredient("Caramel", 5);
		Assertions.assertNotNull(ingredient.getName());

		// Construct the URL with the name and quantity parameters
		mvc.perform(
				post(String.format(formatStringSD, ingredient.getName(), ingredient.getQuantity()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.asJsonString(ingredient)))
				.andExpect(status().isOk());
	}

	/**
	 * Tests that adding an Ingredient to the ingService via POST works and properly increases the
	 * number of Ingredients.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void testIngredientAPI() throws Exception {
		ingService.deleteAll();
		Assertions.assertEquals(0, (int) ingService.findAll().size(),
				"Database should be empty, and was not");
		final Ingredient ingredient1 = new Ingredient("Marshmallow cream", 2);
		final Ingredient ingredient2 = new Ingredient("Almond milk", 4);

		mvc.perform(post(
				String.format(formatStringSD, ingredient1.getName(), ingredient1.getQuantity()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(ingredient1))).andExpect(status().isOk());
		Assertions.assertEquals(1, (int) ingService.count());

		mvc.perform(post(
				String.format(formatStringSD, ingredient2.getName(), ingredient2.getQuantity()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(ingredient2))).andExpect(status().isOk());
		Assertions.assertEquals(2, (int) ingService.count());

		final String invalidQuantity = "two";
		mvc.perform(post(String.format(formatStringSS, ingredient2.getName(), invalidQuantity))
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(ingredient2))).andExpect(status().isBadRequest());
		Assertions.assertEquals(2, (int) ingService.count());
	}

	/**
	 * Tests deleting an Ingredient using DELETE.
	 * 
	 * @throws Exception if DELETE fails
	 */
	@Test
	@Transactional
	public void testDeleteRecipe() throws Exception {
		ingService.deleteAll();
		final Ingredient ingredient = new Ingredient("Liquid sugar", 3);
		ingService.save(ingredient);

		mvc.perform(delete("/api/v1/ingredients/Liquid sugar")).andExpect(status().isOk());
		mvc.perform(delete("/api/v1/ingredients/Liquid sugar")).andExpect(status().isNotFound());
	}

	/**
	 * Tests getting a valid Ingredient.
	 * 
	 * @throws Exception if GET fails
	 */
	@Test
	@Transactional
	public void testGetIngredient() throws Exception {
		final Ingredient ingredient = new Ingredient("Sugar", 3);
		ingService.save(ingredient);

		try {
			String newIngr = mvc.perform(get("/api/v1/ingredients/sugar"))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			Assertions.assertTrue(newIngr.contains("sugar successfully retrieved"));
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}

	/**
	 * Tests getting all Ingredients in the database.
	 * 
	 * @throws Exception if POST or GET fails
	 */
	@Test
	@Transactional
	public void testIngredientGetAllAPI() throws Exception {
		ingService.deleteAll();
		Assertions.assertEquals(0, (int) ingService.findAll().size(),
				"Database should be empty, and was not");
		final Ingredient ingredient1 = new Ingredient("Marshmallow cream", 2);
		final Ingredient ingredient2 = new Ingredient("Almond milk", 4);

		mvc.perform(post(
				String.format(formatStringSD, ingredient1.getName(), ingredient1.getQuantity()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(ingredient1))).andExpect(status().isOk());
		mvc.perform(post(
				String.format(formatStringSD, ingredient2.getName(), ingredient2.getQuantity()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(ingredient2))).andExpect(status().isOk());

		mvc.perform(get("/api/v1/ingredients")).andExpect(status().isOk()).andReturn();

		Assertions.assertEquals(2, (int) ingService.count());
	}

	/**
	 * Tests getting an invalid Ingredient.
	 * 
	 * @throws Exception if GET fails
	 */
	@Test
	@Transactional
	public void testGetIngredientFail() throws Exception {
		try {
			String newIngr = mvc.perform(get("/api/v1/ingredients/sugar"))
					.andExpect(status().isNotFound()).andReturn().getResponse()
					.getContentAsString();
			Assertions.assertNotNull(newIngr);
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}
}
