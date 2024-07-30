package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * This is the controller that holds the REST endpoints that handle add and update operations for
 * the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results to JSON.
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@RestController
public class APIInventoryController extends APIController {

	/**
	 * InventoryinvService object, to be autowired in by Spring to allow for manipulating the
	 * Inventory model.
	 */
	@Autowired
	private InventoryService invService;

	/**
	 * REST API endpoint to provide GET access to the CoffeeMaker's singleton Inventory. This will
	 * convert the Inventory to JSON.
	 *
	 * @return response to the request
	 */
	@GetMapping(BASE_PATH + "/inventory")
	public ResponseEntity getInventory() {
		final Inventory inventory = invService.getInventory();
		return new ResponseEntity(inventory, HttpStatus.OK);
	}

	/**
	 * REST API method to provide POST access to create add a new Ingredient to the Inventory using
	 * URL params. Verifies that the Ingredient doesn't already exist.
	 *
	 * @param name The name of the ingredient to create
	 * 
	 * @return ResponseEntity indicating success if the Ingredient could be saved to the Inventory,
	 *         or an error if it could not be
	 */
	@PostMapping(BASE_PATH + "/inventory/{name}")
	public ResponseEntity createIngredient(@PathVariable("name") final String name) {
		final Inventory inventory = invService.getInventory();

		if (inventory.getIngredient(name.trim().toLowerCase()) != null) {
			return new ResponseEntity(
					errorResponse(name.trim().toLowerCase() + " already exists in the Inventory"),
					HttpStatus.CONFLICT);
		} else {
			Ingredient newIngr = new Ingredient(name.trim().toLowerCase(), 1);
			inventory.addIngredient(newIngr);
			invService.save(inventory);

			return new ResponseEntity(successResponse(newIngr.getName() + " successfully created"),
					HttpStatus.OK);
		}

	}

	/**
	 * REST API endpoint to provide update access to CoffeeMaker's singleton Inventory. This will
	 * update the Inventory of the CoffeeMaker by ADDING amounts from the Inventory provided to the
	 * CoffeeMaker's stored Inventory.
	 *
	 * @param inventory amounts to add to Inventory
	 * 
	 * @return response to the request
	 */
	@PutMapping(BASE_PATH + "/inventory")
	public ResponseEntity updateInventory(@RequestBody final Inventory inventory) {

		Inventory currentInventory = invService.getInventory();

		List<Ingredient> ingredients = inventory.getIngredients();
		for (Ingredient ingredient : ingredients) {
			int quantity = ingredient.getQuantity();

			currentInventory.addQuantity(ingredient.getName().trim().toLowerCase(), quantity);
		}

		invService.save(currentInventory);
		return new ResponseEntity(inventory, HttpStatus.OK);
	}

	/**
	 * REST API method to allow clearing all Ingredients from the CoffeeMaker's Inventory, by making
	 * a DELETE request to the API endpoint.
	 * 
	 * deleteAll on InventoryService deletes all Inventory entries, not the Ingredients inside the
	 * Inventory. This means I need to save the Inventory after deleting from it.
	 * 
	 * @return Success if the Ingredients could be deleted
	 */
	@DeleteMapping(BASE_PATH + "/inventory")
	public ResponseEntity clearInventory() {
		Inventory inv = invService.getInventory();
		inv.clearInventory();
		invService.save(inv);

		return new ResponseEntity(successResponse("Inventory was successfully cleared"),
				HttpStatus.OK);
	}
}
