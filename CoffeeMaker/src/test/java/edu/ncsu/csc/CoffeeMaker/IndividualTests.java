package edu.ncsu.csc.CoffeeMaker;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

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
 * Testing my individual implementations.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class IndividualTests {

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
	 * Tests editing a Recipe using PUT.
	 * 
	 * @throws Exception status
	 */
	@Test
	@Transactional
	public void testEditRecipe() throws Exception {
		service.deleteAll();

		final Recipe r = new Recipe();
		Ingredient chocolate = new Ingredient("Chocolate", 2);
		Ingredient coffee = new Ingredient("Coffee", 4);
		r.addIngredient(coffee);
		r.addIngredient(chocolate);
		r.setPrice(10);
		r.setName("Mocha");

		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(r))).andExpect(status().isOk());

		mvc.perform(get("/api/v1/recipes")).andExpect(status().isOk()).andReturn();

		Ingredient milk = new Ingredient("Milk", 2);

		Recipe mocha = service.findByName("Mocha");
		mocha.setPrice(12); // Set new price

		Ingredient fetchedChocolate = mocha.getIngredient("Chocolate");
		mocha.removeIngredient(fetchedChocolate);
		mocha.addIngredient(milk);

		Ingredient fetchedCoffee = mocha.getIngredient("Coffee");
		Ingredient updatedCoffee = mocha.getIngredient("Coffee");

		// Get existing values
		Integer mochaPrice = mocha.getPrice();
		Assertions.assertEquals(12, mochaPrice);
		int coffeeQuantity = updatedCoffee.getQuantity();
		Assertions.assertEquals(4, coffeeQuantity);

		updatedCoffee.setQuantity(3);
		mocha.removeIngredient(fetchedCoffee);
		mocha.addIngredient(updatedCoffee);

		mvc.perform(put("/api/v1/recipes/Mocha").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(mocha))).andExpect(status().isOk());

		mvc.perform(get("/api/v1/recipes")).andExpect(status().isOk()).andReturn();

		final Recipe lemonade = new Recipe();
		Ingredient lemon = new Ingredient("Lemon", 2);
		Ingredient sugar = new Ingredient("Sugar", 4);
		lemonade.addIngredient(lemon);
		lemonade.addIngredient(sugar);
		lemonade.setPrice(15);
		lemonade.setName("Lemonade");

		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(lemonade))).andExpect(status().isOk());

		// List of all Recipe objects in system
		List<Recipe> recipes = service.findAll();
	}

	/**
	 * Tests editing a nonexistent Recipe using PUT.
	 * 
	 * @throws Exception status
	 */
	@Test
	@Transactional
	public void testEditInvalidRecipe() throws Exception {
		service.deleteAll();

		final Recipe r = new Recipe();
		Ingredient chocolate = new Ingredient("Chocolate", 2);
		Ingredient coffee = new Ingredient("Coffee", 4);
		r.addIngredient(coffee);
		r.addIngredient(chocolate);
		r.setPrice(10);
		r.setName("Mocha");

		mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(r))).andExpect(status().isOk());

		mvc.perform(get("/api/v1/recipes")).andExpect(status().isOk()).andReturn();

		Ingredient milk = new Ingredient("Milk", 2);

		Recipe mocha = service.findByName("Mocha");
		mocha.setPrice(12);

		Ingredient fetchedChocolate = mocha.getIngredient("Chocolate");
		mocha.removeIngredient(fetchedChocolate);
		mocha.addIngredient(milk);

		Ingredient fetchedCoffee = mocha.getIngredient("Coffee");
		Ingredient updatedCoffee = mocha.getIngredient("Coffee");
		updatedCoffee.setQuantity(3);
		mocha.removeIngredient(fetchedCoffee);
		mocha.addIngredient(updatedCoffee);

		mvc.perform(put("/api/v1/recipes/Lemonade").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(mocha))).andExpect(status().isNotFound());
	}
}
