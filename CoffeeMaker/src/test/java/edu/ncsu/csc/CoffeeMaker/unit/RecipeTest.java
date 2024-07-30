package edu.ncsu.csc.CoffeeMaker.unit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests Recipe.java
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest(classes = TestConfig.class)
public class RecipeTest {

	/**
	 * Local RecipeService.
	 */
	@Autowired
	private RecipeService service;

	/**
	 * Initialize a test.
	 */
	@BeforeEach
	public void setup() {
		service.deleteAll();
	}

	/**
	 * Private helper.
	 * 
	 * @param name   Name of Recipe to make
	 * @param price  Price of Recipe being created
	 * @param coffee Amount of Coffee to add to Recipe
	 * @param milk   Amount of Milk to add to Recipe
	 * @param sugar  Amount of Sugar to add to Recipe
	 * 
	 * @return The Recipe created
	 */
	private Recipe createRecipe(final String name, final Integer price, final Integer coffee,
			final Integer milk, final Integer sugar) {
		final Recipe recipe = new Recipe();
		recipe.setName(name);
		recipe.setPrice(price);

		recipe.addIngredient(new Ingredient("Coffee", coffee));
		recipe.addIngredient(new Ingredient("Milk", milk));
		recipe.addIngredient(new Ingredient("Sugar", sugar));

		return recipe;
	}

	/**
	 * Tests adding a Recipe.
	 */
	@Test
	@Transactional
	public void testAddRecipe() {
		final Recipe r1 = new Recipe();
		r1.setName("Black Coffee");
		r1.setPrice(1);
		r1.addIngredient(new Ingredient("Coffee", 1));

		service.save(r1);

		final Recipe r2 = new Recipe();
		r2.setName("Mocha");
		r2.setPrice(1);
		r2.addIngredient(new Ingredient("Coffee", 1));
		r2.addIngredient(new Ingredient("Milk", 1));
		r2.addIngredient(new Ingredient("Sugar", 1));
		r2.addIngredient(new Ingredient("Chocolate", 1));
		service.save(r2);

		final List<Recipe> recipes = service.findAll();
		Assertions.assertEquals(2, recipes.size(),
				"Creating two Recipes should result in two Recipes in the database");

		Assertions.assertEquals(r1, recipes.get(0),
				"The retrieved Recipe should match the created one");
	}

	/**
	 * Tests adding one Recipe.
	 */
	@Test
	@Transactional
	public void testAddOneRecipe() {

		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");
		final String name = "Coffee";
		final Recipe r1 = createRecipe(name, 50, 3, 1, 1);

		service.save(r1);

		Assertions.assertEquals(1, service.findAll().size(),
				"There should only one Recipe in the CoffeeMaker");
		Assertions.assertNotNull(service.findByName(name));
	}

	/* Test2 is done via the API for different validation */

	/**
	 * Tests invalid Recipes.
	 */
	@Test
	@Transactional
	public void testSetInvalidRecipeParams() {
		final Recipe recipe = new Recipe();
		recipe.setName("Cough-ee");

		// Invalid Recipe fields
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.setName("");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.setName("1234567890123456789012345678901");
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.setPrice(0);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.setPrice(-1);
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.setPrice(1001);
		});

		// Adding invalid Ingredients shouldn't fly
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.addIngredient(new Ingredient("Coffee", 0));
		});
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			recipe.addIngredient(new Ingredient("Coffee", -1));
		});

		Ingredient nullIngr = null;
		Assert.assertFalse(recipe.addIngredient(nullIngr));

		ArrayList<Ingredient> badIngrList = new ArrayList<Ingredient>();
		badIngrList.add(nullIngr);

		Assert.assertFalse(recipe.addIngredients(badIngrList));
	}

	/**
	 * Tests adding two Recipe objects.
	 */
	@Test
	@Transactional
	public void testAddRecipe13() {
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");

		final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1);
		service.save(r1);
		final Recipe r2 = createRecipe("Mocha", 50, 3, 1, 1);
		service.save(r2);

		Assertions.assertEquals(2, service.count(),
				"Creating two Recipes should result in two Recipes in the database");
	}

	/**
	 * Tests deleting a Recipe.
	 */
	@Test
	@Transactional
	public void testDeleteRecipe1() {
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");

		final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1);
		service.save(r1);

		Assertions.assertEquals(1, service.count(), "There should be one Recipe in the database");

		service.delete(r1);
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");
	}

	/**
	 * Tests deleting all Recipe objects.
	 */
	@Test
	@Transactional
	public void testDeleteRecipe2() {
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");

		final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1);
		service.save(r1);
		final Recipe r2 = createRecipe("Mocha", 50, 3, 1, 1);
		service.save(r2);
		final Recipe r3 = createRecipe("Latte", 60, 3, 2, 2);
		service.save(r3);

		Assertions.assertEquals(3, service.count(),
				"There should be three Recipes in the database");

		service.deleteAll();

		Assertions.assertEquals(0, service.count(),
				"`service.deleteAll()` should remove everything");
	}

	/**
	 * Tests whether two Recipe objects are the same.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	@Transactional
	public void testEqualRecipes() {
		final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1);
		final Recipe r2 = createRecipe("Coffee", 50, 3, 1, 1);

		Assertions.assertTrue(r1.equals(r2));

		final Recipe r3 = createRecipe("Mocha", 50, 3, 1, 1);
		Assertions.assertFalse(r1.equals(r3));

		Assertions.assertFalse(r1.equals(3));
		Assertions.assertFalse(r1.equals(null));
	}

	/**
	 * Tests adding Ingredients to a new Recipe.
	 */
	@Test
	@Transactional
	public void testAddIngredients() {
		final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1);
		List<Ingredient> ingredientsToAdd = new ArrayList<>();
		ingredientsToAdd.add(new Ingredient("Caramel", 2));
		r1.addIngredients((ArrayList<Ingredient>) ingredientsToAdd);

		List<Ingredient> ingredients = r1.getIngredients();

		Assertions.assertEquals("caramel", ingredients.get(3).getName());
		Assertions.assertEquals(2, ingredients.get(3).getQuantity());
	}

	/**
	 * Tests editing a Recipe's price and verifying other contents don't change.
	 */
	@Test
	@Transactional
	public void testEditRecipe1() {
		Assertions.assertEquals(0, service.findAll().size(),
				"There should be no Recipes in the CoffeeMaker");

		final Recipe r1 = createRecipe("Coffee", 50, 3, 1, 1);
		service.save(r1);

		r1.setPrice(70);

		service.save(r1);

		final Recipe retrieved = service.findByName("Coffee");

		Assertions.assertEquals(70, (int) retrieved.getPrice());

		List<Ingredient> ingredients = retrieved.getIngredients();

		Assertions.assertEquals(3, ingredients.get(0).getQuantity());
		Assertions.assertEquals(1, ingredients.get(1).getQuantity());
		Assertions.assertEquals(1, ingredients.get(2).getQuantity());

		Assertions.assertEquals(1, service.count(), "Editing a Recipe shouldn't duplicate it");
	}

	/**
	 * Tests Recipe toString().
	 */
	@Test
	@Transactional
	public void testRecipeToString() {
		final Recipe r1 = new Recipe();
		r1.setName("Black Coffee");
		r1.setPrice(1);
		r1.addIngredient(new Ingredient("Coffee", 1));

		service.save(r1);

		Recipe recipe = service.findByName("Black Coffee");
		String recipeString = recipe.toString();
		Assertions.assertEquals("black coffee:\n" + "Price: 1\n" + "Ingredients: coffee: 1, ",
				recipeString);
	}

	/**
	 * Tests Recipe hashCode().
	 */
	@Test
	@Transactional
	public void testRecipeHash() {
		final Recipe r1 = new Recipe();
		r1.setName("Black Coffee");
		r1.setPrice(1);
		r1.addIngredient(new Ingredient("Coffee", 1));

		service.save(r1);

		Recipe recipe = service.findByName("Black Coffee");
		int recipeHash = recipe.hashCode();
		Assertions.assertNotNull(recipeHash);
	}
}
