package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * Tests Inventory.java
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest(classes = TestConfig.class)
public class InventoryTest {

	/**
	 * Local InventoryService.
	 */
	@Autowired
	private InventoryService inventoryService;

	/**
	 * Updates the InventoryService with base values of 500 before each test.
	 */
	@BeforeEach
	public void setup() {
		final Inventory ivt = inventoryService.getInventory();

		ivt.addIngredient(new Ingredient("Chocolate", 500));
		ivt.addIngredient(new Ingredient("Coffee", 500));
		ivt.addIngredient(new Ingredient("Milk", 500));
		ivt.addIngredient(new Ingredient("Sugar", 500));

		inventoryService.save(ivt);
	}

	/**
	 * Tests setting a valid list of Ingredients.
	 */
	@Test
	@Transactional
	public void testSetValidInventory() {
		final Inventory i = inventoryService.getInventory();

		List<Ingredient> ingredients = new ArrayList<>();

		for (Ingredient ingr : i.getIngredients()) {
			ingredients.add(ingr);
		}

		Assertions.assertTrue(i.setIngredients(ingredients));
	}

	/**
	 * Tests setting an invalid list of Ingredients (empty).
	 */
	@Test
	@Transactional
	public void testSetInvalidInventory() {
		final Inventory i = inventoryService.getInventory();

		List<Ingredient> ingredients = new ArrayList<>();

		Assertions.assertFalse(i.setIngredients(ingredients));
	}

	/**
	 * Tests removing an Ingredient that doesn't exist in the Inventory.
	 */
	@Test
	@Transactional
	public void testRemoveInvalidIngredient() {
		final Inventory i = inventoryService.getInventory();

		Assertions.assertFalse(i.removeIngredient("Lemons"));
		Assertions.assertTrue(i.removeIngredient("Coffee"));
	}

	/**
	 * Tests adding a Null Ingredient to the Inventory.
	 */
	@Test
	@Transactional
	public void testAddInvalidIngredient() {
		final Inventory i = inventoryService.getInventory();

		Ingredient nullIng = null;
		Assertions.assertFalse(i.addIngredient(nullIng));
	}

	/**
	 * Tests adding quantity to the Inventory of an Ingredient that doesn't exist and one that does
	 * but with zero quantity.
	 */
	@Test
	@Transactional
	public void testUpdateInvalidIngredient() {
		final Inventory i = inventoryService.getInventory();

		Assertions.assertFalse(i.addQuantity("Lemon", 0));
		Assertions.assertFalse(i.addQuantity("Lemon", 1));
		Assertions.assertFalse(i.addQuantity("Coffee", 0));
	}

	/**
	 * Tests setting quantity of an Ingredient that doesn't exist in the Inventory and one that does
	 * but with zero quantity.
	 */
	@Test
	@Transactional
	public void testSetInvalidIngredient() {
		final Inventory i = inventoryService.getInventory();

		Assertions.assertFalse(i.setIngredient("Lemon", 0));
		Assertions.assertFalse(i.setIngredient("Lemon", 1));
		Assertions.assertFalse(i.setIngredient("Coffee", 0));
		Assertions.assertTrue(i.setIngredient("Coffee", 1));
	}

	/**
	 * Tests using Ingredients.
	 */
	@Test
	@Transactional
	public void testConsumeInventory() {
		final Inventory i = inventoryService.getInventory();

		final Recipe recipe = new Recipe();
		recipe.setName("Delicious Not-Coffee");
		recipe.addIngredient(new Ingredient("Chocolate", 10));
		recipe.addIngredient(new Ingredient("Milk", 20));
		recipe.addIngredient(new Ingredient("Sugar", 5));
		recipe.addIngredient(new Ingredient("Coffee", 1));
		recipe.setPrice(5);

		i.useIngredients(recipe);

		// Make sure that all of the Inventory fields are now properly updated
		Assertions.assertEquals(490, (int) i.getIngredient("Chocolate").getQuantity());
		Assertions.assertEquals(480, (int) i.getIngredient("Milk").getQuantity());
		Assertions.assertEquals(495, (int) i.getIngredient("Sugar").getQuantity());
		Assertions.assertEquals(499, (int) i.getIngredient("Coffee").getQuantity());
	}

	/**
	 * Tests not invalid scenarios where Ingredients are consumed.
	 */
	@Test
	@Transactional
	public void testInvalidConsumeIngredients() {
		final Inventory i = inventoryService.getInventory();

		// Insufficient quantity
		final Recipe recipeInvalid = new Recipe();
		recipeInvalid.setName("Overpriced Not-Coffee");
		recipeInvalid.addIngredient(new Ingredient("Chocolate", 501));
		recipeInvalid.addIngredient(new Ingredient("Milk", 501));
		recipeInvalid.addIngredient(new Ingredient("Sugar", 501));
		recipeInvalid.addIngredient(new Ingredient("Coffee", 501));
		recipeInvalid.setPrice(999);

		Assertions.assertFalse(i.useIngredients(recipeInvalid));

		// Ingredient doesn't exist
		final Recipe recipeValid = new Recipe();
		recipeValid.setName("Delicious Not-Coffee");
		recipeValid.addIngredient(new Ingredient("Chocolate", 501));
		recipeValid.addIngredient(new Ingredient("Milk", 20));
		recipeValid.addIngredient(new Ingredient("Sugar", 5));
		recipeValid.addIngredient(new Ingredient("Coffee", 1));
		recipeValid.setPrice(5);

		i.removeIngredient("Chocolate");

		Assertions.assertFalse(i.useIngredients(recipeValid));
	}

	/**
	 * Tests enough Ingredients in useIngredients().
	 */
	@Test
	@Transactional
	public void testEnoughIngredients() {
		final Inventory i = inventoryService.getInventory();

		final Recipe recipe = new Recipe();
		recipe.setName("Overpriced Not-Coffee");
		recipe.addIngredient(new Ingredient("Chocolate", 500));
		recipe.addIngredient(new Ingredient("Milk", 500));
		recipe.addIngredient(new Ingredient("Sugar", 500));
		recipe.addIngredient(new Ingredient("Coffee", 500));

		recipe.setPrice(999);

		Assertions.assertTrue(i.enoughIngredients(recipe));
	}

	/**
	 * Tests adding Ingredients.
	 */
	@Test
	@Transactional
	public void testAddInventory1() {
		Inventory ivt = inventoryService.getInventory();

		ivt.addQuantity("Coffee", 5);
		ivt.addQuantity("Milk", 3);
		ivt.addQuantity("Sugar", 7);
		ivt.addQuantity("Chocolate", 2);

		// Save and retrieve again to update with DB
		inventoryService.save(ivt);

		ivt = inventoryService.getInventory();

		Assertions.assertEquals(505, (int) ivt.getIngredient("Coffee").getQuantity(),
				"Adding to the Inventory should result in correctly-updated values for coffee");
		Assertions.assertEquals(503, (int) ivt.getIngredient("Milk").getQuantity(),
				"Adding to the Inventory should result in correctly-updated values for milk");
		Assertions.assertEquals(507, (int) ivt.getIngredient("Sugar").getQuantity(),
				"Adding to the Inventory should result in correctly-updated values sugar");
		Assertions.assertEquals(502, (int) ivt.getIngredient("Chocolate").getQuantity(),
				"Adding to the Inventory should result in correctly-updated values chocolate");
	}

	/**
	 * Tests invalid adding Ingredients.
	 */
	@Test
	@Transactional
	public void testAddInventory2() {
		final Inventory ivt = inventoryService.getInventory();

		try {
			ivt.addQuantity("Coffee", -5);

		} catch (final IllegalArgumentException iae) {
			Assertions.assertEquals(500, (int) ivt.getIngredient("Coffee").getQuantity(),
					"Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee");
		}
	}

	/**
	 * More invalid adding Inventory tests.
	 */
	@Test
	@Transactional
	public void testAddInventory3() {
		final Inventory ivt = inventoryService.getInventory();

		try {
			ivt.addQuantity("Milk", -3);
		} catch (final IllegalArgumentException iae) {
			Assertions.assertEquals(500, (int) ivt.getIngredient("Milk").getQuantity(),
					"Trying to update the Inventory with an invalid value for milk should result in no changes -- milk");
		}
	}

	/**
	 * Tests toString.
	 */
	@Test
	@Transactional
	public void testToString() {
		final Inventory ivt = inventoryService.getInventory();

		String inventoryString = ivt.toString();

		// Assert: Check if the string representation is as expected
		String expectedString = "chocolate: 500\n" + "coffee: 500\n" + "milk: 500\n"
				+ "sugar: 500\n";
		Assertions.assertEquals(expectedString, inventoryString);
	}

	/**
	 * Tests setId.
	 */
	@Test
	@Transactional
	public void testSetId() {
		final Inventory ivt = inventoryService.getInventory();
		long id = 5;

		ivt.setId(id);
		Assertions.assertEquals(id, ivt.getId());
	}
}
