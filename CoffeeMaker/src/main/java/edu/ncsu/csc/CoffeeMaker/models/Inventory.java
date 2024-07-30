package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using Hibernate libraries. See
 * InventoryRepository and InventoryService for the other two pieces used for database support.
 * 
 * Inventory should contain a list of Ingredients, wherein an Ingredient has a name and quantity.
 * These are different objects than what a Recipe stores, which each has a local copy of an
 * Ingredient with a quantity to reflect the amount necessary for that Recipe.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

	/**
	 * ID for Inventory entry.
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Store a collection of Ingredients.
	 * 
	 * `OneToMany`: For an Inventory, there can be a collection of Ingredients as a field.
	 * 
	 * CascadeType.ALL: This states that any actions that are performed (ie, saves, deletes, etc) on
	 * this Inventory object should be automatically performed (cascaded) onto the referenced
	 * Ingredient objects. This way, saving the Inventory saves all of its Ingredients, exactly like
	 * we want.
	 * 
	 * FetchType.EAGER: This states that when we load in this Inventory object from the database,
	 * all Ingredients associated with it should be loaded in too. (Alternative: lazy loading).
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Ingredient> ingredients;

	/**
	 * Empty constructor for Hibernate.
	 */
	public Inventory() {
		// Intentionally empty so that Hibernate can instantiate.
		this.ingredients = new ArrayList<>(); // Needed to prevent NPEs
	}

	/**
	 * Set the ID of the Inventory (Used by Hibernate).
	 *
	 * @param id the ID
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * Returns the ID of the entry in the DB.
	 *
	 * @return long
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Setter for the Ingredients list of the Inventory.
	 * 
	 * @param ingredients the Ingredients to set
	 * 
	 * @return true if successful, false if not
	 */
	public boolean setIngredients(List<Ingredient> ingredients) {
		if (ingredients.size() > 0) {
			this.ingredients = ingredients;

			return true;
		}

		return false;
	}

	/**
	 * Returns true if there are enough Ingredients to make the beverage.
	 *
	 * @param r Recipe to check if there are enough Ingredients
	 * 
	 * @return true if enough Ingredients to make the beverage
	 */
	public boolean enoughIngredients(final Recipe r) {
		for (Ingredient ingredient : r.getIngredients()) {
			if (this.getIngredient(ingredient.getName()) == null) {
				// Ingredient doesn't exist in Inventory
				return false;
			} else if (!(this.getIngredient(ingredient.getName()).getQuantity() >= ingredient
					.getQuantity())) {
				// If the amount in Inventory is less than the amount the Recipe needs
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns an Ingredient object based on its name, if it exists in the Inventory.
	 * 
	 * @param name Ingredient name to search for
	 * 
	 * @return The Ingredient if found
	 */
	public Ingredient getIngredient(String name) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getName().equals(name.trim().toLowerCase())) {
				return ingredient;
			}
		}

		return null;
	}

	/**
	 * Getter for the Ingredients list of the Inventory
	 * 
	 * @return the Ingredients
	 */
	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	/**
	 * Removes the Ingredients used to make the specified Recipe. Assumes that the user has checked
	 * that there are enough Ingredients to make.
	 *
	 * @param r Recipe to make
	 * 
	 * @return true if Recipe is made.
	 */
	public boolean useIngredients(final Recipe r) {
		if (enoughIngredients(r)) {
			for (Ingredient ingredient : r.getIngredients()) {
				this.useIngredient(ingredient.getName(), ingredient.getQuantity());
				// Don't need to check whether it was used since it is guaranteed to via
				// enoughIngredients
			}

			return true;
		} else {
			return false; // Not enough Ingredients
		}
	}

	/**
	 * Helper for useIngredients. Removes an amt of Inventory for a specific ingredient. When used
	 * in useIngredients, removes the ingredient amts that the Recipe uses.
	 * 
	 * Doesn't need to return boolean since it is called after verifying enough Ingredients.
	 * 
	 * @param name    the name of the Ingredient to use
	 * @param amtUsed the amt to use
	 */
	public void useIngredient(String name, int amtUsed) {
		for (Ingredient ingredient : this.ingredients) {
			if (ingredient.getName().equals(name)) {
				int endAmt = ingredient.getQuantity() - amtUsed;

				if (endAmt >= 0) {
					ingredient.setQuantity(endAmt);
				}
			}
		}
	}

	/**
	 * Adds an ingredient to the Inventory if the Inventory does not already contain it.
	 * 
	 * @param ingredient the ingredient to add
	 * 
	 * @return true if successful, false if not
	 */
	public boolean addIngredient(Ingredient ingredient) {
		if (ingredient != null) {
			if (this.ingredients.size() == 0 || this.getIngredient(ingredient.getName()) == null) {
				this.ingredients.add(ingredient);
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds more Inventory for a specified ingredient if it exists.
	 * 
	 * @param name     the name of the ingredient to add more of
	 * @param quantity how much more to add
	 * 
	 * @return true if successful, false if not
	 */
	public boolean addQuantity(String name, int quantity) {
		for (Ingredient ingredient : this.ingredients) {
			if (ingredient.getName().equals(name.trim().toLowerCase())) {
				if (quantity >= 1) {
					int amt = ingredient.getQuantity() + quantity;
					ingredient.setQuantity(amt);

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Sets the quantity of an existing Ingredient.
	 * 
	 * @param name     Name of Ingredient whose quantity is being updated
	 * @param quantity Quantity to set for the provided Ingredient name
	 * 
	 * @return true if successful, false if not
	 */
	public boolean setIngredient(String name, int quantity) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getName().equals(name.trim().toLowerCase())) {
				if (quantity >= 1) {
					ingredient.setQuantity(quantity);

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Remove an Ingredient from the Inventory.
	 * 
	 * @param name Name of Ingredient being removed
	 * 
	 * @return true if successful, false if not
	 */
	public boolean removeIngredient(String name) {
		for (Ingredient ingredient : ingredients) {
			if (ingredient.getName().equals(name.trim().toLowerCase())) {
				ingredients.remove(ingredient);

				return true;
			}
		}

		return false;
	}

	/**
	 * Removes all Ingredients from the Inventory.
	 */
	public void clearInventory() {
		ingredients.clear();
	}

	/**
	 * Returns a string describing the current contents of the Inventory.
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		for (Ingredient ingredient : this.ingredients) {
			buf.append(ingredient.getName() + ": " + ingredient.getQuantity() + "\n");
		}

		return buf.toString();
	}
}
