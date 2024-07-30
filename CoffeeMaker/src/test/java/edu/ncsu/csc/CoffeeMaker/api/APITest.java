package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

/**
 * Tests the API endpoint
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {

	/**
	 * MockMvc uses Spring's testing framework to handle requests to the REST API
	 */
	private MockMvc mvc;

	/**
	 * Spring WebAppContext.
	 * 
	 * https://stackoverflow.com/questions/11708967/what-is-the-difference-between-applicationcontext-and-webapplicationcontext-in-s
	 */
	@Autowired
	private WebApplicationContext context;

	/**
	 * Sets up the tests.
	 */
	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	/**
	 * Tests the API endpoint.
	 */
	@Test
	@Transactional
	public void endPointTest() {
		try {
			String recipe = mvc.perform(get("/api/v1/recipes")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			if (!recipe.contains("Mocha")) {
				Recipe r = new Recipe();
				r.setName("Mocha");
				r.setPrice(725);
				mvc.perform(post("/api/v1/inventory/coffee")).andDo(print())
						.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
				r.addIngredient(new Ingredient("Coffee", 1));
				mvc.perform(post("/api/v1/inventory/milk")).andDo(print())
						.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
				r.addIngredient(new Ingredient("Milk", 1));
				mvc.perform(post("/api/v1/inventory/sugar")).andDo(print())
						.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
				r.addIngredient(new Ingredient("Sugar", 3));
				mvc.perform(post("/api/v1/inventory/chocolate")).andDo(print())
						.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
				r.addIngredient(new Ingredient("Chocolate", 2));

				mvc.perform(post("/api/v1/recipes").contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.asJsonString(r))).andExpect(status().isOk());
			}
			String recipeNew = mvc.perform(get("/api/v1/recipes")).andDo(print())
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

			Assertions.assertTrue(recipeNew.contains("mocha"));

			// Adding Inventory
			Inventory inventoryUpdate = new Inventory();
			inventoryUpdate.addIngredient(new Ingredient("Coffee", 50));
			inventoryUpdate.addIngredient(new Ingredient("Milk", 50));
			inventoryUpdate.addIngredient(new Ingredient("Sugar", 50));
			inventoryUpdate.addIngredient(new Ingredient("Chocolate", 50));

			mvc.perform(put("/api/v1/inventory").contentType(MediaType.APPLICATION_JSON)
					.content(TestUtils.asJsonString(inventoryUpdate))).andExpect(status().isOk());

			// Making coffee
			mvc.perform(post("/api/v1/makecoffee/Mocha").contentType(MediaType.APPLICATION_JSON)
					.content(TestUtils.asJsonString("1000"))).andExpect(status().isOk());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}
}
