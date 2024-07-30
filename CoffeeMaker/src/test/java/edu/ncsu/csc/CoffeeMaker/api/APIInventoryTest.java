package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * Tests APIInventoryController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class APIInventoryTest {

	/**
	 * Local MockMvc for POST, PUT, GET, DELETE requests.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * Local InventoryService.
	 */
	@Autowired
	private InventoryService invService;

	/**
	 * Format string to POSTING new Ingredients to Inventory
	 */
	final String formatString = "/api/v1/inventory/%s";

	/**
	 * Sets up the tests.
	 */
	@BeforeEach
	public void setup() {
		final Inventory ivt = invService.getInventory();
		ivt.addIngredient(new Ingredient("Chocolate"));
		ivt.addIngredient(new Ingredient("Coffee"));
		ivt.addIngredient(new Ingredient("Milk"));
		ivt.addIngredient(new Ingredient("Sugar"));
		invService.save(ivt);
	}

	/**
	 * Tests GET requesting Inventory.
	 * 
	 * @throws Exception if GET fails
	 */
	@Test
	@Transactional
	public void testGetInventory() throws Exception {
		mvc.perform(get("/api/v1/inventory")).andExpect(status().isOk()).andReturn();

		Assertions.assertEquals(1, invService.count());
	}

	/**
	 * Tests DELETE requesting to clear Inventory.
	 * 
	 * @throws Exception if DELETE fails
	 */
	@Test
	@Transactional
	public void testClearInventory() throws Exception {
		mvc.perform(delete("/api/v1/inventory")).andExpect(status().isOk()).andReturn();

		Assertions.assertEquals(1, invService.count());
		String newIvt = mvc.perform(get("/api/v1/inventory")).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();
		Assertions.assertTrue(newIvt.contains("\"ingredients\":[]"));
	}

	/**
	 * Tests invalid GET requesting Inventory.
	 */
	@Test
	@Transactional
	public void testInvalidGetInventory() {
		try {
			String newIvt = mvc.perform(get("/api/v1/inventory")).andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();
			Assertions.assertFalse(newIvt.contains("\"Chocolooate\""));
			Assertions.assertFalse(newIvt.contains("\"Covfefe\""));
			Assertions.assertTrue(newIvt.contains("\"id\""));
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}

	/**
	 * Tests PUT requesting Inventory.
	 */
	@Test
	@Transactional
	public void testUpdateInventory() {
		Inventory ivtUpdate = new Inventory();
		ivtUpdate.addIngredient(new Ingredient("Chocolate", 1));
		ivtUpdate.addIngredient(new Ingredient("Coffee", 2));
		ivtUpdate.addIngredient(new Ingredient("Milk", 3));
		ivtUpdate.addIngredient(new Ingredient("Sugar", 4));

		try {
			mvc.perform(put("/api/v1/inventory").contentType(MediaType.APPLICATION_JSON)
					.content(TestUtils.asJsonString(ivtUpdate))).andExpect(status().isOk());
			String newIvt = mvc.perform(get("/api/v1/inventory")).andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();
			Assertions.assertTrue(newIvt.contains("\"chocolate\",\"quantity\":2"));
			Assertions.assertTrue(newIvt.contains("\"coffee\",\"quantity\":3"));
			Assertions.assertTrue(newIvt.contains("\"milk\",\"quantity\":4"));
			Assertions.assertTrue(newIvt.contains("\"sugar\",\"quantity\":5"));
		} catch (Exception e) {
			e.printStackTrace();
			Assertions.fail();
		}
	}

	/**
	 * Tests that adding a duplicate Ingredient to the Inventory is handled.
	 * 
	 * @throws Exception if POST fails
	 */
	@Test
	@Transactional
	public void testDuplicateIngredientAPI() throws Exception {
		invService.deleteAll();

		final Ingredient ingredient1 = new Ingredient("Marshmallow cream", 2);
		final Ingredient ingredient2 = new Ingredient("Marshmallow cream", 4);

		mvc.perform(
				post(String.format(formatString, ingredient1.getName(), ingredient1.getQuantity()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.asJsonString(ingredient1)))
				.andExpect(status().isOk());
		mvc.perform(
				post(String.format(formatString, ingredient2.getName(), ingredient2.getQuantity()))
						.contentType(MediaType.APPLICATION_JSON)
						.content(TestUtils.asJsonString(ingredient2)))
				.andExpect(status().isConflict());
	}
}
