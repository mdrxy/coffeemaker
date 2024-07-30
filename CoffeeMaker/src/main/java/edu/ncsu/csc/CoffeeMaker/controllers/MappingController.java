package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns the approprate HTML
 * page in the /src/main/resources/templates folder. For a larger application, this should be split
 * across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

	/**
	 * On a GET request to /index, the IndexController will return
	 * /src/main/resources/templates/index.html.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/index", "/index.html", "/" })
	public String index(final Model model) {
		return "index";
	}

	/**
	 * On a GET request to /customrecipe, return /src/main/resources/templates/customrecipe.html.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/customrecipe", "/customrecipe.html" })
	public String customRecipeForm(final Model model) {
		return "customrecipe";
	}

	/**
	 * On a GET request to /deleterecipe, the DeleteRecipeController will return
	 * /src/main/resources/templates/deleterecipe.html.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/deleterecipe", "/deleterecipe.html" })
	public String deleteRecipeForm(final Model model) {
		return "deleterecipe";
	}

	/**
	 * On a GET request to /editrecipe, the EditRecipeController will return
	 * /src/main/resources/templates/editrecipe.html.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/editrecipe", "/editrecipe.html" })
	public String editRecipeForm(final Model model) {
		return "editrecipe";
	}

	/**
	 * Handles a GET request for Inventory. The GET request provides a view to the client that
	 * includes the list of the current Ingredients in the Inventory and a form where the client can
	 * enter more Ingredients to add to the Inventory.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/inventory", "/inventory.html" })
	public String inventoryForm(final Model model) {
		return "inventory";
	}

	/**
	 * On a GET request to /makecoffee, the MakeCoffeeController will return
	 * /src/main/resources/templates/makecoffee.html.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/makecoffee", "/makecoffee.html" })
	public String makeCoffeeForm(final Model model) {
		return "makecoffee";
	}

	/**
	 * On a GET request to /addingredient, return /src/main/resources/templates/addingredient.html.
	 *
	 * @param model underlying UI model
	 * 
	 * @return contents of the page
	 */
	@GetMapping({ "/addingredient", "/addingredient.html" })
	public String addIngredient(final Model model) {
		return "addingredient";
	}
}
