package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * Endpoints for manipulating Ingredients.
 * 
 * `@RestController` : This is used to indicate to Spring, "this is a class where I want to keep
 * REST API endpoints" and that helps it know that parameters to/from these methods should be
 * automatically serialized/deserialized.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@RestController
public class APIIngredientController extends APIController {

	/**
	 * IngredientService object, to be autowired in by Spring to allow for manipulating the
	 * Ingredient model.
	 */
	@Autowired
	private IngredientService ingService;

	/**
	 * REST APImethod to provide GET access to all Ingredients in the system.
	 * 
	 * @return JSON representation of Ingredients
	 */
	@GetMapping(BASE_PATH + "/ingredients")
	public List<Ingredient> getIngredients() {
		return ingService.findAll();
	}

	/**
	 * REST API method to provide GET access to a specific ingredient, as indicated by the path
	 * variable provided.
	 * 
	 * @param name The name of the Ingredient
	 * 
	 * @return Response to the request
	 */
	@GetMapping(BASE_PATH + "/ingredients/{name}")
	public ResponseEntity getIngredient(@PathVariable("name") final String name) {
		final Ingredient ingredient = ingService.findByName(name.toLowerCase());

		return null == ingredient
				? new ResponseEntity(
						errorResponse("No Recipe found with name " + name.toLowerCase()),
						HttpStatus.NOT_FOUND)
				: new ResponseEntity(
						successResponse(ingredient.getName() + " successfully retrieved"),
						HttpStatus.OK);
	}

	/**
	 * REST API method to provide POST access to create a new Ingredient model using URL params.
	 * 
	 * Don't check whether the Ingredient already exists, since Recipes take these local copies.
	 *
	 * @param name     The name of the Ingredient to create
	 * @param quantity Amount of Ingredient
	 * 
	 * @return ResponseEntity indicating success if the Ingredient could be saved to the Inventory,
	 *         or an error if it could not be
	 */
	@PostMapping(BASE_PATH + "/ingredients/{name}")
	public ResponseEntity createIngredient(@PathVariable("name") final String name,
			@RequestParam(defaultValue = "1") final String quantity) {
		try {
			Ingredient newIngr = new Ingredient(name.trim().toLowerCase(),
					Integer.parseInt(quantity));
			ingService.save(newIngr);

			return new ResponseEntity(successResponse(newIngr.getName() + " successfully created"),
					HttpStatus.OK);
		} catch (Exception NumberFormatException) {
			// Can't parse quantity as an integer
			return new ResponseEntity(errorResponse("Quantity must be a decimal integer"),
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * REST API method to allow deleting an Ingredient from the CoffeeMaker's Inventory, by making a
	 * DELETE request to the API endpoint and indicating the ingredient to delete (as a path
	 * variable).
	 *
	 * @param name The name of the Ingredient to delete
	 * 
	 * @return Success if the Ingredient could be deleted; an error if the Ingredient does not exist
	 */
	@DeleteMapping(BASE_PATH + "/ingredients/{name}")
	public ResponseEntity deleteIngredient(@PathVariable final String name) {
		final Ingredient ingredient = ingService.findByName(name.toLowerCase());

		if (null == ingredient) {
			return new ResponseEntity(
					errorResponse("No Recipe found for name " + name.toLowerCase()),
					HttpStatus.NOT_FOUND);
		}

		ingService.delete(ingredient);

		return new ResponseEntity(
				successResponse(ingredient.getName() + " was deleted successfully"), HttpStatus.OK);
	}
}
