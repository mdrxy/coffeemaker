package edu.ncsu.csc.CoffeeMaker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Tests the MappingController via GET requests.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MappingControllerTest {

	/**
	 * Local MockMvc for POST, PUT, GET, DELETE requests.
	 */
	@Autowired
	private MockMvc mvc;

	/**
	 * GET index.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetIndex() throws Exception {
		String expectedChunk = "<title>Coffee Maker</title>";
		MvcResult result = mvc.perform(get("/index")).andExpect(status().isOk()).andReturn();
		String actualContent = result.getResponse().getContentAsString();
		Assertions.assertTrue(actualContent.contains(expectedChunk));
	}

	/**
	 * GET customrecipe.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetRecipe() throws Exception {
		String expectedRecipeChunk = "<title>Add Recipe</title>";
		MvcResult recipeResult = mvc.perform(get("/customrecipe")).andExpect(status().isOk())
				.andReturn();
		String actualRecipeContent = recipeResult.getResponse().getContentAsString();
		Assertions.assertTrue(actualRecipeContent.contains(expectedRecipeChunk));
	}

	/**
	 * GET deleterecipe.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetDeleteRecipe() throws Exception {
		String expectedChunk = "<title>Delete Recipes</title>";
		MvcResult result = mvc.perform(get("/deleterecipe")).andExpect(status().isOk()).andReturn();
		String actualContent = result.getResponse().getContentAsString();
		Assertions.assertTrue(actualContent.contains(expectedChunk));
	}

	/**
	 * GET makecoffee.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetMakeCoffee() throws Exception {
		String expectedChunk = "<label for=\"amtPaid\">Enter payment</label>";
		MvcResult result = mvc.perform(get("/makecoffee")).andExpect(status().isOk()).andReturn();
		String actualContent = result.getResponse().getContentAsString();
		Assertions.assertTrue(actualContent.contains(expectedChunk));
	}

	/**
	 * GET addingredient.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetAddIngredient() throws Exception {
		String expectedChunk = "<title>Add Ingredient</title>";
		MvcResult result = mvc.perform(get("/addingredient")).andExpect(status().isOk())
				.andReturn();
		String actualContent = result.getResponse().getContentAsString();
		Assertions.assertTrue(actualContent.contains(expectedChunk));
	}

	/**
	 * GET editrecipe.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetEditRecipe() throws Exception {
		String expectedChunk = "<title>Edit Recipe</title>";
		MvcResult result = mvc.perform(get("/editrecipe")).andExpect(status().isOk()).andReturn();
		String actualContent = result.getResponse().getContentAsString();
		Assertions.assertTrue(actualContent.contains(expectedChunk));
	}

	/**
	 * GET inventory.html and assert that it's returned with expected header.
	 */
	@Test
	public void testGetInventory() throws Exception {
		String expectedChunk = "<title>Update Inventory</title>";
		MvcResult result = mvc.perform(get("/inventory")).andExpect(status().isOk()).andReturn();
		String actualContent = result.getResponse().getContentAsString();
		Assertions.assertTrue(actualContent.contains(expectedChunk));
	}
}
