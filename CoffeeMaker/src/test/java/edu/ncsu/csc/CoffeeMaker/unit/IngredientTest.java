package edu.ncsu.csc.CoffeeMaker.unit;

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
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * Tests Ingredient.java
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest(classes = TestConfig.class)
public class IngredientTest {

	/**
	 * Local InventoryService.
	 */
	@Autowired
	private InventoryService inventoryService;

	/**
	 * Updates the Inventory service with base values of 500 before each test.
	 */
	@BeforeEach
	public void setup() {
		inventoryService.deleteAll();
		final Inventory ivt = inventoryService.getInventory();

		ivt.addIngredient(new Ingredient("Chocolate", 500));
		ivt.addIngredient(new Ingredient("Coffee", 500));
		ivt.addIngredient(new Ingredient("Milk", 500));
		ivt.addIngredient(new Ingredient("Sugar", 500));

		inventoryService.save(ivt);
	}

	/**
	 * Tests adding Ingredient toString().
	 */
	@Test
	@Transactional
	public void testIngredientToString() {
		Ingredient ingr = inventoryService.getInventory().getIngredient("chocolate");
		String ingrString = ingr.toString();
		Assertions.assertTrue(ingrString.contains("ingredient=chocolate, amount=500"));
	}

	/**
	 * Tests adding invalid Ingredients.
	 */
	@Test
	@Transactional
	public void testSetInvalidIngredientParams() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final Ingredient ingr = new Ingredient("", 3);
			Assertions.assertNotNull(ingr);
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final Ingredient ingr = new Ingredient("1234567890123456789012345678901", 3);
			Assertions.assertNotNull(ingr);
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final Ingredient ingr = new Ingredient("", 0);
			Assertions.assertNotNull(ingr);
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final Ingredient ingr = new Ingredient("Chocolate", 3);
			ingr.setQuantity(1001);
			Assertions.assertNotNull(ingr);
		});

		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			final Ingredient ingr = new Ingredient("", 1001);
			Assertions.assertNotNull(ingr);
		});
	}
}
