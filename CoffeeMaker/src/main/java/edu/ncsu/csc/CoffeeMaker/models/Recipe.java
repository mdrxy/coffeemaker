package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate libraries. See
 * RecipeRepository and RecipeService for the other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

	/**
	 * Store a collection of Ingredients.
	 * 
	 * `OneToMany`: For each entity of Recipe, there can be a collection of Ingredients as a field.
	 * 
	 * CascadeType.ALL: This states that any actions that are performed (ie, saves, deletes, etc) on
	 * this Recipe object should be automatically performed (cascaded) onto the referenced
	 * Ingredient objects. This way, saving the Recipe saves all of its Ingredients, exactly like we
	 * want.
	 * 
	 * FetchType.EAGER: This states that when we load in this Recipe object from the database, all
	 * Ingredients associated with it should be loaded in too. (Alternative: lazy loading).
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Ingredient> ingredients;

	/**
	 * Recipe id.
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Recipe name.
	 */
	private String name;

	/** Recipe price */
	@Min(0)
	private Integer price;

	/**
	 * BLANK CONSTRUCTOR.
	 * 
	 * Creates a default Recipe for the CoffeeMaker.
	 */
	public Recipe() {
		this.name = "";
		this.ingredients = new ArrayList<>(); // Needed to prevent NPEs
	}

	/**
	 * Set the ID of the Recipe (Used by Hibernate).
	 *
	 * @param id the ID
	 */
	@SuppressWarnings("unused")
	private void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Get the ID of the Recipe.
	 *
	 * @return the ID
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Sets the Recipe name.
	 * 
	 * @param name The name to set. Must not exceed 30 characters in length
	 * 
	 * @return Whether the name was set
	 */
	public boolean setName(final String name) {
		if (1 > name.trim().length()) {
			throw new IllegalArgumentException("Length of Recipe name must be a positive integer");
		} else if (name.trim().length() > 30) {
			throw new IllegalArgumentException(
					"Length of Recipe name must not exceed 30 characters");
		} else {
			this.name = name.trim().toLowerCase();
			return true;
		}
	}

	/**
	 * Returns name of the Recipe.
	 *
	 * @return Returns the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the price of the Recipe.
	 *
	 * @return Returns the price
	 */
	public Integer getPrice() {
		return price;
	}

	/**
	 * Sets the Recipe price.
	 *
	 * @param price The price to set
	 * 
	 * @return true if successful, false if not
	 */
	public boolean setPrice(final Integer price) {
		if (price < 1) {
			throw new IllegalArgumentException("Price must be a positive integer");
		} else if (price > 1000) {
			throw new IllegalArgumentException("Price must not exceed 1000");
		} else {
			this.price = price;
			return true;
		}
	}

	/**
	 * Add an Ingredient to this Recipe's list of Ingredients.
	 * 
	 * Quantity of an Ingredient must be a positive integer no greater than 1000.
	 * 
	 * Validation not necessary since it is done in Ingredient.
	 * 
	 * @param ingredient Ingredient to add
	 * 
	 * @return true if successful, false if not
	 */
	public boolean addIngredient(Ingredient ingredient) {
		if (null != ingredient) {
			this.ingredients.add(ingredient);

			return true;
		}

		return false;
	}

	/**
	 * Remove an ingredient from this Recipe's list of Ingredients.
	 * 
	 * @param ingredient Ingredient to remove
	 */
	public void removeIngredient(Ingredient ingredient) {
		if (this.ingredients.size() > 1) {
			this.ingredients.remove(ingredient);
		} else {
			throw new IllegalArgumentException("Recipe must have at least one ingredient");
		}
	}

	/**
	 * Add a list of Ingredients to this Recipe's list of Ingredients.
	 * 
	 * @param ingredients Ingredients to add
	 * 
	 * @return true if all Ingredients were successfully added
	 */
	public boolean addIngredients(ArrayList<Ingredient> ingredients) {
		for (Ingredient ingredient : ingredients) {
			if (!addIngredient(ingredient)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get all Ingredients this Recipe contains.
	 * 
	 * @return List of Ingredients
	 */
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	/**
	 * Get a specific Ingredients that this Recipe contains, if it exists.
	 * 
	 * @param name Ingredient name to get
	 * 
	 * @return Matched Ingredient
	 */
	public Ingredient getIngredient(String name) {
		for (Ingredient ingredient : this.ingredients) {
			if (name.trim().toLowerCase().equals(ingredient.getName())) {
				return ingredient;
			}
		}

		return null;
	}

	/**
	 * Returns a String representation of this Recipe including all fields and nested fields
	 * (Ingredient names).
	 * 
	 * Format: {recipeName}: \nPrice: {price int as String}\nIngredients: {ingredientA, ingredientB,
	 * ...}
	 *
	 * @return String representation
	 */
	@Override
	public String toString() {
		String recipeName = this.getName() + ":\n";
		String priceString = "Price: " + this.getPrice().toString() + "\n";

		String ingredientNames = "Ingredients: ";

		for (int i = 0; i < ingredients.size(); i++) {
			ingredientNames += ingredients.get(i).getName() + ": "
					+ ingredients.get(i).getQuantity();

			if (i != ingredients.size()) {
				ingredientNames += ", ";
			}
		}

		return recipeName + priceString + ingredientNames;
	}

	/**
	 * Hash value of this Recipe object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		Integer result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());

		return result;
	}

	/**
	 * Whether this object is the same as another, checking field values.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final Recipe other = (Recipe) obj;

		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}

		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}

		if (this.price == null) {
			if (other.price != null) {
				return false;
			}
		} else if (!this.price.equals(other.price)) {
			return false;
		}

		List<Ingredient> otherIngredients = other.getIngredients();

		if (otherIngredients.size() != this.ingredients.size()) {
			return false;
		}

		for (Ingredient otherIngredient : otherIngredients) {
			boolean ingredientFound = false;
			for (Ingredient localIngredient : this.ingredients) {
				// If the Ingredient exists in the local Recipe and has the same quantity
				if (localIngredient.getName().equals(otherIngredient.getName())
						&& localIngredient.getQuantity() == otherIngredient.getQuantity()) {
					ingredientFound = true;
				}
			}

			// If not found, then the Recipes are not equal
			if (!ingredientFound) {
				return false;
			}
		}

		// All checks are passed
		return true;
	}
}
