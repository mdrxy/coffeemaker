package edu.ncsu.csc.CoffeeMaker;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests various database interactions.
 */
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration
@SpringBootTest(classes = TestConfig.class)

public class TestDatabaseInteraction {

	/**
	 * Local RecipeService.
	 */
	@Autowired
	private RecipeService recipeService;

	/**
	 * Saving, Inventory size, edits, deletion.
	 */
	@Test
	@Transactional
	public void testRecipes() {
		recipeService.deleteAll(); // Clean slate

		// Create a new Recipe
		Recipe mocha = new Recipe();
		mocha.setName("Mocha");
		mocha.setPrice(725);
		mocha.addIngredient(new Ingredient("Chocolate", 2));
		mocha.addIngredient(new Ingredient("Coffee", 1));
		mocha.addIngredient(new Ingredient("Milk", 1));
		mocha.addIngredient(new Ingredient("Sugar", 10));
		recipeService.save(mocha);

		List<Ingredient> ingredients = mocha.getIngredients();
		Ingredient chocolate = ingredients.get(0);
		Ingredient coffee = ingredients.get(1);
		Ingredient milk = ingredients.get(2);
		Ingredient sugar = ingredients.get(3);

		// Test findAll
		List<Recipe> dbRecipes = (List<Recipe>) recipeService.findAll();
		Assertions.assertEquals(1, dbRecipes.size());
		Recipe dbRecipe = dbRecipes.get(0);
		Assertions.assertEquals(mocha.getName(), dbRecipe.getName());
		Assertions.assertEquals(mocha.getPrice(), dbRecipe.getPrice());

		// Retrieve Ingredients from the found Recipe
		List<Ingredient> dbIngredients = dbRecipe.getIngredients();
		Assertions.assertEquals(chocolate, dbIngredients.get(0));
		Assertions.assertEquals(coffee, dbIngredients.get(1));
		Assertions.assertEquals(milk, dbIngredients.get(2));
		Assertions.assertEquals(sugar, dbIngredients.get(3));

		// Test findByName
		Recipe findRecipe = recipeService.findByName(mocha.getName());
		Assertions.assertEquals(findRecipe.getName(), mocha.getName());

		// Verifying that we can edit the Recipe
		dbRecipe.setPrice(120);
		Ingredient ingredient3 = dbIngredients.get(3);
		ingredient3.setQuantity(8);
		recipeService.save(dbRecipe);

		List<Recipe> dbRecipesNew = (List<Recipe>) recipeService.findAll();
		Assertions.assertEquals(1, dbRecipesNew.size());
		Recipe dbRecipeNew = dbRecipesNew.get(0);

		Assertions.assertEquals(mocha.getName(), dbRecipeNew.getName());
		Assertions.assertEquals(120, dbRecipeNew.getPrice());
		Assertions.assertEquals(chocolate, dbIngredients.get(0));
		Assertions.assertEquals(coffee, dbIngredients.get(1));
		Assertions.assertEquals(milk, dbIngredients.get(2));
		Assertions.assertEquals(8, dbIngredients.get(3).getQuantity());

		// Verifying that we can delete a Recipe:
		Recipe americano = new Recipe();
		americano.setName("Americano");
		americano.setPrice(900);
		americano.addIngredient(new Ingredient("Coffee", 5));
		americano.addIngredient(new Ingredient("Milk", 5));
		recipeService.save(americano);

		List<Recipe> recipesList = (List<Recipe>) recipeService.findAll();
		Assertions.assertEquals(2, recipesList.size());

		recipeService.delete(americano);
		recipesList = (List<Recipe>) recipeService.findAll();
		Assertions.assertEquals(1, recipesList.size());
		Assertions.assertEquals(null, recipeService.findByName("Americano"));
	}
}
