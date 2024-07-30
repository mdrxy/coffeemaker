package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

/**
 * Ingredient for a Recipe.
 */
@Entity
public class Ingredient extends DomainObject {

	/**
	 * Serves as the primary key for the database to reference things so that we can keep track of
	 * them and reference them from other tables later.
	 * 
	 * `@Id` tells Hibernate that we'll have the database generate this automatically by
	 * incrementing a numeric value. Common practice to let the DB generate its own IDs.
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Ingredient name.
	 */
	private String name;

	/**
	 * Quantity of Ingredient. Context dependent in the sense that Inventory uses this field to
	 * represent the amount of this Ingredient in the database, whereas Recipe uses it to mean how
	 * much of an Ingredient is necessary to make one Recipe item.
	 * 
	 * Minimum of 0, since these items should only exist as positive integers in the system.
	 */
	@Min(0)
	private int quantity;

	/**
	 * Blank constructor for Hibernate to use when loading objects from the database.
	 */
	public Ingredient() {
		super();
	}

	/**
	 * Create an Ingredient with a defined name and quantity.
	 * 
	 * @param name     Ingredient name
	 * @param quantity Ingredient quantity - recall that this is context dependent
	 */
	public Ingredient(String name, Integer quantity) {
		super();
		setName(name);
		setQuantity(quantity);
	}

	/**
	 * Create an Ingredient with a defined name but not quantity.
	 * 
	 * @param name Ingredient name
	 */
	public Ingredient(String name) {
		super();
		setName(name);
		setQuantity(1);
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
	 * Get the ID of the Ingredient.
	 */
	@Override
	public Serializable getId() {
		return id;
	}

	/**
	 * Set the Ingredient's name.
	 * 
	 * @param name The name to set. Must not exceed 30 characters in length
	 * 
	 * @return Whether the name was set
	 */
	private boolean setName(String name) {
		if (name.isBlank()) {
			throw new IllegalArgumentException("Name must not be blank");
		} else if (name.trim().length() > 30) {
			throw new IllegalArgumentException("Name must not exceed 30 characters");
		} else {
			this.name = name.trim().toLowerCase();
			return true;
		}
	}

	/**
	 * Get the Ingredient's name.
	 * 
	 * @return Ingredient name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the Ingredient's quantity. Must be a positive integer.
	 * 
	 * @param quantity int quantity to set
	 * 
	 * @return true if successful, false if not
	 */
	public boolean setQuantity(int quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity must be a positive integer");
		} else if (quantity > 1000) {
			throw new IllegalArgumentException("Quantity must not exceed 1000");
		} else {
			this.quantity = quantity;
			return true;
		}
	}

	/**
	 * Get the Ingredient's quantity.
	 * 
	 * @return quantity Ingredient's quantity
	 */
	public int getQuantity() {
		return this.quantity;
	}

	/**
	 * Ingredient's String representation.
	 */
	@Override
	public String toString() {
		return "Ingredient [id=" + id + ", ingredient=" + this.getName() + ", amount="
				+ this.getQuantity() + "]";
	}
}
